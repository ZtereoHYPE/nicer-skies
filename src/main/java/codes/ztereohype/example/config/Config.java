package codes.ztereohype.example.config;

public class Config {
    public Config(boolean tweakedLigthmap, boolean twinklingStars, boolean nebulas, String nebulaType, float nebulaStrength) {
        this.tweakedLigthmap = tweakedLigthmap;
        this.twinklingStars = twinklingStars;
        this.nebulas = nebulas;

        this.nebulaConfig = new NebulaConfig(nebulaType, nebulaStrength);
    }

    private boolean tweakedLigthmap;
    private boolean twinklingStars;
    private boolean nebulas;

    private final NebulaConfig nebulaConfig;

    public boolean isTweakedLigthmap() {
        return this.tweakedLigthmap;
    }

    public boolean isTwinklingStars() {
        return this.twinklingStars;
    }

    public boolean isNebulas() {
        return this.nebulas;
    }

    public NebulaConfig getNebulaConfig() {
        return this.nebulaConfig;
    }

    public void setTweakedLigthmap(boolean tweakedLigthmap) {
        this.tweakedLigthmap = tweakedLigthmap;
    }

    public void setTwinklingStars(boolean twinklingStars) {
        this.twinklingStars = twinklingStars;
    }

    public void setNebulas(boolean nebulas) {
        this.nebulas = nebulas;
    }

    public static final class NebulaConfig {
        public NebulaConfig(String nebulaType, float nebulaStrength) {
            this.nebulaType = nebulaType;
            this.nebulaStrength = nebulaStrength;
        }

        private String nebulaType;
        private float nebulaStrength;

        public String getNebulaType() {
            return this.nebulaType;
        }

        public void setNebulaType(String nebulaType) {
            this.nebulaType = nebulaType;
        }

        public float getNebulaStrength() {
            return this.nebulaStrength;
        }

        public void setNebulaStrength(float nebulaStrength) {
            this.nebulaStrength = nebulaStrength;
        }
    }

    @Override
    public String toString() {
        return "Config{" +
                "tweakedLigthmap=" + tweakedLigthmap +
                ", twinklingStars=" + twinklingStars +
                ", nebulas=" + nebulas +
                ", nebulaConfig=" + nebulaConfig +
                '}';
    }
}
