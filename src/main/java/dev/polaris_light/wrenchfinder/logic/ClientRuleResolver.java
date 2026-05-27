package dev.polaris_light.wrenchfinder.logic;

import dev.polaris_light.wrenchfinder.config.ClientConfig;
import dev.polaris_light.wrenchfinder.config.LookupRule;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

public final class ClientRuleResolver {
    private ClientRuleResolver() {
    }

    public static List<String> resolve(ResourceLocation blockId, BlockState state) {
        LinkedHashSet<String> resolved = new LinkedHashSet<>();

        for (LookupRule rule : ClientConfig.get().compiledRules()) {
            if (rule.matches(blockId.toString())) {
                resolved.addAll(rule.itemPatterns());
            }
        }

        if (resolved.isEmpty() && ClientConfig.get().fallbackToBlockItem) {
            Item item = state.getBlock().asItem();
            if (item != Items.AIR) {
                ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(item);
                if (itemId != null) {
                    resolved.add(itemId.toString());
                }
            }
        }

        return new ArrayList<>(resolved);
    }
}
