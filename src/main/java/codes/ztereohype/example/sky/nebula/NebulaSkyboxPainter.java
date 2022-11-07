package codes.ztereohype.example.sky.nebula;

import codes.ztereohype.example.core.Gradient;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.world.level.levelgen.synth.PerlinNoise;

import java.awt.Color;

public class NebulaSkyboxPainter extends SkyboxPainter {
    private static final float SCALING_FACTOR = 1f;
    private static final float BASE_NOISE_AMOUNT = 0.56f; // the amount of base noise to keep

    private final Gradient nebulaGradient;

    public NebulaSkyboxPainter(PerlinNoise noise, Gradient nebula_gradient) {
        super(noise);
        nebulaGradient = nebula_gradient;
    }

    @Override
    int getColour(float x, float y, float z) {
        // Get projection
        float[] projCoords = this.projectOnSphere(x, y, z);
        x = projCoords[0];
        y = projCoords[1];
        z = projCoords[2];

        // Get offset
        float offset = (float) noise.getValue(x * SCALING_FACTOR * 3, y * SCALING_FACTOR * 3, z * SCALING_FACTOR * 3);
        x = Mth.clamp(x + offset/5f, -1f, 1f);
        y = Mth.clamp(y + offset/5f, -1f, 1f);
        z = Mth.clamp(z + offset/5f, -1f, 1f);

        // Value of noise at coord, 0..1
        double noiseValue = Mth.clamp(noise.getValue(x * SCALING_FACTOR, y * SCALING_FACTOR, z * SCALING_FACTOR) + 0.5, 0, 1);

        // Value to be subtracted from noise at coord, 0..1
        double subtractionValue = Mth.clamp(noise.getOctaveNoise(1).noise(x * SCALING_FACTOR, y * SCALING_FACTOR, z * SCALING_FACTOR) + 0.5, 0D, 1D);

        double[] ds = new double[3];
        noise.getOctaveNoise(0).noiseWithDerivative(x * SCALING_FACTOR, y * SCALING_FACTOR, z * SCALING_FACTOR, ds);

        // Find a base background colour to use (xyz interpoaltion across sky, gamer mode)
        int baseB = (int) ((x/2 + 0.5) * 127);
        int baseG = (int) ((y/2 + 0.5) * 127);
        int baseR = (int) ((z/2 + 0.5) * 127);

        int alpha = (int)(Mth.clamp((noiseValue * (1 / BASE_NOISE_AMOUNT) - (1 / BASE_NOISE_AMOUNT - 1)) * 255, 20, 254.99f)); // otherwise death occurs

        alpha = (int) Mth.clamp(alpha - subtractionValue * 128, 50, 255);

        double nebulaFactor = (Mth.clamp((noiseValue * (1D / BASE_NOISE_AMOUNT) - (1D / BASE_NOISE_AMOUNT - 1)), 0.01, 0.9999));
        Color nebula = nebulaGradient.getAt(nebulaFactor);
        double bgFactor = Mth.clamp(Math.log10(-nebulaFactor + 1) + 1, 0, 1);

        int r = Mth.clamp((int) ((nebulaFactor * nebula.getRed()) + baseR * bgFactor) - (int)(ds[0] * nebulaFactor * 127), 0, 255);
        int g = Mth.clamp((int) ((nebulaFactor * nebula.getGreen()) + baseG * bgFactor) - (int)(ds[1] * nebulaFactor * 64), 0, 255);
        int b = Mth.clamp((int) ((nebulaFactor * nebula.getBlue()) + baseB * bgFactor) - (int)(ds[2] * nebulaFactor * 127), 0, 255);

        return FastColor.ARGB32.color(alpha, b, g, r);
    }
}
