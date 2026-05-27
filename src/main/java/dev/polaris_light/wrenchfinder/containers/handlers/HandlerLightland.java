package dev.polaris_light.wrenchfinder.containers.handlers;

import dev.polaris_light.wrenchfinder.WrenchFinder;
import dev.polaris_light.wrenchfinder.api.IContainerHandler;
import dev.polaris_light.wrenchfinder.containers.ContainerTrace;
import dev.polaris_light.wrenchfinder.logic.InventoryUtil;
import dev.xkmc.l2backpack.content.capability.InvPickupCap;
import dev.xkmc.l2backpack.content.capability.PickupModeCap;
import dev.xkmc.l2backpack.content.capability.PickupTrace;
import dev.xkmc.l2backpack.init.registrate.LBMisc;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandlerModifiable;

public class HandlerLightland implements IContainerHandler {
    @Override
    public boolean matches(Player player, ItemStack itemStack, ItemStack inventoryStack) {
        return resolveInvCap(inventoryStack) != null;
    }

    @Override
    public int getSignature(Player player, ItemStack inventoryStack) {
        InvPickupCap<?> cap = resolveInvCap(inventoryStack);
        return cap != null ? cap.getSignature() : -1;
    }

    @Override
    public int countItems(Player player, ContainerTrace trace, ItemStack itemStack, ItemStack inventoryStack) {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return 0;
        }

        InvPickupCap<?> cap = resolveInvCap(inventoryStack);
        if (cap == null) {
            return 0;
        }

        IItemHandlerModifiable inventory = cap.getInv(new PickupTrace(false, serverPlayer));
        if (inventory == null) {
            return 0;
        }

        int found = 0;
        for (int i = 0; i < inventory.getSlots(); i++) {
            ItemStack stack = inventory.getStackInSlot(i);
            if (InventoryUtil.stackEquals(stack, itemStack)) {
                found += stack.getCount();
                if (found >= Integer.MAX_VALUE) {
                    return Integer.MAX_VALUE;
                }
            } else {
                found += WrenchFinder.containerManager.countItems(player, trace, itemStack, stack);
                if (found >= Integer.MAX_VALUE) {
                    return Integer.MAX_VALUE;
                }
            }
        }
        return found;
    }

    @Override
    public int useItems(Player player, ContainerTrace trace, ItemStack itemStack, ItemStack inventoryStack, int count) {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return count;
        }

        InvPickupCap<?> cap = resolveInvCap(inventoryStack);
        if (cap == null) {
            return count;
        }

        IItemHandlerModifiable inventory = cap.getInv(new PickupTrace(false, serverPlayer));
        if (inventory == null) {
            return count;
        }

        int remaining = count;
        for (int i = 0; i < inventory.getSlots(); i++) {
            ItemStack stack = inventory.getStackInSlot(i);
            if (InventoryUtil.stackEquals(stack, itemStack)) {
                int extractable = Math.min(stack.getCount(), remaining);
                ItemStack extracted = inventory.extractItem(i, extractable, false);
                remaining -= extracted.getCount();
                if (remaining <= 0) {
                    return 0;
                }
            } else {
                remaining = WrenchFinder.containerManager.useItems(player, trace, itemStack, stack, remaining);
                if (remaining <= 0) {
                    return 0;
                }
            }
        }
        return remaining;
    }

    private InvPickupCap<?> resolveInvCap(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return null;
        }

        PickupModeCap cap = stack.getCapability(LBMisc.PICKUP);
        return cap instanceof InvPickupCap<?> invPickupCap ? invPickupCap : null;
    }
}
