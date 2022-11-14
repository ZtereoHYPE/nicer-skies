package codes.ztereohype.example.config;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.LogManager;

public class ConfigManager {
    private static final Gson gson = new Gson();
    private final Config config;
    private final File file;

    public static ConfigManager fromFile(File file) {
        Config config;
        try {
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();

                config = new Config(false, true, true, "rainbow");

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
            config = new Config(false, true, true, "rainbow");
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
        return NebulaType.getFromString(config.getNebulaConfig().getNebulaType());
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
}
