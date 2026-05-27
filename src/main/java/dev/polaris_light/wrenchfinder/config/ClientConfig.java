package dev.polaris_light.wrenchfinder.config;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;

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

        var holder = AutoConfig.register(ClientConfig.class, GsonConfigSerializer::new);
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
        defaults.add(new RuleEntry("minecraft:bedrock", List.of(
            new ItemPattern("create:wrench"),
            new ItemPattern("minecraft:barrier")
        )));
        defaults.add(new RuleEntry("*create*:*", List.of(
            new ItemPattern("create:wrench")
        )));
        defaults.add(new RuleEntry("botania:*", List.of(
            new ItemPattern("botania:dreamwood_wand"),
            new ItemPattern("botania:twigwand")
        )));
        defaults.add(new RuleEntry("appliedenergistics2:*", List.of(
            new ItemPattern("ae2:network_tool"),
            new ItemPattern("ae2:certus_quartz_wrench"),
            new ItemPattern("ae2:nether_quartz_wrench")
        )));
        defaults.add(new RuleEntry("*ae*:*", List.of(
            new ItemPattern("ae2:network_tool"),
            new ItemPattern("ae2:certus_quartz_wrench"),
            new ItemPattern("ae2:nether_quartz_wrench")
        )));
        defaults.add(new RuleEntry("*pipez*:*", List.of(
            new ItemPattern("pipez:wrench")
        )));
        defaults.add(new RuleEntry("*thermal*:*", List.of(
            new ItemPattern("thermal:wrench")
        )));
        defaults.add(new RuleEntry("*mekanism*:*", List.of(
            new ItemPattern("mekanism:configurator")
        )));
        defaults.add(new RuleEntry("ars_nouveau*:*", List.of(
            new ItemPattern("ars_nouveau:dominion_wand")
        )));
        defaults.add(new RuleEntry("laserio*:*", List.of(
            new ItemPattern("laserio:laser_wrench")
        )));
        defaults.add(new RuleEntry("*integrateddynamics*:*", List.of(
            new ItemPattern("integrateddynamics:wrench")
        )));
        defaults.add(new RuleEntry("*immersiveengineering*:*", List.of(
            new ItemPattern("immersiveengineering:tool")
        )));
        defaults.add(new RuleEntry("*oritech*:*", List.of(
            new ItemPattern("oritech:wrench")
        )));
        defaults.add(new RuleEntry("*anvilcraft*:*", List.of(
            new ItemPattern("anvilcraft:anvil_hammer"),
            new ItemPattern("anvilcraft:royal_anvil_hammer"),
            new ItemPattern("anvilcraft:ember_anvil_hammer"),
            new ItemPattern("anvilcraft:transcendence_anvil_hammer")
        )));
        defaults.add(new RuleEntry("kaleidoscope_cookery:stockpot", List.of(
            new ItemPattern("kaleidoscope_cookery:stockpot_lid")
        )));
        defaults.add(new RuleEntry("kaleidoscope_cookery:pot", List.of(
            new ItemPattern("kaleidoscope_cookery:kitchen_shovel"),
            new ItemPattern("kaleidoscope_cookery:oil_pot")
        )));
        defaults.add(new RuleEntry("kaleidoscope_cookery:enamel_basin", List.of(
            new ItemPattern("kaleidoscope_cookery:kitchen_shovel")
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
