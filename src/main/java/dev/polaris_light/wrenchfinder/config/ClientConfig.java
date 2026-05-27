package dev.polaris_light.wrenchfinder.config;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.serializer.Toml4jConfigSerializer;

import java.util.ArrayList;
import java.util.List;

@Config(name = "wrenchfinder-client")
public class ClientConfig implements ConfigData {
    @ConfigEntry.Category("general")
    @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
    @ConfigEntry.Gui.Tooltip(count = 1)
    public HandActionMode handActionMode = HandActionMode.MOVE_TO_INVENTORY;

    @ConfigEntry.Category("general")
    @ConfigEntry.Gui.Tooltip(count = 1)
    public boolean fallbackToBlockItem = true;

    @ConfigEntry.Category("general")
    @ConfigEntry.Gui.Tooltip(count = 1)
    public boolean showFailureMessages = true;

    @ConfigEntry.Category("rules")
    @ConfigEntry.Gui.Tooltip(count = 2)
    public List<RuleEntry> lookupRules = defaultRules();

    public static void init() {
        if (ClientConfigState.initialized) {
            return;
        }

        var holder = AutoConfig.register(ClientConfig.class, Toml4jConfigSerializer::new);
        holder.load();
        sanitizeInPlace(holder.getConfig());
        holder.save();
        ClientConfigState.initialized = true;
    }

    public static ClientConfig get() {
        if (!ClientConfigState.initialized) {
            init();
        }
        ClientConfig config = AutoConfig.getConfigHolder(ClientConfig.class).getConfig();
        sanitizeInPlace(config);
        return config;
    }

    public static ClientConfig defaults() {
        ClientConfig defaults = new ClientConfig();
        sanitizeInPlace(defaults);
        return defaults;
    }

    public static void save(ClientConfig config) {
        if (!ClientConfigState.initialized) {
            init();
        }

        sanitizeInPlace(config);
        var holder = AutoConfig.getConfigHolder(ClientConfig.class);
        holder.setConfig(config);
        holder.save();
    }

    @Override
    public void validatePostLoad() {
        sanitizeInPlace(this);
    }

    public List<LookupRule> compiledRules() {
        return sanitizeRules(lookupRules).stream()
            .map(RuleEntry::toLookupRule)
            .filter(rule -> rule != null)
            .toList();
    }

    private static void sanitizeInPlace(ClientConfig config) {
        if (config.handActionMode == null) {
            config.handActionMode = HandActionMode.MOVE_TO_INVENTORY;
        }
        config.lookupRules = sanitizeRules(config.lookupRules);
    }

    private static List<RuleEntry> sanitizeRules(List<RuleEntry> rules) {
        List<RuleEntry> sanitized = new ArrayList<>();
        List<RuleEntry> sourceRules = rules == null ? defaultRules() : rules;
        for (RuleEntry entry : sourceRules) {
            if (entry == null) {
                continue;
            }

            LookupRule normalized = entry.toLookupRule();
            if (normalized != null) {
                sanitized.add(new RuleEntry(normalized.blockPattern(), normalized.itemPatterns().stream()
                    .map(ItemPattern::new)
                    .toList()));
            }
        }
        return sanitized;
    }

    private static List<RuleEntry> defaultRules() {
        List<RuleEntry> defaults = new ArrayList<>();
        defaults.add(new RuleEntry("minecraft:crafting_table", List.of(new ItemPattern("minecraft:crafting_table"))));
        defaults.add(new RuleEntry("minecraft:furnace", List.of(
            new ItemPattern("minecraft:furnace"),
            new ItemPattern("minecraft:blast_furnace"),
            new ItemPattern("minecraft:smoker")
        )));
        defaults.add(new RuleEntry("minecraft:*_planks", List.of(
            new ItemPattern("minecraft:oak_planks"),
            new ItemPattern("minecraft:spruce_planks"),
            new ItemPattern("minecraft:birch_planks")
        )));
        return defaults;
    }

    public static class RuleEntry {
        @ConfigEntry.Gui.Tooltip(count = 2)
        public String blockPattern = "";

        @ConfigEntry.Gui.Tooltip(count = 2)
        public List<ItemPattern> itemPatterns = new ArrayList<>();

        public RuleEntry() {
        }

        public RuleEntry(String blockPattern, List<ItemPattern> itemPatterns) {
            this.blockPattern = blockPattern;
            this.itemPatterns = new ArrayList<>(itemPatterns);
        }

        public LookupRule toLookupRule() {
            List<String> patterns = new ArrayList<>();
            if (itemPatterns != null) {
                for (ItemPattern itemPattern : itemPatterns) {
                    if (itemPattern == null) {
                        continue;
                    }
                    String value = itemPattern.value == null ? "" : itemPattern.value.trim();
                    if (!value.isEmpty()) {
                        patterns.add(value);
                    }
                }
            }
            return LookupRule.of(blockPattern, patterns);
        }

        @Override
        public String toString() {
            if (blockPattern == null || blockPattern.isBlank()) {
                return "New Rule";
            }
            return blockPattern.trim();
        }
    }

    public static class ItemPattern {
        public String value = "";

        public ItemPattern() {
        }

        public ItemPattern(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            if (value == null || value.isBlank()) {
                return "Pattern";
            }
            return value.trim();
        }
    }
}

final class ClientConfigState {
    static boolean initialized;

    private ClientConfigState() {
    }
}
