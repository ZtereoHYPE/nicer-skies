package dev.ztereohype.nicerskies.sky.nebula;

import dev.ztereohype.nicerskies.core.Gradient;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.world.level.levelgen.synth.PerlinNoise;

public class NebulaSkyboxPainter extends SkyboxPainter {
    private final float scalingFactor;
    private final float noiseAmount; // the amount of base noise to keep
    private final int baseColourStrength;

    private final Gradient nebulaGradient;

    public NebulaSkyboxPainter(PerlinNoise noise, Gradient nebulaGradient, float scalingFactor, float noiseAmount, int baseColourStrength) {
        super(noise);
        this.nebulaGradient = nebulaGradient;

        this.scalingFactor = scalingFactor;
        this.noiseAmount = noiseAmount;
        this.baseColourStrength = baseColourStrength;
    }

    @Override
    int getTexelColour(float x, float y, float z) {
        // Get projection
        float[] projCoords = this.projectOnSphere(x, y, z);
        x = projCoords[0];
        y = projCoords[1];
        z = projCoords[2];

        // Get offset
        float offset = (float) noise.getValue(x * scalingFactor * 3, y * scalingFactor * 3, z * scalingFactor * 3);
        x = Mth.clamp(x + offset / 5f, -1f, 1f);
        y = Mth.clamp(y + offset / 5f, -1f, 1f);
        z = Mth.clamp(z + offset / 5f, -1f, 1f);

        // Value of noise at coord, 0..1
        double noiseValue = Mth.clamp(noise.getValue(x * scalingFactor, y * scalingFactor, z * scalingFactor) + 0.5, 0, 1);

        // Get the derivatives of the first (largest) octave of noise to shift the colour around with.
        double[] ds = new double[3];
        noise.getOctaveNoise(0).noiseWithDerivative(x * scalingFactor, y * scalingFactor, z * scalingFactor, ds);

        // Find a base background colour to use (xyz interpoaltion across sky, gamer mode)
        int baseB = (int) ((x / 2 + 0.5) * baseColourStrength);
        int baseG = (int) ((y / 2 + 0.5) * baseColourStrength);
        int baseR = (int) ((z / 2 + 0.5) * baseColourStrength);

        // Turn off nebula rendering if noiseAmount is 0. (user expected behaviour)
        double nebulaFactor;
        if (noiseAmount != 0) {
            nebulaFactor = (Mth.clamp((noiseValue * (1D / noiseAmount) - (1D / noiseAmount - 1)), 0, 0.99));
        } else {
            nebulaFactor = 0;
        }

        // Get the colour of the nebula and the amount of background colour to show at the current factor.
        int[] nebula = nebulaGradient.getAt(nebulaFactor);
        double bgFactor = Mth.clamp(Math.log10(-nebulaFactor + 1) + 1, 0, 1);

        // Merge everything:
        // colour = nebulaFactor * [colour of nebula in that pixel] - [noise derivative in that pixel] * nebulaFactor + background colour * bgFactor
        int r = Mth.clamp((int) (nebulaFactor * nebula[0] - ds[0] * nebulaFactor * 128 + baseR * bgFactor), 0, 255);
        int g = Mth.clamp((int) (nebulaFactor * nebula[1] - ds[1] * nebulaFactor * 64  + baseG * bgFactor), 0, 255);
        int b = Mth.clamp((int) (nebulaFactor * nebula[2] - ds[2] * nebulaFactor * 128 + baseB * bgFactor), 0, 255);

        // Get the alpha depending on the background factor
        int alpha = Mth.clamp((int) ((1 - bgFactor) * 255), 50, 255);

        return FastColor.ARGB32.color(alpha, b, g, r);
    }
}
