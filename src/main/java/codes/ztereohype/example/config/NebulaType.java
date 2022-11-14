package codes.ztereohype.example.config;

public enum NebulaType {
    RAINBOW("rainbow");

    private final String type;

    NebulaType(String type) {
        this.type = type;
    }

    public String getTypeString() {
        return type;
    }

    // bad?
    public static NebulaType getFromString(String type) {
        for (NebulaType nebulaType : NebulaType.values()) {
            if (nebulaType.getTypeString().equals(type)) {
                return nebulaType;
            }
        }
        return null;
    }
}
