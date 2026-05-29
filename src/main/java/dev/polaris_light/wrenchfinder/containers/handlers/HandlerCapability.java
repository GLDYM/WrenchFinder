package dev.polaris_light.wrenchfinder.containers.handlers;

import dev.polaris_light.wrenchfinder.WrenchFinder;
import dev.polaris_light.wrenchfinder.api.IContainerHandler;
import dev.polaris_light.wrenchfinder.containers.ContainerTrace;
import dev.polaris_light.wrenchfinder.logic.InventoryUtil;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.IItemHandler;

import java.util.Optional;

public class HandlerCapability implements IContainerHandler {
    @Override
    public boolean matches(Player player, ItemStack itemStack, ItemStack inventoryStack) {
        return inventoryStack != null && inventoryStack.getCapability(ForgeCapabilities.ITEM_HANDLER).isPresent();
    }

    @Override
    public int getSignature(Player player, ItemStack inventoryStack) {
        return inventoryStack.hashCode();
    }

    @Override
    public int countItems(Player player, ContainerTrace trace, ItemStack itemStack, ItemStack inventoryStack) {
        Optional<IItemHandler> itemHandlerOptional = inventoryStack.getCapability(ForgeCapabilities.ITEM_HANDLER).resolve();
        if (itemHandlerOptional.isEmpty()) {
            return 0;
        }

        IItemHandler itemHandler = itemHandlerOptional.get();
        int total = 0;

        for (int i = 0; i < itemHandler.getSlots(); i++) {
            ItemStack containerStack = itemHandler.getStackInSlot(i);
            if (InventoryUtil.stackEquals(itemStack, containerStack)) {
                total += Math.max(0, containerStack.getCount());
            } else {
                total += WrenchFinder.containerManager.countItems(player, trace, itemStack, containerStack);
            }
        }
        return total;
    }

    @Override
    public int useItems(Player player, ContainerTrace trace, ItemStack itemStack, ItemStack inventoryStack, int count) {
        Optional<IItemHandler> itemHandlerOptional = inventoryStack.getCapability(ForgeCapabilities.ITEM_HANDLER).resolve();
        if (itemHandlerOptional.isEmpty()) {
            return 0;
        }

        IItemHandler itemHandler = itemHandlerOptional.get();
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            ItemStack handlerStack = itemHandler.getStackInSlot(i);
            if (InventoryUtil.stackEquals(itemStack, handlerStack)) {
                ItemStack extracted = itemHandler.extractItem(i, count, false);
                count -= extracted.getCount();
                if (count <= 0) {
                    break;
                }
            } else {
                int before = count;
                count = WrenchFinder.containerManager.useItems(player, trace, itemStack, handlerStack, count);
                if (count < before && count <= 0) {
                    break;
                }
            }
        }
        return count;
    }
}
