package dev.polaris_light.wrenchfinder.client;

import dev.polaris_light.wrenchfinder.WrenchFinder;
import dev.polaris_light.wrenchfinder.config.ClientConfig;
import dev.polaris_light.wrenchfinder.logic.ClientRuleResolver;
import dev.polaris_light.wrenchfinder.network.ModMessages;
import dev.polaris_light.wrenchfinder.network.PacketFindItem;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import org.lwjgl.glfw.GLFW;

import java.util.List;

public class KeybindHandler {
    public static final KeyMapping KEY_LOOKUP = new KeyMapping(
        getKey("lookup"),
        GLFW.GLFW_KEY_Z,
        getKey("category")
    );

    private static String getKey(String name) {
        return String.join(".", "key", WrenchFinder.MODID, name);
    }

    @SubscribeEvent
    public void onClientTick(ClientTickEvent.Post event) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null || minecraft.level == null) {
            return;
        }

        while (KEY_LOOKUP.consumeClick()) {
            if (!(minecraft.hitResult instanceof BlockHitResult blockHitResult)
                || blockHitResult.getType() != HitResult.Type.BLOCK) {
                notifyClient(Component.translatable("message.wrenchfinder.not_pointing_block"));
                continue;
            }

            ResourceLocation blockId = BuiltInRegistries.BLOCK.getKey(
                minecraft.level.getBlockState(blockHitResult.getBlockPos()).getBlock()
            );
            if (blockId == null) {
                notifyClient(Component.translatable("message.wrenchfinder.no_rule"));
                continue;
            }

            List<String> itemPatterns = ClientRuleResolver.resolve(blockId, minecraft.level.getBlockState(blockHitResult.getBlockPos()));
            if (itemPatterns.isEmpty()) {
                notifyClient(Component.translatable("message.wrenchfinder.no_rule"));
                continue;
            }

            ModMessages.sendToServer(new PacketFindItem(
                blockId.toString(),
                itemPatterns,
                ClientConfig.get().handActionMode,
                ClientConfig.get().showFailureMessages
            ));
        }
    }

    private static void notifyClient(Component message) {
        if (ClientConfig.get().showFailureMessages) {
            Minecraft minecraft = Minecraft.getInstance();
            if (minecraft.player != null) {
                minecraft.player.sendSystemMessage(message);
            }
        }
    }
}
