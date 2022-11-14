package codes.ztereohype.example;

import codes.ztereohype.example.config.ConfigManager;
import codes.ztereohype.example.sky.SkyManager;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.impl.game.minecraft.launchwrapper.FabricClientTweaker;

import java.io.File;

public class NicerSkies implements ModInitializer {
    public static ConfigManager config = ConfigManager.fromFile(new File(FabricLoader.getInstance()
                                                                                     .getConfigDir()
                                                                                     .toFile(), "nicerskies.json"));
    public static SkyManager skyManager = new SkyManager();

    @Override
    public void onInitialize() {
    }
}
