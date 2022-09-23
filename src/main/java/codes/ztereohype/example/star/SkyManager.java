package codes.ztereohype.example.star;

import codes.ztereohype.example.ExampleMod;
import codes.ztereohype.example.Gradient;
import codes.ztereohype.example.nebula.NebulaSkyboxPainter;
import codes.ztereohype.example.nebula.Skybox;
import codes.ztereohype.example.nebula.SkyboxPainter;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.synth.ImprovedNoise;
import net.minecraft.world.level.levelgen.synth.PerlinNoise;

import java.awt.*;
import java.util.ArrayList;
import java.util.stream.IntStream;

import static codes.ztereohype.example.nebula.Skybox.RESOLUTION;

public class SkyManager {
    private static final Gradient STAR_GRADIENT = new Gradient();
    private static final Gradient NEBULA_GRADIENT = new Gradient();

    private static final BufferBuilder starBufferBuilder = Tesselator.getInstance().getBuilder();

    private static final int STARS = 1500;
    public static ArrayList<Star> starList = new ArrayList<>();

    public static void generateSky() {
        NEBULA_GRADIENT.clear();
        STAR_GRADIENT.clear();

        if (ExampleMod.nebulaSkybox == null) {
            ExampleMod.nebulaSkybox = new Skybox();
        }

        RandomSource randomSource = RandomSource.create(1234L); //todo: world seed/hash server ip

        buildGradients();

        PerlinNoise perlinNoise = PerlinNoise.create(randomSource, IntStream.of(1, 2, 3, 4, 5));
        NebulaSkyboxPainter painter = new NebulaSkyboxPainter(perlinNoise, NEBULA_GRADIENT);

        ExampleMod.nebulaSkybox.paint(painter);

        starList.clear();
        generateStars(randomSource);
    }

    public static void buildGradients() {
        STAR_GRADIENT.add(0.0f, 255, 179, 97);
        STAR_GRADIENT.add(0.2f, 255, 249, 253);
        STAR_GRADIENT.add(1.0f, 175, 199, 255);

        NEBULA_GRADIENT.add(0.2f, 41, 98, 146);
        NEBULA_GRADIENT.add(0.5f, 120, 59, 93);
        NEBULA_GRADIENT.add(0.7f, 209, 72, 103);
        NEBULA_GRADIENT.add(0.8f, 255, 200, 123);
        NEBULA_GRADIENT.add(1.0f, 253, 243, 220);

//        NEBULA_GRADIENT.add(0.0f, 128, 0, 0);
//        NEBULA_GRADIENT.add(0.4f, 128, 0, 0);
//        NEBULA_GRADIENT.add(0.5f, 128, 0, 0);
//        NEBULA_GRADIENT.add(0.7f, 128, 0, 0);
//        NEBULA_GRADIENT.add(1.0f, 128, 128, 128);
    }

    public static void generateStars(RandomSource randomSource) {
        ImprovedNoise noise = new ImprovedNoise(randomSource);

        for (int i = 0; i < STARS; ++i) {
            float randX = randomSource.nextFloat() * 2.0F - 1.0F;
            float randY = randomSource.nextFloat() * 2.0F - 1.0F;
            float randZ = randomSource.nextFloat() * 2.0F - 1.0F;

            float resizeSpeed = 0.03f + randomSource.nextFloat() * 0.04f;
            float spinSpeed = randomSource.nextFloat() * 0.02f - 0.01f;

            Color starColor = STAR_GRADIENT.getAt(randomSource.nextFloat());

            float starRadius = 0.15F + randomSource.nextFloat() * 0.15F;
            double starValue = noise.noise(randX*2.5f, randY*2.5f, randZ*2.5f) + 0.5;

            float squaredDistance = randX * randX + randY * randY + randZ * randZ;
            if (squaredDistance < 1.0 && squaredDistance > 0.01 && starValue > 0.2) {
                starList.add(new Star(randX, randY, randZ, starRadius, starColor, resizeSpeed, spinSpeed));
            } else --i;
        }
    }

    public static void updateStars(int ticks, VertexBuffer starBuffer) {
        starBufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);

        for (Star star : starList) {
            star.tick(ticks);
            star.setVertices(starBufferBuilder);
        }

        starBuffer.bind();
        starBuffer.upload(starBufferBuilder.end());
    }
}

