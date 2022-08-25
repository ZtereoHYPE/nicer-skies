package codes.ztereohype.example.sky;

import codes.ztereohype.example.ExampleMod;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.synth.ImprovedNoise;
import net.minecraft.world.level.levelgen.synth.PerlinNoise;

import java.util.ArrayList;
import java.util.stream.IntStream;

public class StarManager {
    private static final BufferBuilder starBufferBuilder = Tesselator.getInstance().getBuilder();

    private static final int STARS = 1500;
    private static final int NEBULAS_RESOLUTION = 256;
    private static final float SCALING_FACTOR = 1f;
    public static ArrayList<Star> starList = new ArrayList<>();

    // ez optimisation: use less vram by using only height 3 (requires pain uv mapping)
    public static final DynamicTexture skyTexture = new DynamicTexture(NEBULAS_RESOLUTION * 4, NEBULAS_RESOLUTION * 4, false);

    public static void generateNebulaTextures(RandomSource randomSource) {
        PerlinNoise perlinNoise = PerlinNoise.create(randomSource, IntStream.of(1, 2, 3, 4, 5));
        ImprovedNoise distortionNoise = new ImprovedNoise(randomSource);

        // top face
        NativeImage skyNativeTex = skyTexture.getPixels();
        for (int texY = 0; texY < NEBULAS_RESOLUTION; texY++) {
            for (int texX = 0; texX < NEBULAS_RESOLUTION; texX++) {
                float x = (texX / (float) NEBULAS_RESOLUTION) * 2 - 1;
                float y = 1;
                float z = (texY / (float) NEBULAS_RESOLUTION) * 2 - 1;

                skyNativeTex.setPixelRGBA(texX + 2 * NEBULAS_RESOLUTION, texY, getFunnyColour(x, y, z, perlinNoise, distortionNoise));
            }
        }

        // bottom face
        for (int texY = 0; texY < NEBULAS_RESOLUTION; texY++) {
            for (int texX = 0; texX < NEBULAS_RESOLUTION; texX++) {
                float x = (texX / (float) NEBULAS_RESOLUTION) * 2 - 1;
                float y = -1;
                float z = (texY / (float) NEBULAS_RESOLUTION) * 2 - 1;

                skyNativeTex.setPixelRGBA(texX + 2 * NEBULAS_RESOLUTION, texY + 2 * NEBULAS_RESOLUTION, getFunnyColour(x, y, z, perlinNoise, distortionNoise));
            }
        }

        // -x face
        for (int texY = 0; texY < NEBULAS_RESOLUTION; texY++) {
            for (int texX = 0; texX < NEBULAS_RESOLUTION; texX++) {
                float x = -1;
                float y = (texY / (float) NEBULAS_RESOLUTION) * 2 - 1;
                float z = (texX / (float) NEBULAS_RESOLUTION) * 2 - 1;

                skyNativeTex.setPixelRGBA(texX, texY + NEBULAS_RESOLUTION, getFunnyColour(x, y, z, perlinNoise, distortionNoise));
            }
        }

        // +x face
        for (int texY = 0; texY < NEBULAS_RESOLUTION; texY++) {
            for (int texX = 0; texX < NEBULAS_RESOLUTION; texX++) {
                float x = 1;
                float y = (texY / (float) NEBULAS_RESOLUTION) * 2 - 1;
                float z = (texX / (float) NEBULAS_RESOLUTION) * 2 - 1;

                skyNativeTex.setPixelRGBA(texX + 2 * NEBULAS_RESOLUTION, texY + NEBULAS_RESOLUTION, getFunnyColour(x, y, z, perlinNoise, distortionNoise));
            }
        }

        // +z face
        for (int texY = 0; texY < NEBULAS_RESOLUTION; texY++) {
            for (int texX = 0; texX < NEBULAS_RESOLUTION; texX++) {
                float x = (texX / (float) NEBULAS_RESOLUTION) * 2 - 1;
                float y = (texY / (float) NEBULAS_RESOLUTION) * 2 - 1;
                float z = 1;

                skyNativeTex.setPixelRGBA(texX + NEBULAS_RESOLUTION, texY + NEBULAS_RESOLUTION, getFunnyColour(x, y, z, perlinNoise, distortionNoise));
            }
        }

        // -z face
        for (int texY = 0; texY < NEBULAS_RESOLUTION; texY++) {
            for (int texX = 0; texX < NEBULAS_RESOLUTION; texX++) {
                float x = (texX / (float) NEBULAS_RESOLUTION) * 2 - 1;
                float y = (texY / (float) NEBULAS_RESOLUTION) * 2 - 1;
                float z = -1;

                skyNativeTex.setPixelRGBA(texX + 3 * NEBULAS_RESOLUTION, texY + NEBULAS_RESOLUTION, getFunnyColour(x, y, z, perlinNoise, distortionNoise));
            }
        }

        skyTexture.upload();
    }

