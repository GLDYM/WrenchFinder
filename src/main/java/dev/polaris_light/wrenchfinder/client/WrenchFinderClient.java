package dev.polaris_light.wrenchfinder.client;

import dev.polaris_light.wrenchfinder.config.ClientConfig;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.client.gui.screens.Screen;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;

public final class WrenchFinderClient {
    private WrenchFinderClient() {
    }

    public static void init() {
        ClientConfig.init();
    }

    public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(KeybindHandler.KEY_LOOKUP);
    }

    public static Screen createConfigScreen(Screen parent) {
        init();
        return AutoConfig.getConfigScreen(ClientConfig.class, parent).get();
    }
}
