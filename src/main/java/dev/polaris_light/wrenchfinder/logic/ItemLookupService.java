package dev.polaris_light.wrenchfinder.logic;

import dev.polaris_light.wrenchfinder.config.HandActionMode;
import dev.polaris_light.wrenchfinder.containers.ContainerTrace;
import dev.polaris_light.wrenchfinder.WrenchFinder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public final class ItemLookupService {
    private ItemLookupService() {
    }

    public static void lookupAndEquip(ServerPlayer player, List<String> itemPatterns, HandActionMode actionMode, boolean notifyFailures) {
        if (itemPatterns.isEmpty()) {
            notifyFailure(player, notifyFailures, "message.wrenchfinder.no_rule");
            return;
        }

        ItemStack mainHand = player.getMainHandItem();
        if (matchesAny(mainHand, itemPatterns)) {
            return;
        }

        DirectMatch directMatch = findDirectMatch(player, itemPatterns);
        if (directMatch != null) {
            applyDirectMatch(player, directMatch);
            return;
        }

        int selectedSlot = getSelectedSlot(player);
        if (!mainHand.isEmpty() && !canStoreInInventory(player.getInventory(), mainHand.copy(), selectedSlot)) {
            notifyFailure(player, notifyFailures, "message.wrenchfinder.no_space");
            return;
        }

        ExternalMatch externalMatch = findExternalMatch(player, itemPatterns);
        if (externalMatch == null) {
            notifyFailure(player, notifyFailures, "message.wrenchfinder.not_found");
            return;
        }

        ItemStack extracted = extractFromExternal(player, externalMatch);
        if (extracted.isEmpty()) {
            notifyFailure(player, notifyFailures, "message.wrenchfinder.not_found");
            return;
        }

        ItemStack previousMainHand = mainHand.copy();
        player.setItemInHand(InteractionHand.MAIN_HAND, extracted);

        if (!previousMainHand.isEmpty()) {
            ItemStack remainder = previousMainHand.copy();
            if (!addToInventory(player.getInventory(), remainder, selectedSlot)) {
                // This path should be avoided by the pre-check, but keeps us safe if a storage changed mid-action.
                player.setItemInHand(InteractionHand.MAIN_HAND, previousMainHand);
                ItemStack rollback = extracted.copy();
                addToInventory(player.getInventory(), rollback, selectedSlot);
                notifyFailure(player, notifyFailures, "message.wrenchfinder.no_space");
                return;
            }
        }

        player.getInventory().setChanged();
        player.containerMenu.broadcastChanges();
        if (actionMode == HandActionMode.SWAP) {
            player.sendSystemMessage(Component.translatable("message.wrenchfinder.swap_fallback"));
        }
    }

    private static void applyDirectMatch(ServerPlayer player, DirectMatch match) {
        ItemStack mainHand = player.getMainHandItem().copy();

        if (match.slot == Inventory.SLOT_OFFHAND) {
            ItemStack found = player.getOffhandItem().copy();
            player.setItemInHand(InteractionHand.OFF_HAND, mainHand);
            player.setItemInHand(InteractionHand.MAIN_HAND, found);
        } else {
            ItemStack found = player.getInventory().getItem(match.slot).copy();
            player.getInventory().setItem(match.slot, mainHand);
            player.setItemInHand(InteractionHand.MAIN_HAND, found);
        }

        player.getInventory().setChanged();
        player.containerMenu.broadcastChanges();
    }

    private static DirectMatch findDirectMatch(ServerPlayer player, List<String> itemPatterns) {
        Inventory inventory = player.getInventory();
        int selectedSlot = getSelectedSlot(player);
        for (String pattern : itemPatterns) {
            for (int slot = 0; slot < 36; slot++) {
                if (slot == selectedSlot) {
                    continue;
                }
                ItemStack stack = inventory.getItem(slot);
                if (!stack.isEmpty() && matchesPattern(stack, pattern)) {
                    return new DirectMatch(slot);
                }
            }

            ItemStack offhand = player.getOffhandItem();
            if (!offhand.isEmpty() && matchesPattern(offhand, pattern)) {
                return new DirectMatch(Inventory.SLOT_OFFHAND);
            }
        }
        return null;
    }

    private static ExternalMatch findExternalMatch(ServerPlayer player, List<String> itemPatterns) {
        List<ItemStack> carriers = InventoryUtil.getFullInv(player);
        for (String pattern : itemPatterns) {
            for (Item item : BuiltInRegistries.ITEM) {
                if (!GlobMatcher.matches(BuiltInRegistries.ITEM.getKey(item).toString(), pattern)) {
                    continue;
                }

                ItemStack target = new ItemStack(item);
                for (ItemStack carrier : carriers) {
                    if (carrier.isEmpty()) {
                        continue;
                    }

                    int amount = WrenchFinder.containerManager.countItems(player, new ContainerTrace(player), target, carrier);
                    if (amount > 0) {
                        return new ExternalMatch(carrier, target, amount);
                    }
                }

            }
        }
        return null;
    }

    private static ItemStack extractFromExternal(ServerPlayer player, ExternalMatch match) {
        int requested = Math.min(match.amount, match.target.getMaxStackSize());
        int remaining = WrenchFinder.containerManager.useItems(
            player,
            new ContainerTrace(player),
            match.target,
            match.carrier,
            requested
        );
        int extracted = requested - remaining;
        if (extracted <= 0) {
            return ItemStack.EMPTY;
        }

        ItemStack result = match.target.copy();
        result.setCount(extracted);
        return result;
    }

    private static boolean matchesAny(ItemStack stack, List<String> itemPatterns) {
        if (stack.isEmpty()) {
            return false;
        }

        for (String pattern : itemPatterns) {
            if (matchesPattern(stack, pattern)) {
                return true;
            }
        }
        return false;
    }

    private static boolean matchesPattern(ItemStack stack, String pattern) {
        return GlobMatcher.matches(BuiltInRegistries.ITEM.getKey(stack.getItem()).toString(), pattern);
    }

    private static boolean canStoreInInventory(Inventory inventory, ItemStack stack, int selectedSlot) {
        return getRemainingAfterStoreSimulation(inventory, stack, selectedSlot).isEmpty();
    }

    private static boolean addToInventory(Inventory inventory, ItemStack stack, int selectedSlot) {
        ItemStack remainder = getRemainingAfterStoreSimulation(inventory, stack, selectedSlot, true);
        return remainder.isEmpty();
    }

    private static ItemStack getRemainingAfterStoreSimulation(Inventory inventory, ItemStack input, int selectedSlot) {
        return getRemainingAfterStoreSimulation(inventory, input, selectedSlot, false);
    }

    private static ItemStack getRemainingAfterStoreSimulation(Inventory inventory, ItemStack input, int selectedSlot, boolean mutateInventory) {
        ItemStack remainder = input.copy();

        for (int slot = 0; slot < 36 && !remainder.isEmpty(); slot++) {
            if (slot == selectedSlot) {
                continue;
            }
            ItemStack existing = inventory.getItem(slot);
            if (existing.isEmpty() || !ItemStack.isSameItemSameTags(existing, remainder)) {
                continue;
            }

            int space = Math.min(existing.getMaxStackSize(), inventory.getMaxStackSize()) - existing.getCount();
            if (space <= 0) {
                continue;
            }

            int moved = Math.min(space, remainder.getCount());
            if (mutateInventory) {
                existing.grow(moved);
            }
            remainder.shrink(moved);
        }

        for (int slot = 0; slot < 36 && !remainder.isEmpty(); slot++) {
            if (slot == selectedSlot) {
                continue;
            }
            ItemStack existing = inventory.getItem(slot);
            if (!existing.isEmpty()) {
                continue;
            }

            if (mutateInventory) {
                inventory.setItem(slot, remainder.copy());
            }
            remainder = ItemStack.EMPTY;
        }

        return remainder;
    }

    private static void notifyFailure(ServerPlayer player, boolean notifyFailures, String translationKey) {
        if (notifyFailures) {
            player.sendSystemMessage(Component.translatable(translationKey));
        }
    }

    private static int getSelectedSlot(ServerPlayer player) {
        ItemStack selected = player.getMainHandItem();
        for (int slot = 0; slot < 9; slot++) {
            if (player.getInventory().getItem(slot) == selected) {
                return slot;
            }
        }
        return 0;
    }

    private record DirectMatch(int slot) {
    }

    private record ExternalMatch(ItemStack carrier, ItemStack target, int amount) {
    }
}
