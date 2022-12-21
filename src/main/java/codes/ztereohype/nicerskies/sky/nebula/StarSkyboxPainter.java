package codes.ztereohype.nicerskies.sky.nebula;

import codes.ztereohype.nicerskies.core.Gradient;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.world.level.levelgen.synth.PerlinNoise;

public class StarSkyboxPainter extends SkyboxPainter {
    private static final float SCALING_FACTOR = 1.5f;
    private static final float BASE_NOISE_AMOUNT = 0.45f; // the amount of base noise to keep

    private final Gradient starryGradient;

    public StarSkyboxPainter(PerlinNoise noise, Gradient starryGradient) {
        super(noise);
        this.starryGradient = starryGradient;
    }

    @Override
    int getColour(float x, float y, float z) {
        float[] projCoords = this.projectOnSphere(x, y, z);
        x = projCoords[0];
        y = projCoords[1];
        z = projCoords[2];

        double noiseValue = Mth.clamp(noise.getValue(x * SCALING_FACTOR, y * SCALING_FACTOR, z * SCALING_FACTOR) + 0.5, 0D, 1D);

        int alpha = (int) (Mth.clamp((noiseValue * (1D / BASE_NOISE_AMOUNT) - (1D / BASE_NOISE_AMOUNT - 1)) * 35D, 1D, 255.99D)); // otherwise death occurs
        double colourValue = Mth.clamp((alpha / 255D), 0D, 1D);
        int[] color = starryGradient.getAt(colourValue);

        return FastColor.ARGB32.color(alpha, color[2], color[1], color[0]);
    }
}
