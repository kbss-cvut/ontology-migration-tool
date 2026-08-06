package cz.cvut.kbss.model;

/**
 * The type of additional included file
 */
public enum AdditionalFileType {
    CHANGELOG("changelog"),
    CHANGESET("changeSet");

    /**
     * The first top level key in an additional file.
     * <pre><code>
     *     changelog:
     *          # ...
     * </code></pre>
     */
    private final String topLevelKey;

    AdditionalFileType(String topLevelKey) {
        this.topLevelKey = topLevelKey;
    }

    public String getTopLevelKey() {
        return topLevelKey;
    }

    public static AdditionalFileType forKey(String topLevelKey) {
        for (AdditionalFileType type : values()) {
            if (type.topLevelKey.equals(topLevelKey)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown additional file type with top level key: " + topLevelKey);
    }
}
