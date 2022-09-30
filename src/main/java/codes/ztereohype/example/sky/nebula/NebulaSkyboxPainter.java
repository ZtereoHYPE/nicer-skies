package codes.ztereohype.example.sky.nebula;

import codes.ztereohype.example.Gradient;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.world.level.levelgen.synth.PerlinNoise;

import java.awt.*;

public class NebulaSkyboxPainter extends SkyboxPainter {
    private static final float SCALING_FACTOR = 1f;
    private static final float BASE_NOISE_AMOUNT = 0.7f; // the amount of base noise to keep

    private final Gradient nebulaGradient;

    public NebulaSkyboxPainter(PerlinNoise noise, Gradient nebula_gradient) {
        super(noise);
        nebulaGradient = nebula_gradient;
    }

    @Override
    int getColour(float x, float y, float z) {
        float[] projCoords = this.projectOnSphere(x, y, z);
        x = projCoords[0];
        y = projCoords[1];
        z = projCoords[2];

        float offset = (float) noise.getValue(x * SCALING_FACTOR * 3, y * SCALING_FACTOR * 3, z * SCALING_FACTOR * 3);

        x += offset/5f;
        y += offset/5f;
        z += offset/5f;

        // 0..1
        double noiseValue = Mth.clamp(noise.getValue(x * SCALING_FACTOR, y * SCALING_FACTOR, z * SCALING_FACTOR) + 0.5, 0D, 1D);

        // 0..1
        double subtractionValue = Mth.clamp(noise.getOctaveNoise(1).noise(x * SCALING_FACTOR, y * SCALING_FACTOR, z * SCALING_FACTOR) + 0.5, 0D, 1D);

//        double[] derivates = new double[3];
//        noise.getOctaveNoise(0).noiseWithDerivative(x * SCALING_FACTOR, y * SCALING_FACTOR, z * SCALING_FACTOR, derivates);
//        double maxDerivative = Mth.clamp(Math.max(Math.max(derivates[0], derivates[1]), derivates[2]) * 0.5 + 0.5, 0, 0);

        int alpha = (int)(Mth.clamp((noiseValue * (1D / BASE_NOISE_AMOUNT) - (1D / BASE_NOISE_AMOUNT - 1)) * 255D, 1D, 254.99D)); // otherwise death occurs

        alpha = (int) Mth.clamp(alpha - subtractionValue * 128, 0, 255); //todo subtract colour channels separately

        double colourValue = Mth.clamp((alpha / 255D), 0D, 1D);

        Color color = nebulaGradient.getAt(colourValue);

        return FastColor.ARGB32.color(alpha, color.getBlue(), color.getGreen(), color.getRed());
    }
}
