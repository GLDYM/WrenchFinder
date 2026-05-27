package dev.polaris_light.wrenchfinder.client;

import dev.polaris_light.wrenchfinder.WrenchFinder;
import dev.polaris_light.wrenchfinder.config.ClientConfig;
import me.shedaniel.autoconfig.AutoConfig;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;

@Mod(value = WrenchFinder.MODID, dist = Dist.CLIENT)
public class WrenchFinderClient {
    public WrenchFinderClient(ModContainer container) {
        ClientConfig.init();
        container.getEventBus().addListener(WrenchFinderClient::registerKeyMappings);
        container.registerExtensionPoint(IConfigScreenFactory.class, (minecraft, parent) ->
            AutoConfig.getConfigScreen(ClientConfig.class, parent).get()
        );
        NeoForge.EVENT_BUS.register(new KeybindHandler());
    }

    public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(KeybindHandler.KEY_LOOKUP);
    }
}