    public static int getFunnyColour(float x, float y, float z, PerlinNoise noise, ImprovedNoise distortionNoise) {
        float invDistance = Mth.fastInvSqrt(x * x + y * y + z * z);

        //divide by distance to get projection on sphere (shorten the vector)
        x *= invDistance;
        y *= invDistance;
        z *= invDistance;

        float offset = (float) noise.getValue(x * SCALING_FACTOR * 3, y * SCALING_FACTOR * 3, z * SCALING_FACTOR * 3);

        x += offset/6f;
        y += offset/6f;
        z += offset/6f;

        // 0..1
        double noiseValue = noise.getValue(x * SCALING_FACTOR, y * SCALING_FACTOR, z * SCALING_FACTOR) * 0.5 + 0.5;
//        float temperature = (float) (Math.max(Math.max(derivates[0], derivates[1]), derivates[2]) * 2500 + 3500);



        int alpha = Mth.clamp((int) (noiseValue * 255) - 128, 0, 128);

        // 0..1
        double colourLerp = (alpha/128.0)*2;

        int red = (int) (Mth.lerp(colourLerp, 1.0, 0.2) * 255);
        int green = (int) (Mth.lerp(colourLerp, 0.7, 0.7) * 255);
        int blue = (int) (Mth.lerp(colourLerp, 0.2, 0.9) * 255);

        return  FastColor.ARGB32.color(alpha, red, green, blue);
//                                                        Star.getBlueFromKelvin(noiseLerp*7000),
//                                                        Star.getGreenFromKelvin(noiseLerp*7000),
//                                                        Star.getRedFromKelvin(noiseLerp*7000));
//                blue, green, red);
    }

    public static void generateSky() {
        RandomSource randomSource = RandomSource.create(123L);

        generateNebulaTextures(randomSource);

        ImprovedNoise noise = new ImprovedNoise(randomSource);

        starList.clear();

        for (int i = 0; i < STARS; ++i) {
            float randX = randomSource.nextFloat() * 2.0F - 1.0F;
            float randY = randomSource.nextFloat() * 2.0F - 1.0F;
            float randZ = randomSource.nextFloat() * 2.0F - 1.0F;

            float twinkleSpeed = 0.03f + randomSource.nextFloat() * 0.04f;

            float temperature = (randomSource.nextFloat() * 10f + 10f) * 600;

            float starRadius = 0.20F + randomSource.nextFloat() * 0.15F;
            float starValue = (float) noise.noise(randX*2.5f, randY*2.5f, randZ*2.5f) * 0.55f + 0.45f;
            starRadius *= starValue;

            float squaredDistance = randX * randX + randY * randY + randZ * randZ;

            if (squaredDistance < 1.0 && squaredDistance > 0.01 && starRadius > 0.13) {
                starList.add(new Star(randX, randY, randZ, starRadius, temperature, twinkleSpeed));
            } else --i;
        }
    }

    public static void updateStars(int ticks, VertexBuffer starBuffer) {
        if (!ExampleMod.toggle) return;

        starBufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);

        for (Star star : starList) {
            star.tick(ticks);
            star.setVertices(starBufferBuilder);
        }

        starBuffer.bind();
        starBuffer.upload(starBufferBuilder.end());
        VertexBuffer.unbind();
    }
}

