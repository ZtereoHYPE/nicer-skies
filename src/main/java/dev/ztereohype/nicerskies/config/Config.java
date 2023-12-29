package dev.ztereohype.nicerskies.config;

import dev.ztereohype.nicerskies.NicerSkies;
import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Config {
    public static final ConfigData DEFAULT_CONFIG = new ConfigData(false,
            true,
            true,
            1f,
            0.5f,
            1f,
            128,
            false,
            false
    );

    private static final Gson gson = new Gson();
    private final File file;

    @Getter
    private ConfigData configData;

    public static Config fromFile(File file) {
        ConfigData config;
        try {
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();

                config = DEFAULT_CONFIG.toBuilder().build();

                gson.toJson(config, new FileWriter(file));
            } else {
                config = gson.fromJson(new FileReader(file), ConfigData.class);

                if (config == null) {
                    throw new IOException("Failed to read config file");
                }
            }
        } catch (IOException e) {
            NicerSkies.LOGGER.warning("Failed to read config file, falling back to default config.");
            e.printStackTrace();
            config = DEFAULT_CONFIG.toBuilder().build();
        }

        return new Config(config, file);
    }

    private Config(ConfigData configData, File file) {
        this.configData = configData;
        this.file = file;
        save(file);
    }

    public boolean getLightmapTweaked() {
        return configData.isLightmapTweaked();
    }

    public boolean areTwinlkingStarsEnabled() {
        return configData.isTwinklingStars();
    }

    public boolean areNebulasEnabled() {
        return configData.isRenderNebulas();
    }

    public float getNebulaStrength() {
        return configData.getNebulaConfig().getNebulaStrength();
    }

    public float getNebulaNoiseAmount() {
        return configData.getNebulaConfig().getNebulaNoiseAmount();
    }

    public float getNebulaNoiseScale() {
        return configData.getNebulaConfig().getNebulaNoiseScale();
    }

    public int getNebulaBaseColourAmount() {
        return configData.getNebulaConfig().getBaseColourAmount();
    }

    public boolean getRenderDuringDay() {
        return configData.getNebulaConfig().isRenderDuringDay();
    }

    public boolean renderInOtherDimensions() {
        return configData.isNebulasInOtherDimensions();
    }

    public void updateConfig(ConfigData configData) {
        this.configData = configData.toBuilder().build();
        this.configData.setNebulaConfig(configData.getNebulaConfig().toBuilder().build());
        save(file);
    }

    public void save(File file) {
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(gson.toJson(configData));
        } catch (IOException e) {
            e.printStackTrace();
            NicerSkies.LOGGER.warning("Failed to save config file!");
        }
    }

    @Data
    @AllArgsConstructor
    @Builder(toBuilder = true)
    public final static class ConfigData {
        private boolean lightmapTweaked;
        private boolean twinklingStars;
        private boolean renderNebulas;
        private boolean nebulasInOtherDimensions;

        private NebulaConfigData nebulaConfig;

        private ConfigData(boolean tweakedLigthmap, boolean twinklingStars, boolean nebulas, float nebulaStrength, float nebulaNoiseAmount, float nebulaNoiseScale, int baseColourAmount, boolean renderDuringDay, boolean renderInOtherDimensions) {
            this.lightmapTweaked = tweakedLigthmap;
            this.twinklingStars = twinklingStars;
            this.renderNebulas = nebulas;
            this.nebulasInOtherDimensions = renderInOtherDimensions;

            this.nebulaConfig = new NebulaConfigData(nebulaStrength, nebulaNoiseAmount, nebulaNoiseScale, baseColourAmount, renderDuringDay);
        }

        @Data
        @AllArgsConstructor
        @Builder(toBuilder = true)
        public static final class NebulaConfigData {
            private float nebulaStrength;
            private float nebulaNoiseAmount;
            private float nebulaNoiseScale;
            private int baseColourAmount;
            private boolean renderDuringDay;
        }
    }
}
