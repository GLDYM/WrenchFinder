package dev.polaris_light.wrenchfinder.config;

import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.common.TranslatableEnum;

public enum HandActionMode implements TranslatableEnum {
    MOVE_TO_INVENTORY,
    SWAP;

    @Override
    public Component getTranslatedName() {
        return Component.translatable("config.wrenchfinder.hand_action." + name().toLowerCase());
    }
}
