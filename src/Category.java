public enum Category {
    FADE,
    BUZZ_CUT,
    LONG_HAIR,
    BEARD,
    COLORING,
    STYLING;

    public static Category fromString(String value) {
        if (value == null) {
            throw new IllegalArgumentException("Category cannot be null");
        }

        return Category.valueOf(
                value.trim()
                        .toUpperCase()
                        .replace(" ", "_")
                        .replace("-", "_")
        );
    }
}
