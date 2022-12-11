package codes.ztereohype.example.config;

public enum NebulaType {
    RAINBOW("Rainbow");

    private final String type;

    NebulaType(String type) {
        this.type = type;
    }

    public String getTypeString() {
        return type;
    }
}
