package codes.ztereohype.nicerskies.config;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ConfigManager {
    public static final Config DEFAULT_CONFIG = new Config(false, true, true, NebulaType.RAINBOW.getTypeString(), 1f, 0.5f, 1f, 128);

    private static final Gson gson = new Gson();
    private final Config config;
    private final File file;

    public static ConfigManager fromFile(File file) {
        Config config;
        try {
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();

                config = cloneConfig(DEFAULT_CONFIG);

                gson.toJson(config, new FileWriter(file));
            } else {
                config = gson.fromJson(new FileReader(file), Config.class);

                if (config == null) {
                    throw new IOException("Failed to read config file");
                }
            }
        } catch (IOException e) {
            // todo setup logger properly
            e.printStackTrace();
            config = cloneConfig(DEFAULT_CONFIG);
        }

        return new ConfigManager(config, file);
    }

    private ConfigManager(Config config, File file) {
        this.config = config;
        this.file = file;
        save(file);
    }

    public boolean getLightmapTweaked() {
        return config.isTweakedLigthmap();
    }

    public boolean getTwinklingStars() {
        return config.isTwinklingStars();
    }

    public boolean getNebulas() {
        return config.isNebulas();
    }

    public NebulaType getNebulaType() {
        return NebulaType.valueOf(config.getNebulaConfig().getNebulaType().toUpperCase());
    }

    public float getNebulaStrength() {
        return config.getNebulaConfig().getNebulaStrength();
    }

    public float getNebulaNoiseAmount() {
        return config.getNebulaConfig().getNebulaNoiseAmount();
    }

    public float getNebulaNoiseScale() {
        return config.getNebulaConfig().getNebulaNoiseScale();
    }

    public int getNebulaBaseColourAmount() {
        return config.getNebulaConfig().getBaseColourAmount();
    }

    public void setLightmapTweaked(boolean tweaked) {
        config.setTweakedLigthmap(tweaked);
        save(file);
    }

    public void setTwinklingStars(boolean twinkling) {
        config.setTwinklingStars(twinkling);
        save(file);
    }

    public void setNebulas(boolean nebulas) {
        config.setNebulas(nebulas);
        save(file);
    }

    public void setNebulaType(NebulaType type) {
        config.getNebulaConfig().setNebulaType(type.getTypeString());
        save(file);
    }

    public void setNebulaStrength(float strength) {
        config.getNebulaConfig().setNebulaStrength(strength);
        save(file);
    }

    public void setNebulaNoiseAmount(float amount) {
        config.getNebulaConfig().setNebulaNoiseAmount(amount);
        save(file);
    }

    public void setNebulaNoiseScale(float scale) {
        config.getNebulaConfig().setNebulaNoiseScale(scale);
        save(file);
    }

    public void setNebulaBaseColourAmount(int amount) {
        config.getNebulaConfig().setBaseColourAmount(amount);
        save(file);
    }

    public void save(File file) {
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(gson.toJson(config));
//            System.out.println("Saved config " + );
        } catch (IOException e) {
            e.printStackTrace();
            // todo setup logger properly
//            LogManager.getLogManager().getLogger("NicerSkies").warning("Failed to save config file!");
        }
    }

    private static Config cloneConfig(Config config) {
        return gson.fromJson(gson.toJson(config), Config.class);
    }

    public boolean nebulaConfigEquals(Config config) {
        return this.config.getNebulaConfig().equals(config.getNebulaConfig());
    }

    public void resetNebulaSettings() {
        config.getNebulaConfig().setNebulaType(DEFAULT_CONFIG.getNebulaConfig().getNebulaType());
        config.getNebulaConfig().setNebulaStrength(DEFAULT_CONFIG.getNebulaConfig().getNebulaStrength());
        config.getNebulaConfig().setNebulaNoiseAmount(DEFAULT_CONFIG.getNebulaConfig().getNebulaNoiseAmount());
        config.getNebulaConfig().setNebulaNoiseScale(DEFAULT_CONFIG.getNebulaConfig().getNebulaNoiseScale());
        config.getNebulaConfig().setBaseColourAmount(DEFAULT_CONFIG.getNebulaConfig().getBaseColourAmount());
        save(file);
    }
}
