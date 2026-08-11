package dev.ztereohype.nicerskies.sky;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.shaders.UniformType;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import dev.ztereohype.nicerskies.NicerSkies;
import dev.ztereohype.nicerskies.config.Config;
import dev.ztereohype.nicerskies.core.Gradient;
import dev.ztereohype.nicerskies.core.HashedSeedManager;
import dev.ztereohype.nicerskies.sky.nebula.DebugSkyboxPainter;
import dev.ztereohype.nicerskies.sky.nebula.NebulaSkyboxPainter;
import dev.ztereohype.nicerskies.sky.nebula.Skybox;
import dev.ztereohype.nicerskies.sky.nebula.SkyboxPainter;
import dev.ztereohype.nicerskies.sky.star.Starbox;
import lombok.Getter;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.synth.PerlinNoise;

import java.util.stream.IntStream;


@Getter
public class NicerSkiesRenderer {
    private @Getter Starbox starbox;
    private Skybox skybox;

    private int ticks = 42;

    private final Gradient starGradient = new Gradient() {{
        add(0.0f, 255, 179, 97);
        add(0.2f, 255, 249, 253);
        add(1.0f, 175, 199, 255);
    }};

    private final Gradient nebulaGradient = new Gradient() {{
        add(0.2f, 41, 83, 146);
        add(0.5f, 120, 47, 93);
        add(0.7f, 209, 58, 103);
        add(0.8f, 255, 160, 123);
        add(1.0f, 253, 194, 220);
    }};

    public void buildBuffers() {
        this.starbox = new Starbox();
        this.skybox = new Skybox();
    }

    public boolean hasBuiltBuffers() {
        return this.starbox != null && this.skybox != null;
    }

    public void generateSky(long seed) {
        RandomSource randomSource = RandomSource.create(seed);

        Config cm = NicerSkies.getInstance().getConfig();

        if (cm.areNebulasEnabled()) {
            PerlinNoise perlinNoise = PerlinNoise.create(randomSource.fork(), IntStream.of(1, 2, 3, 4, 5, 6, 7));
            float noiseScale = cm.getNebulaNoiseScale();
            float noiseAmount = cm.getNebulaNoiseAmount();
            int bgStrength = cm.getNebulaBaseColourAmount();

            SkyboxPainter painter = new NebulaSkyboxPainter(perlinNoise, nebulaGradient, noiseScale, noiseAmount, bgStrength);
            this.skybox.paint(painter);
        }

        if (cm.areTwinlkingStarsEnabled()) {
            this.starbox.generateStars(randomSource, starGradient);
        }
    }

    public void tick() {
        ticks++;
        if (starbox != null) {
            this.starbox.updateStars(ticks);
        }
    }
}
