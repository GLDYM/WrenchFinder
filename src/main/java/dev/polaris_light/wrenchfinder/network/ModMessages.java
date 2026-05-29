package dev.polaris_light.wrenchfinder.network;

import dev.polaris_light.wrenchfinder.WrenchFinder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public final class ModMessages {
    private static final String PROTOCOL_VERSION = "1";
    private static SimpleChannel instance;

    private ModMessages() {
    }

    public static void register() {
        instance = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(WrenchFinder.MODID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
        );

        int packetId = 0;
        instance.messageBuilder(PacketFindItem.class, packetId++, NetworkDirection.PLAY_TO_SERVER)
            .encoder(PacketFindItem::encode)
            .decoder(PacketFindItem::decode)
            .consumerMainThread(PacketFindItem.Handler::handle)
            .add();
    }

    public static void sendToServer(Object message) {
        instance.sendToServer(message);
    }

    public static void sendToPlayer(Object message, ServerPlayer player) {
        instance.send(PacketDistributor.PLAYER.with(() -> player), message);
    }
}
