package dev.polaris_light.wrenchfinder.logic;

public final class GlobMatcher {
    private GlobMatcher() {
    }

    public static boolean matches(String value, String pattern) {
        if (value == null || pattern == null) {
            return false;
        }

        StringBuilder regex = new StringBuilder("^");
        for (int i = 0; i < pattern.length(); i++) {
            char c = pattern.charAt(i);
            switch (c) {
                case '*' -> regex.append(".*");
                case '?' -> regex.append('.');
                case '.', '(', ')', '[', ']', '{', '}', '^', '$', '|', '+', '\\' -> regex.append('\\').append(c);
                default -> regex.append(c);
            }
        }
        regex.append('$');
        return value.matches(regex.toString());
    }
}
