package dev.polaris_light.wrenchfinder.network;

import dev.polaris_light.wrenchfinder.WrenchFinder;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public final class ModMessages {
    private ModMessages() {
    }

    public static void registerPayloads(final RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(WrenchFinder.MODID);
        registrar.playToServer(PacketFindItem.ID, PacketFindItem.CODEC, PacketFindItem.Handler::handle);
    }
}
