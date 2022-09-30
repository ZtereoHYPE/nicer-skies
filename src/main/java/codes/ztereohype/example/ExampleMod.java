package codes.ztereohype.example;

import codes.ztereohype.example.sky.SkyManager;
import codes.ztereohype.example.sky.nebula.NebulaSkyboxPainter;
import codes.ztereohype.example.sky.nebula.Skybox;
import com.mojang.realmsclient.util.JsonUtils;
import net.fabricmc.api.ModInitializer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.synth.PerlinNoise;

import java.util.stream.IntStream;

public class ExampleMod implements ModInitializer {
    public static boolean toggle = true;
    public static SkyManager skyManager = new SkyManager();

    @Override
    public void onInitialize() {
        skyManager.generateSky(123L);
    }
}
