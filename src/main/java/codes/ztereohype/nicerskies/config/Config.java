package codes.ztereohype.nicerskies.config;

import lombok.Data;

@Data
public class Config {
    private boolean tweakedLigthmap;
    private boolean twinklingStars;
    private boolean nebulas;

    private final NebulaConfig nebulaConfig;

    public Config(boolean tweakedLigthmap, boolean twinklingStars, boolean nebulas, String nebulaType, float nebulaStrength, float nebulaNoiseAmount, float nebulaNoiseScale, int baseColourAmount, boolean renderDuringDay) {
        this.tweakedLigthmap = tweakedLigthmap;
        this.twinklingStars = twinklingStars;
        this.nebulas = nebulas;

        this.nebulaConfig = new NebulaConfig(nebulaType, nebulaStrength, nebulaNoiseAmount, nebulaNoiseScale, baseColourAmount, renderDuringDay);
    }

    @Data
    public static final class NebulaConfig {
        public NebulaConfig(String nebulaType, float nebulaStrength, float nebulaNoiseAmount, float nebulaNoiseScale, int baseColourAmount, boolean renderDuringDay) {
            this.nebulaType = nebulaType;
            this.nebulaStrength = nebulaStrength;
            this.nebulaNoiseAmount = nebulaNoiseAmount;
            this.nebulaNoiseScale = nebulaNoiseScale;
            this.baseColourAmount = baseColourAmount;
            this.renderDuringDay = renderDuringDay;
        }

        private String nebulaType;
        private float nebulaStrength;
        private float nebulaNoiseAmount;
        private float nebulaNoiseScale;
        private int baseColourAmount;
        private boolean renderDuringDay;
    }
}
