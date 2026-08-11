package dev.ztereohype.nicerskies;

import dev.ztereohype.nicerskies.config.Config;
import dev.ztereohype.nicerskies.sky.NicerSkiesRenderer;
import lombok.Getter;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

@Getter
public class NicerSkies implements ClientModInitializer {
    public static final String MOD_ID = "nicer-skies";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    private static NicerSkies INSTANCE;

    private Config config;
    private NicerSkiesRenderer renderer;

    @Override
    public void onInitializeClient() {
        INSTANCE = this;

        this.config = Config.fromFile(new File(FabricLoader.getInstance()
                                                           .getConfigDir()
                                                           .toFile(), "nicerskies.json"));

        this.renderer = new NicerSkiesRenderer();
    }

    public static NicerSkies getInstance() {
        return INSTANCE;
    }
}
