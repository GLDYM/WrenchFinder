package dev.polaris_light.wrenchfinder.config;

import dev.polaris_light.wrenchfinder.logic.GlobMatcher;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;

public record LookupRule(String blockPattern, List<String> itemPatterns) {
    public LookupRule {
        itemPatterns = List.copyOf(itemPatterns);
    }

    public static LookupRule of(String blockPattern, List<String> itemPatterns) {
        if (blockPattern == null) {
            return null;
        }

        String normalizedBlockPattern = blockPattern.trim();
        if (normalizedBlockPattern.isEmpty()) {
            return null;
        }

        LinkedHashSet<String> normalizedPatterns = new LinkedHashSet<>();
        for (String pattern : itemPatterns) {
            if (pattern != null) {
                String trimmed = pattern.trim();
                if (!trimmed.isEmpty()) {
                    normalizedPatterns.add(trimmed);
                }
            }
        }

        if (normalizedPatterns.isEmpty()) {
            return null;
        }

        return new LookupRule(normalizedBlockPattern, new ArrayList<>(normalizedPatterns));
    }

    public static LookupRule parse(String raw) {
        if (raw == null) {
            return null;
        }

        String trimmed = raw.trim();
        if (trimmed.isEmpty()) {
            return null;
        }

        String[] split = trimmed.contains("=>")
            ? trimmed.split("=>", 2)
            : trimmed.split("=", 2);
        if (split.length != 2) {
            return null;
        }

        List<String> patterns = new ArrayList<>();
        Collections.addAll(patterns, split[1].split(","));
        return of(split[0], patterns);
    }

    public boolean matches(String blockId) {
        return GlobMatcher.matches(blockId, blockPattern);
    }
}
