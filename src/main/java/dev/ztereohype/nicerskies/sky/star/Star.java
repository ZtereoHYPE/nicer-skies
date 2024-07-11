package dev.ztereohype.nicerskies.sky.star;

import com.mojang.blaze3d.vertex.BufferBuilder;
import lombok.Getter;
import net.minecraft.util.Mth;

public class Star {
    private final float xCoord;
    private final float yCoord;
    private final float zCoord;

    private final float longitudeSin;
    private final float longitudeCos;

    private final float latitudeSin;
    private final float latitudeCos;

    private final float resizeSpeed;
    private final float spinSpeed;

    private final float minRadius;
    private final float maxRadius;

    private final int r;
    private final int g;
    private final int b;

    private float currentAngle;
    private @Getter float currentRadius;

    public Star(float randX, float randY, float randZ, float size, int[] color, float resizeSpeed, float spinSpeed) {
        this.r = color[0];
        this.g = color[1];
        this.b = color[2];

        float invsqrtDistance = (float) Mth.fastInvSqrt(randX * randX + randY * randY + randZ * randZ);
        this.xCoord = randX * invsqrtDistance * 100.0F;
        this.yCoord = randY * invsqrtDistance * 100.0F;
        this.zCoord = randZ * invsqrtDistance * 100.0F;

        double polarAngle = Math.atan2(randX, randZ);
        this.longitudeSin = (float) Math.sin(polarAngle);
        this.longitudeCos = (float) Math.cos(polarAngle);

        double proj = Math.atan2(Math.sqrt(randX * randX + randZ * randZ), randY);
        this.latitudeSin = (float) Math.sin(proj);
        this.latitudeCos = (float) Math.cos(proj);

        this.spinSpeed = spinSpeed;
        this.resizeSpeed = resizeSpeed;

        this.minRadius = size - 0.15f;
        this.maxRadius = size + 0.15f;
        this.currentRadius = size;
        this.currentAngle = (spinSpeed + 0.01f) * 628.3f; // random angle from 0 to 2π
    }

    public void tick(int ticks) {
        currentAngle += spinSpeed;
        currentRadius = Mth.lerp(Mth.sin(ticks * resizeSpeed), minRadius, maxRadius);
    }

    // Code left for readability
    /* public void setVertices(BufferBuilder bufferBuilder) {
        float cosRot = Mth.cos(currentAngle);
        float sinRot = Mth.sin(currentAngle);

        for (int v = 0; v < 4; ++v) {
            // shift the vector to the 4 corners:
            // vec 0, 1 --> -rad;  vec 2, 3 --> +rad
            float xShift = ((v & 2) - 1) * currentRadius;
            // vec 1, 2 --> +rad;  vec 3, 0 --> -rad
            float yShift = (((v + 1) & 2) - 1) * currentRadius;

            float unprojectedVerticalOffset = xShift * cosRot - yShift * sinRot;
            float unprojectedHorizontalOffset = yShift * cosRot + xShift * sinRot;
            float latitudeCorrectedUnprojectedVerticalOffset = -unprojectedVerticalOffset * latitudeCos; // max negative +2pi, max positive -2pi

            float yOffset = unprojectedVerticalOffset * latitudeSin; // at ±pi should be max, squished at poles
            float xOffset = latitudeCorrectedUnprojectedVerticalOffset * longitudeSin - unprojectedHorizontalOffset * longitudeCos;
            float zOffset = unprojectedHorizontalOffset * longitudeSin + latitudeCorrectedUnprojectedVerticalOffset * longitudeCos;

            bufferBuilder.vertex(xCoord + xOffset, yCoord + yOffset, zCoord + zOffset)
                         .color(r, g, b, 255)
                         .endVertex();
        }
    } */

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

        bufferBuilder.addVertex(
                             xCoord + ae1 * longitudeSin - secondVertexDistance * longitudeCos,
                             yCoord + firstVertexDistance * latitudeSin,
                             zCoord + secondVertexDistance * longitudeSin + ae1 * longitudeCos)
                     .setColor(r, g, b, 255)
                     .addVertex(
                             xCoord + ae2 * longitudeSin - thirdVertexDistance * longitudeCos,
                             yCoord + secondVertexDistance * latitudeSin,
                             zCoord + thirdVertexDistance * longitudeSin + ae2 * longitudeCos)
                     .setColor(r, g, b, 255)
                     .addVertex(
                             xCoord + ae3 * longitudeSin - fourthVertexDistance * longitudeCos,
                             yCoord + thirdVertexDistance * latitudeSin,
                             zCoord + fourthVertexDistance * longitudeSin + ae3 * longitudeCos)
                     .setColor(r, g, b, 255)
                     .addVertex(
                             xCoord + ae4 * longitudeSin - firstVertexDistance * longitudeCos,
                             yCoord + fourthVertexDistance * latitudeSin,
                             zCoord + firstVertexDistance * longitudeSin + ae4 * longitudeCos)
                     .setColor(r, g, b, 255);
    }
}
