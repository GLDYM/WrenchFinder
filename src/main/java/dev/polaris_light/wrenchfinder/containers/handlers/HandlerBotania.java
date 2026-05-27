package dev.polaris_light.wrenchfinder.containers.handlers;

import dev.polaris_light.wrenchfinder.api.IContainerHandler;
import dev.polaris_light.wrenchfinder.containers.ContainerTrace;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import vazkii.botania.api.BotaniaForgeCapabilities;
import vazkii.botania.api.item.BlockProvider;

public class HandlerBotania implements IContainerHandler {
    @Override
    public boolean matches(Player player, ItemStack itemStack, ItemStack inventoryStack) {
        return inventoryStack != null && inventoryStack.getCapability(BotaniaForgeCapabilities.BLOCK_PROVIDER) != null;
    }

    @Override
    public int getSignature(Player player, ItemStack inventoryStack) {
        return inventoryStack.hashCode();
    }

    @Override
    public int countItems(Player player, ContainerTrace trace, ItemStack itemStack, ItemStack inventoryStack) {
        BlockProvider provider = inventoryStack.getCapability(BotaniaForgeCapabilities.BLOCK_PROVIDER);
        if (provider == null) {
            return 0;
        }

        int providedCount = provider.getBlockCount(player, inventoryStack, Block.byItem(itemStack.getItem()));
        return providedCount == -1 ? Integer.MAX_VALUE : providedCount;
    }

    @Override
    public int useItems(Player player, ContainerTrace trace, ItemStack itemStack, ItemStack inventoryStack, int count) {
        BlockProvider provider = inventoryStack.getCapability(BotaniaForgeCapabilities.BLOCK_PROVIDER);
        if (provider == null) {
            return count;
        }

        return provider.provideBlock(player, inventoryStack, Block.byItem(itemStack.getItem()), true) ? 0 : count;
    }
}
