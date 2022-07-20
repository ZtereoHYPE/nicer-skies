package codes.ztereohype.example;

import net.minecraft.util.Mth;

import java.util.ArrayList;
import java.util.List;

public class Star {
    private final float xCoord;
    private final float yCoord;
    private final float zCoord;

    private final float sinPolarAngle;
    private final float cosPolarAngle;

    private final float projSin;
    private final float projCos;

    private final float twinkleSpeed;
    private final float minRadius;
    private final float maxRadius;

    private float currentAngle;
    private float currentRadius;

    public Star(float randX, float randY, float randZ, float radius, float twinkleSpeed) {
        float invsqrtDistance = Mth.fastInvSqrt(randX * randX + randY * randY + randZ * randZ);
        this.xCoord = randX * invsqrtDistance * 100.0F;
        this.yCoord = randY * invsqrtDistance * 100.0F;
        this.zCoord = randZ * invsqrtDistance * 100.0F;

        double polarAngle = Math.atan2(randX, randZ);
        this.sinPolarAngle = (float) Math.sin(polarAngle);
        this.cosPolarAngle = (float) Math.cos(polarAngle);

        // magic projection fuckery??
        double proj = Math.atan2(Math.sqrt(randX * randX + randZ * randZ), randY);
        this.projSin = (float) Math.sin(proj);
        this.projCos = (float) Math.cos(proj);

        this.twinkleSpeed = twinkleSpeed;
        this.minRadius = radius - 0.15f;
        this.maxRadius = radius + 0.15f;
        this.currentRadius = radius;
        this.currentAngle = twinkleSpeed; //just so they dont all start straight
    }

    public void tick(int ticks) {
        currentAngle += 0.007f * twinkleSpeed;
        currentRadius = Mth.lerp(Mth.sin(ticks * twinkleSpeed / 10f) * 0.5f + 0.5f, minRadius, maxRadius);
    }

    //return 4*3 coords for 4 vertices
    public float[] getVertices() {
        float[] vertices = new float[12];

        float cosRot = Mth.cos(currentAngle);
        float sinRot = Mth.sin(currentAngle);

        for(int v = 0; v < 4; ++v) {
            // shift the vector to the 4 corners:
            // vec 0, 1 --> -rad;  vec 2, 3 --> +rad
            float xShift = ((v & 2) - 1) * currentRadius;
            // vec 1, 2 --> +rad;  vec 3, 0 --> -rad
            float yShift = (((v + 1) & 2) - 1) * currentRadius;

            // magic projection fuckery to turn the shift into an offset applying rotation and polar bs
            float aa = xShift * cosRot - yShift * sinRot;
            float ab = yShift * cosRot + xShift * sinRot;
            float ae = - aa * projCos;
            float yOffset = aa * projSin;
            float xOffset = ae * sinPolarAngle - ab * cosPolarAngle;
            float zOffset = ab * sinPolarAngle + ae * cosPolarAngle;

            vertices[v * 3    ] = xCoord + xOffset;
            vertices[v * 3 + 1] = yCoord + yOffset;
            vertices[v * 3 + 2] = zCoord + zOffset;
        }

        return vertices;
    }
}
