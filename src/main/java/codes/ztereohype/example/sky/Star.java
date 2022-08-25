package codes.ztereohype.example.sky;

import com.mojang.blaze3d.vertex.BufferBuilder;
import net.minecraft.util.Mth;

public class Star {
    private final float xCoord;
    private final float yCoord;
    private final float zCoord;

    private final float longitudeSin;
    private final float longitudeCos;

    private final float latitudeSin;
    private final float latitudeCos;

    private final float twinkleSpeed;
    private final float minRadius;
    private final float maxRadius;

    private final int r;
    private final int g;
    private final int b;

    private float currentAngle;
    private float currentRadius;

    public Star(float randX, float randY, float randZ, float size, float temperature, float twinkleSpeed) {
        this.r = getRedFromKelvin(temperature);
        this.g = getGreenFromKelvin(temperature);
        this.b = getBlueFromKelvin(temperature);

        float invsqrtDistance = Mth.fastInvSqrt(randX * randX + randY * randY + randZ * randZ);
        this.xCoord = randX * invsqrtDistance * 100.0F;
        this.yCoord = randY * invsqrtDistance * 100.0F;
        this.zCoord = randZ * invsqrtDistance * 100.0F;

        double polarAngle = Math.atan2(randX, randZ);
        this.longitudeSin = (float) Math.sin(polarAngle);
        this.longitudeCos = (float) Math.cos(polarAngle);

        double proj = Math.atan2(Math.sqrt(randX * randX + randZ * randZ), randY);
        this.latitudeSin = (float) Math.sin(proj);
        this.latitudeCos = (float) Math.cos(proj);

        this.twinkleSpeed = twinkleSpeed;
        this.minRadius = size - 0.13f;
        this.maxRadius = size + 0.13f;
        this.currentRadius = size;
        this.currentAngle = (float) ((twinkleSpeed - 0.03) * 157); // random angle from 0 to 2π
    }

    public void tick(int ticks) {
        currentAngle += 0.07f * twinkleSpeed;
        currentRadius = Mth.lerp(Mth.sin(ticks * twinkleSpeed), minRadius, maxRadius);
    }

    //return 4*3 coords for 4 vertices
//    public void setVertices(BufferBuilder bufferBuilder) {
//        float cosRot = Mth.cos(currentAngle);
//        float sinRot = Mth.sin(currentAngle);
//
//        for (int v = 0; v < 4; ++v) {
//            // shift the vector to the 4 corners:
//            // vec 0, 1 --> -rad;  vec 2, 3 --> +rad
//            float xShift = ((v & 2) - 1) * currentRadius;
//            // vec 1, 2 --> +rad;  vec 3, 0 --> -rad
//            float yShift = (((v + 1) & 2) - 1) * currentRadius;
//
//            float unprojectedVerticalOffset = xShift * cosRot - yShift * sinRot;
//            float unprojectedHorizontalOffset = yShift * cosRot + xShift * sinRot;
//            float latitudeCorrectedUnprojectedVerticalOffset = -unprojectedVerticalOffset * latitudeCos; // max negative +2pi, max positive -2pi
//
//            float yOffset = unprojectedVerticalOffset * latitudeSin; // at ±pi should be max, squished at poles
//            float xOffset = latitudeCorrectedUnprojectedVerticalOffset * longitudeSin - unprojectedHorizontalOffset * longitudeCos;
//            float zOffset = unprojectedHorizontalOffset * longitudeSin + latitudeCorrectedUnprojectedVerticalOffset * longitudeCos;
//
//            bufferBuilder.vertex(xCoord + xOffset, yCoord + yOffset, zCoord + zOffset)
//                         .color(r, g, b, 255)
//                         .endVertex();
//        }
//    }

    public void setVertices(BufferBuilder bufferBuilder) {
        float horizontalVertexDistance = currentRadius * Mth.cos(currentAngle);
        float verticalVertexDistance = currentRadius * Mth.sin(currentAngle);

        float firstVertexDistance = -horizontalVertexDistance + verticalVertexDistance;
        float secondVertexDistance = -horizontalVertexDistance - verticalVertexDistance;
        float thirdVertexDistance = horizontalVertexDistance - verticalVertexDistance;
        float fourthVertexDistance = horizontalVertexDistance + verticalVertexDistance;

        float ae1 = thirdVertexDistance * latitudeCos;
        float ae2 = fourthVertexDistance * latitudeCos;
        float ae3 = firstVertexDistance * latitudeCos;
        float ae4 = secondVertexDistance * latitudeCos;

        bufferBuilder.vertex(xCoord + ae1 * longitudeSin - secondVertexDistance * longitudeCos, yCoord + firstVertexDistance * latitudeSin, zCoord + secondVertexDistance * longitudeSin + ae1 * longitudeCos)
                     .color(r, g, b, 255)
                     .endVertex();
        bufferBuilder.vertex(xCoord + ae2 * longitudeSin - thirdVertexDistance * longitudeCos, yCoord + secondVertexDistance * latitudeSin, zCoord + thirdVertexDistance * longitudeSin + ae2 * longitudeCos)
                     .color(r, g, b, 255)
                     .endVertex();
        bufferBuilder.vertex(xCoord + ae3 * longitudeSin - fourthVertexDistance * longitudeCos, yCoord + thirdVertexDistance * latitudeSin, zCoord + fourthVertexDistance * longitudeSin + ae3 * longitudeCos)
                     .color(r, g, b, 255)
                     .endVertex();
        bufferBuilder.vertex(xCoord + ae4 * longitudeSin - firstVertexDistance * longitudeCos, yCoord + fourthVertexDistance * latitudeSin, zCoord + firstVertexDistance * longitudeSin + ae4 * longitudeCos)
                     .color(r, g, b, 255)
                     .endVertex();
    }

    // source: https://tannerhelland.com/2012/09/18/convert-temperature-rgb-algorithm-code.html
    public static int getRedFromKelvin(double kelvins) {
        kelvins /= 100;

        if (kelvins <= 66) {
            return 255;

        } else {
            double red = 329.698727446D * Math.pow(kelvins - 60, -0.1332047592D);
            return Mth.clamp((int)red, 0, 255);
        }
    }

    public static int getGreenFromKelvin(double kelvins) {
        kelvins /= 100;

        double green;
        if (kelvins <= 66) {
            green = 99.4708025861D * Math.log(kelvins) - 161.1195681661D;

        } else {
            green = 288.1221695283D * Math.pow(kelvins - 60, -0.0755148492D);
        }

        return Mth.clamp((int)green, 0, 255);
    }

    public static int getBlueFromKelvin(double kelvins) {
        kelvins /= 100;

        if (kelvins >= 66) {
            return 255;

        } else if (kelvins <= 19){
            return 0;

        } else {
            double blue = 138.5177312231D * Math.log(kelvins - 60) - 305.0447927307D;
//            double blue = 138.5177312231D * Math.log(kelvins - 60) - 205.0447927307D;
            return Mth.clamp((int)blue, 0, 255);
        }
    }
}
