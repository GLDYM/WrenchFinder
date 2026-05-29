package dev.polaris_light.wrenchfinder.config;

import net.minecraft.network.chat.Component;

public enum HandActionMode {
    MOVE_TO_INVENTORY,
    SWAP;

    public Component getTranslatedName() {
        return Component.translatable("config.wrenchfinder.hand_action." + name().toLowerCase());
    }
}
