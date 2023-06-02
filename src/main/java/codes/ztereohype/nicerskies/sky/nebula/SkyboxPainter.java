package codes.ztereohype.nicerskies.sky.nebula;

import net.minecraft.util.Mth;
import net.minecraft.world.level.levelgen.synth.PerlinNoise;

public abstract class SkyboxPainter {
    protected final PerlinNoise noise;

    SkyboxPainter(PerlinNoise noise) {
        this.noise = noise;
    }

    abstract int getTexelColour(float x, float y, float z);

    public float[] projectOnSphere(float x, float y, float z) {
        float invDistance = (float) Mth.fastInvSqrt(x * x + y * y + z * z);

        //divide by distance to get projection on sphere (shorten the vector)
        x *= invDistance;
        y *= invDistance;
        z *= invDistance;

        return new float[] {x, y, z};
    }
}
