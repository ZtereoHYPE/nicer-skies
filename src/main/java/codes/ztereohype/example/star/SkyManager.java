package codes.ztereohype.example.star;

import codes.ztereohype.example.ExampleMod;
import codes.ztereohype.example.Gradient;
import codes.ztereohype.example.nebula.Skybox;
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
    private static final float SCALING_FACTOR = 1f;
    public static ArrayList<Star> starList = new ArrayList<>();

    public static void generateSky() {
        NEBULA_GRADIENT.clear();
        STAR_GRADIENT.clear();

        if (ExampleMod.nebulaSkybox == null) {
            ExampleMod.nebulaSkybox = new Skybox();
        }

        RandomSource randomSource = RandomSource.create(1234L); //todo: world seed/hash server ip

        buildGradients();

        generateNebulaTextures(randomSource);

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
            float spinSpeed = randomSource.nextFloat() * 0.02f - 0.01f; // wtf is this?

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
//        VertexBuffer.unbind();
    }

    public static void generateNebulaTextures(RandomSource randomSource) {
        PerlinNoise perlinNoise = PerlinNoise.create(randomSource, IntStream.of(1, 2, 3, 4, 5));
        ImprovedNoise distortionNoise = new ImprovedNoise(randomSource);
        NativeImage skyNativeTex = ExampleMod.nebulaSkybox.skyTexture.getPixels();

//        for (int face = 0; face < 6; ++face) {
//            for (int texY = 0; texY < NEBULAS_RESOLUTION; texY++) {
//                for (int texX = 0; texX < NEBULAS_RESOLUTION; texX++) {
//                    float x = (texX / (float) NEBULAS_RESOLUTION) * 2 - 1;
//                    float y = 1;
//                    float z = (texY / (float) NEBULAS_RESOLUTION) * 2 - 1;
//
//
//
//                    skyNativeTex.setPixelRGBA(texX + 2 * NEBULAS_RESOLUTION, texY, getFunnyColour(x, y, z, perlinNoise, distortionNoise));
//                }
//            }
//        }

        // top face
        for (int texY = 0; texY < RESOLUTION; texY++) {
            for (int texX = 0; texX < RESOLUTION; texX++) {
                float x = (texX / (float) RESOLUTION) * 2 - 1;
                float y = 1;
                float z = (texY / (float) RESOLUTION) * 2 - 1;

                skyNativeTex.setPixelRGBA(texX + 2 * RESOLUTION, texY, getFunnyColour(x, y, z, perlinNoise, distortionNoise));
            }
        }

        // bottom face
        for (int texY = 0; texY < RESOLUTION; texY++) {
            for (int texX = 0; texX < RESOLUTION; texX++) {
                float x = (texX / (float) RESOLUTION) * 2 - 1;
                float y = -1;
                float z = (texY / (float) RESOLUTION) * 2 - 1;

                skyNativeTex.setPixelRGBA(texX + 2 * RESOLUTION, texY + 2 * RESOLUTION, getFunnyColour(x, y, z, perlinNoise, distortionNoise));
            }
        }

        // -x face
        for (int texY = 0; texY < RESOLUTION; texY++) {
            for (int texX = 0; texX < RESOLUTION; texX++) {
                float x = -1;
                float y = (texY / (float) RESOLUTION) * 2 - 1;
                float z = (texX / (float) RESOLUTION) * 2 - 1;

                skyNativeTex.setPixelRGBA(texX, texY + RESOLUTION, getFunnyColour(x, y, z, perlinNoise, distortionNoise));
            }
        }

        // +x face
        for (int texY = 0; texY < RESOLUTION; texY++) {
            for (int texX = 0; texX < RESOLUTION; texX++) {
                float x = 1;
                float y = (texY / (float) RESOLUTION) * 2 - 1;
                float z = (texX / (float) RESOLUTION) * 2 - 1;

                skyNativeTex.setPixelRGBA(texX + 2 * RESOLUTION, texY + RESOLUTION, getFunnyColour(x, y, z, perlinNoise, distortionNoise));
            }
        }

        // +z face
        for (int texY = 0; texY < RESOLUTION; texY++) {
            for (int texX = 0; texX < RESOLUTION; texX++) {
                float x = (texX / (float) RESOLUTION) * 2 - 1;
                float y = (texY / (float) RESOLUTION) * 2 - 1;
                float z = 1;

                skyNativeTex.setPixelRGBA(texX + RESOLUTION, texY + RESOLUTION, getFunnyColour(x, y, z, perlinNoise, distortionNoise));
            }
        }

        // -z face
        for (int texY = 0; texY < RESOLUTION; texY++) {
            for (int texX = 0; texX < RESOLUTION; texX++) {
                float x = (texX / (float) RESOLUTION) * 2 - 1;
                float y = (texY / (float) RESOLUTION) * 2 - 1;
                float z = -1;

                skyNativeTex.setPixelRGBA(texX + 3 * RESOLUTION, texY + RESOLUTION, getFunnyColour(x, y, z, perlinNoise, distortionNoise));
            }
        }

        ExampleMod.nebulaSkybox.skyTexture.upload();
    }

    public static int getFunnyColour(float x, float y, float z, PerlinNoise noise, ImprovedNoise subtractionNoise) {
        double baseNoiseAmount = 0.7f; // the amount of base noise to keep

        float invDistance = Mth.fastInvSqrt(x * x + y * y + z * z);

        //divide by distance to get projection on sphere (shorten the vector)
        x *= invDistance;
        y *= invDistance;
        z *= invDistance;

        float offset = (float) noise.getValue(x * SCALING_FACTOR * 3, y * SCALING_FACTOR * 3, z * SCALING_FACTOR * 3);

        x += offset/5f;
        y += offset/5f;
        z += offset/5f;

        // 0..1
        double noiseValue = Mth.clamp(noise.getValue(x * SCALING_FACTOR, y * SCALING_FACTOR, z * SCALING_FACTOR) + 0.5, 0D, 1D);
        double subtractionValue = Mth.clamp(subtractionNoise.noise(x * SCALING_FACTOR, y * SCALING_FACTOR, z * SCALING_FACTOR) + 0.5, 0D, 1D);

//        double[] derivates = new double[3];
//        noise.getOctaveNoise(0).noiseWithDerivative(x * SCALING_FACTOR, y * SCALING_FACTOR, z * SCALING_FACTOR, derivates);
//        double maxDerivative = Mth.clamp(Math.max(Math.max(derivates[0], derivates[1]), derivates[2]) * 0.5 + 0.5, 0, 0);

        int alpha = (int)(Mth.clamp((noiseValue * (1D / baseNoiseAmount) - (1D / baseNoiseAmount - 1)) * 255D, 1D, 254.99D)); // otherwise death occurs

        alpha = (int) Mth.clamp(alpha - subtractionValue * 255, 0, 255); //todo subtract colour channels separately

        double colourValue = Mth.clamp((alpha / 255D), 0D, 1D);

        Color color = NEBULA_GRADIENT.getAt(colourValue);

        return FastColor.ARGB32.color(alpha, color.getBlue(), color.getGreen(), color.getRed());
//        return FastColor.ARGB32.color(255, 255, 255, 255);
    }
}

