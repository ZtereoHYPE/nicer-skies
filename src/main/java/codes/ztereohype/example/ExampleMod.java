package codes.ztereohype.example;

import codes.ztereohype.example.sky.SkyManager;
import net.fabricmc.api.ModInitializer;

public class ExampleMod implements ModInitializer {
    public static boolean toggle = true;
    public static SkyManager skyManager = new SkyManager();

    @Override
    public void onInitialize() {
        skyManager.generateSky(123L);
    }
}
