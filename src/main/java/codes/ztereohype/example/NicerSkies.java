package codes.ztereohype.example;

import codes.ztereohype.example.sky.SkyManager;
import net.fabricmc.api.ModInitializer;

public class NicerSkies implements ModInitializer {
    public static boolean toggle = true;
    public static SkyManager skyManager = new SkyManager();

    @Override
    public void onInitialize() {}
}