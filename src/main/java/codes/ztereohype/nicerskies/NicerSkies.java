package codes.ztereohype.nicerskies;

import codes.ztereohype.nicerskies.config.ConfigManager;
import codes.ztereohype.nicerskies.sky.SkyManager;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class NicerSkies implements ModInitializer {
    public static final Logger LOGGER = LogManager.getLogManager().getLogger("NicerSkies");

    public static ConfigManager config = ConfigManager.fromFile(new File(FabricLoader.getInstance()
                                                                                     .getConfigDir()
                                                                                     .toFile(), "nicerskies.json"));
    public static SkyManager skyManager = new SkyManager();


    @Override
    public void onInitialize() {
    }
}
