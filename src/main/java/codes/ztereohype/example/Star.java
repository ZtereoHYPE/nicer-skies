package codes.ztereohype.example;

import net.minecraft.util.Mth;

import java.util.ArrayList;
import java.util.List;

public class Star {
    private final double xCoord;
    private final double yCoord;
    private final double zCoord;

    private final double sinPolarAngle;
    private final double cosPolarAngle;

    private final double projSin;
    private final double projCos;

    private final double twinkleSpeed;
    private final double initialRadius;

    private double currentAngle;
    private double currentRadius;

    public Star(double randX, double randY, double randZ, double initialRadius, double twinkleSpeed) {
        double invsqrtDistance = Mth.fastInvSqrt(randX * randX + randY * randY + randZ * randZ);
        this.xCoord = randX * invsqrtDistance * 100.0;
        this.yCoord = randY * invsqrtDistance * 100.0;
        this.zCoord = randZ * invsqrtDistance * 100.0;

        double polarAngle = Math.atan2(randX, randZ);
        this.sinPolarAngle = Math.sin(polarAngle);
        this.cosPolarAngle = Math.cos(polarAngle);

        // magic projection fuckery??
        double proj = Math.atan2(Math.sqrt(randX * randX + randZ * randZ), randY);
        this.projSin = Math.sin(proj);
        this.projCos = Math.cos(proj);

        this.twinkleSpeed = twinkleSpeed;
        this.initialRadius = initialRadius;
        this.currentRadius = initialRadius;
        this.currentAngle = twinkleSpeed; //just so they dont all start straight
    }

    public void tick(int ticks) {
        currentAngle += 0.007d * twinkleSpeed;
        currentRadius = Mth.lerp(Math.sin(ticks * twinkleSpeed / 10f) * 0.5f + 0.5f, initialRadius - 0.15f, initialRadius + 0.15f) ;
    }

    //return 4*3 coords for 4 vertices
    public double[] getVertices() {
        double[] vertices = new double[12];

        double cosRot = Math.cos(currentAngle);
        double sinRot = Math.sin(currentAngle);

        for(int v = 0; v < 4; ++v) {
            // shift the vector to the 4 corners:
            // vec 0, 1 --> -rad;  vec 2, 3 --> +rad
            double xShift = (double)((v & 2) - 1) * currentRadius;
            // vec 1, 2 --> +rad;  vec 3, 0 --> -rad
            double yShift = (double)(((v + 1) & 2) - 1) * currentRadius;

            // magic projection fuckery to turn the shift into an offset applying rotation and polar bs
            double aa = xShift * cosRot - yShift * sinRot;
            double ab = yShift * cosRot + xShift * sinRot;
            double ae = - aa * projCos;
            double yOffset = aa * projSin;
            double xOffset = ae * sinPolarAngle - ab * cosPolarAngle;
            double zOffset = ab * sinPolarAngle + ae * cosPolarAngle;

            vertices[v * 3    ] = xCoord + xOffset;
            vertices[v * 3 + 1] = yCoord + yOffset;
            vertices[v * 3 + 2] = zCoord + zOffset;
        }

        return vertices;
    }
}
