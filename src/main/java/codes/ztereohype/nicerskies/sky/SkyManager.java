package codes.ztereohype.nicerskies.sky;

import codes.ztereohype.nicerskies.NicerSkies;
import codes.ztereohype.nicerskies.config.ConfigManager;
import codes.ztereohype.nicerskies.core.Gradient;
import codes.ztereohype.nicerskies.mixin.LevelRendererAccessor;
import codes.ztereohype.nicerskies.mixin.LevelRendererInvoker;
import codes.ztereohype.nicerskies.sky.nebula.NebulaSkyboxPainter;
import codes.ztereohype.nicerskies.sky.nebula.Skybox;
import codes.ztereohype.nicerskies.sky.star.Starbox;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.synth.PerlinNoise;

import java.util.stream.IntStream;

public class SkyManager {
    private @Getter Starbox starbox;
    private @Getter Skybox skybox;

    private final Gradient starGradient = new Gradient();
    private final Gradient nebulaGradient = new Gradient();

    public void generateSky(long seed, boolean starsEnabled, boolean nebulaEnabled) {
        ConfigManager cm = NicerSkies.config;

        nebulaGradient.clear();
        starGradient.clear();

        buildGradients();

        RandomSource randomSource = RandomSource.create(seed);

        if (nebulaEnabled) {
            PerlinNoise perlinNoise = PerlinNoise.create(randomSource.fork(), IntStream.of(1, 2, 3, 4, 5, 6, 7));
            NebulaSkyboxPainter painter = new NebulaSkyboxPainter(perlinNoise, nebulaGradient, cm.getNebulaNoiseScale(), cm.getNebulaNoiseAmount(), cm.getNebulaBaseColourAmount());
            this.skybox = new Skybox(painter);
        }

        if (starsEnabled) {
            LevelRendererAccessor levelRenderer = (LevelRendererAccessor) Minecraft.getInstance().levelRenderer;
            starbox = new Starbox(randomSource, starGradient, levelRenderer.getStarBuffer());
            tick(levelRenderer.getTicks());
        } else {

            ((LevelRendererInvoker)Minecraft.getInstance().levelRenderer).nicerSkies_generateSky();
        }
    }

    public void tick(int ticks) {
        if (starbox != null) {
            this.starbox.updateStars(ticks);
        }
    }

    public void buildGradients() {
        starGradient.add(0.0f, 255, 179, 97);
        starGradient.add(0.2f, 255, 249, 253);
        starGradient.add(1.0f, 175, 199, 255);

        nebulaGradient.add(0.2f, 41, 83, 146);
        nebulaGradient.add(0.5f, 120, 47, 93);
        nebulaGradient.add(0.7f, 209, 58, 103);
        nebulaGradient.add(0.8f, 255, 160, 123);
        nebulaGradient.add(1.0f, 253, 194, 220);
    }
}
