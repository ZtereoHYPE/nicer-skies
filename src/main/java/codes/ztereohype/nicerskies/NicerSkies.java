package codes.ztereohype.nicerskies;

import codes.ztereohype.nicerskies.config.Config;
import codes.ztereohype.nicerskies.sky.SkyManager;
import lombok.Getter;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.util.logging.LogManager;
import java.util.logging.Logger;

@Getter
public class NicerSkies implements ClientModInitializer {
    public static final Logger LOGGER = LogManager.getLogManager().getLogger("nicer-skies");
    private static NicerSkies INSTANCE;

    private Config config;
    private SkyManager skyManager;

    @Override
    public void onInitializeClient() {
        INSTANCE = this;

        this.config = Config.fromFile(new File(FabricLoader.getInstance()
                                                           .getConfigDir()
                                                           .toFile(), "nicerskies.json"));

        this.skyManager = new SkyManager();
    }

    public static NicerSkies getInstance() {
        return INSTANCE;
    }
}
