package dev.polaris_light.wrenchfinder.containers.handlers;

import com.klikli_dev.occultism.api.common.blockentity.IStorageController;
import com.klikli_dev.occultism.api.common.data.GlobalBlockPos;
import com.klikli_dev.occultism.common.item.storage.StorageRemoteItem;
import com.klikli_dev.occultism.common.misc.ItemStackComparator;
import com.klikli_dev.occultism.registry.OccultismDataComponents;
import dev.polaris_light.wrenchfinder.api.IContainerHandler;
import dev.polaris_light.wrenchfinder.containers.ContainerTrace;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class HandlerOccultism implements IContainerHandler {
    @Override
    public boolean matches(Player player, ItemStack itemStack, ItemStack inventoryStack) {
        return inventoryStack != null && inventoryStack.getItem() instanceof StorageRemoteItem;
    }

    @Override
    public int getSignature(Player player, ItemStack inventoryStack) {
        if (!inventoryStack.has(OccultismDataComponents.LINKED_STORAGE_CONTROLLER)) {
            return -1;
        }

        GlobalBlockPos linkedPos = inventoryStack.get(OccultismDataComponents.LINKED_STORAGE_CONTROLLER);
        return linkedPos != null ? linkedPos.hashCode() : -1;
    }

    @Override
    public int countItems(Player player, ContainerTrace trace, ItemStack itemStack, ItemStack inventoryStack) {
        IStorageController storageController = getStorageController(inventoryStack, player);
        if (storageController == null) {
            return 0;
        }

        return Math.max(storageController.getAvailableAmount(new ItemStackComparator(itemStack, true)), 0);
    }

    @Override
    public int useItems(Player player, ContainerTrace trace, ItemStack itemStack, ItemStack inventoryStack, int count) {
        if (count <= 0) {
            return count;
        }

        IStorageController storageController = getStorageController(inventoryStack, player);
        if (storageController == null) {
            return count;
        }

        ItemStackComparator comparator = new ItemStackComparator(itemStack, true);
        ItemStack simulated = storageController.getItemStack(comparator, count, true);
        if (simulated.isEmpty()) {
            return count;
        }

        ItemStack extracted = storageController.getItemStack(comparator, Math.min(count, simulated.getCount()), false);
        return extracted.isEmpty() ? count : Math.max(count - extracted.getCount(), 0);
    }

    private static IStorageController getStorageController(ItemStack remote, Player player) {
        if (!(remote.getItem() instanceof StorageRemoteItem)) {
            return null;
        }
        return StorageRemoteItem.getStorageController(remote, player.level());
    }
}
