package codes.ztereohype.example.config;

import lombok.Data;

@Data
public class Config {
    public Config(boolean tweakedLigthmap, boolean twinklingStars, boolean nebulas, String nebulaType, float nebulaStrength, float nebulaNoiseAmount, float nebulaNoiseScale, int baseColourAmount) {
        this.tweakedLigthmap = tweakedLigthmap;
        this.twinklingStars = twinklingStars;
        this.nebulas = nebulas;

        this.nebulaConfig = new NebulaConfig(nebulaType, nebulaStrength, nebulaNoiseAmount, nebulaNoiseScale, baseColourAmount);
    }

    private boolean tweakedLigthmap;
    private boolean twinklingStars;
    private boolean nebulas;

    private final NebulaConfig nebulaConfig;

    @Data
    public static final class NebulaConfig {
        public NebulaConfig(String nebulaType, float nebulaStrength, float nebulaNoiseAmount, float nebulaNoiseScale, int baseColourAmount) {
            this.nebulaType = nebulaType;
            this.nebulaStrength = nebulaStrength;
            this.nebulaNoiseAmount = nebulaNoiseAmount;
            this.nebulaNoiseScale = nebulaNoiseScale;
            this.baseColourAmount = baseColourAmount;
        }

        private String nebulaType;
        private float nebulaStrength;
        private float nebulaNoiseAmount;
        private float nebulaNoiseScale;
        private int baseColourAmount;
    }
}
