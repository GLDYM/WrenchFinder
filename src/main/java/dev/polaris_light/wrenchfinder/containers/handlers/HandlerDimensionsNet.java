package dev.polaris_light.wrenchfinder.containers.handlers;

import com.wintercogs.beyonddimensions.api.dimensionnet.DimensionsNet;
import com.wintercogs.beyonddimensions.api.dimensionnet.UnifiedStorage;
import com.wintercogs.beyonddimensions.api.storage.key.impl.ItemStackKey;
import dev.polaris_light.wrenchfinder.api.IContainerHandler;
import dev.polaris_light.wrenchfinder.containers.ContainerTrace;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class HandlerDimensionsNet implements IContainerHandler {

    @Override
    public boolean matches(Player player, ItemStack itemStack, ItemStack inventoryStack) {
        return inventoryStack == player.getOffhandItem() && DimensionsNet.getNetFromPlayer(player) != null;
    }

    @Override
    public int getSignature(Player player, ItemStack inventoryStack) {
        DimensionsNet net = DimensionsNet.getNetFromPlayer(player);
        return net != null ? 10000 + net.getId() : -1;
    }

    @Override
    public int countItems(Player player, ContainerTrace trace, ItemStack itemStack, ItemStack inventoryStack) {
        DimensionsNet net = DimensionsNet.getNetFromPlayer(player);
        if (net == null) {
            return 0;
        }

        UnifiedStorage storage = net.getUnifiedStorage();
        long result = storage.extract(new ItemStackKey(itemStack), Integer.MAX_VALUE, true, false).amount();
        return result > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) result;
    }

    @Override
    public int useItems(Player player, ContainerTrace trace, ItemStack itemStack, ItemStack inventoryStack, int count) {
        DimensionsNet net = DimensionsNet.getNetFromPlayer(player);
        if (net == null) {
            return count;
        }

        UnifiedStorage storage = net.getUnifiedStorage();
        long result = storage.extract(new ItemStackKey(itemStack), count, false, false).amount();
        if (result > count) {
            return 0;
        }

        return count - (int) result;
    }
}
