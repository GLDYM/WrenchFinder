package dev.polaris_light.wrenchfinder.client;

import dev.polaris_light.wrenchfinder.WrenchFinder;
import dev.polaris_light.wrenchfinder.config.ClientConfig;
import me.shedaniel.autoconfig.AutoConfigClient;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;

@Mod(value = WrenchFinder.MODID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = WrenchFinder.MODID, value = Dist.CLIENT)
public class WrenchFinderClient {
    public WrenchFinderClient(ModContainer container) {
        ClientConfig.init();
        container.registerExtensionPoint(IConfigScreenFactory.class, (minecraft, parent) ->
            AutoConfigClient.getConfigScreen(ClientConfig.class, parent).get()
        );
        NeoForge.EVENT_BUS.register(new KeybindHandler());
    }

    @SubscribeEvent
    public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(KeybindHandler.KEY_LOOKUP);
    }
}
