package codes.ztereohype.example;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;

import java.util.ArrayList;
import java.util.List;

//todo: redo all of the star rendering to be able to use the same vertex buffer object with a uniform or with the posestack
public class StarManager {
    private static final int STAR_ATTEMPTS = 1500;
    public static ArrayList<Star> starList = new ArrayList<>();

    public static BufferBuilder.RenderedBuffer generateStars(BufferBuilder bufferBuilder, long seed) {
        RandomSource randomSource = RandomSource.create(seed);
        bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION);

        for(int i = 0; i < 1500; ++i) {
            // -1..1
            double randX = randomSource.nextFloat() * 2.0F - 1.0F;
            double randY = randomSource.nextFloat() * 2.0F - 1.0F;
            double randZ = randomSource.nextFloat() * 2.0F - 1.0F;

            // 0.15..0.25 ???
            double starRadius = 0.15F + randomSource.nextFloat() * 0.5F;

            double squaredDistance = randX * randX + randY * randY + randZ * randZ;
            if (squaredDistance < 1.0 && squaredDistance > 0.01) {
                double invsqrtDistance = Mth.fastInvSqrt(squaredDistance);

                // star center coords
                double xCoord = randX * invsqrtDistance * 100.0;
                double yCoord = randY * invsqrtDistance * 100.0;
                double zCoord = randZ * invsqrtDistance * 100.0;

                // rad angle of polar coords
                double polarAngle = Math.atan2(randX, randZ);
                double sinPolarAngle = Math.sin(polarAngle);
                double cosPolarAngle = Math.cos(polarAngle);

                // magic projection fuckery??
                double p = Math.atan2(Math.sqrt(randX * randX + randZ * randZ), randY);
                double q = Math.sin(p);
                double r = Math.cos(p);

                // random rotation in rad
                double rot = randomSource.nextDouble() * Math.PI * 2.0;
                double sinRot = Math.sin(rot);
                double cosRot = Math.cos(rot);

                for(int v = 0; v < 4; ++v) {
                    // x:
                    // 0 0 0 0 & 0 0 1 0 --> 0 --> -.20
                    // 0 0 0 1 & 0 0 1 0 --> 0 --> -.20
                    // 0 0 1 0 & 0 0 1 0 --> 2 --> +.20
                    // 0 0 1 1 & 0 0 1 0 --> 2 --> +.20

                    // y:
                    // 0 0 0 1 & 0 0 1 0 --> 0 --> -.20
                    // 0 0 1 0 & 0 0 1 0 --> 2 --> +.20
                    // 0 0 1 1 & 0 0 1 0 --> 2 --> +.20
                    // 0 1 0 0 & 0 0 1 0 --> 0 --> -.20

                    // shift the vector to the 4 corners:
                    // vec 0, 1 --> -rad;  vec 2, 3 --> +rad
                    double xShift = (double)((v & 2) - 1) * starRadius;
                    // vec 1, 2 --> +rad;  vec 3, 0 --> -rad
                    double yShift = (double)(((v + 1) & 2) - 1) * starRadius;

                    // magic projection fuckery to turn the shift into an offset applying rotation and polar bs
                    double aa = xShift * cosRot - yShift * sinRot;
                    double ab = yShift * cosRot + xShift * sinRot;
                    double ae = 0.0 * q - aa * r;
                    double yOffset = aa * q + 0.0 * r;
                    double xOffset = ae * sinPolarAngle - ab * cosPolarAngle;
                    double zOffset = ab * sinPolarAngle + ae * cosPolarAngle;

                    bufferBuilder.vertex(xCoord + xOffset, yCoord + yOffset, zCoord + zOffset).endVertex();
                }
            }
        }

        return bufferBuilder.end();
    }

    public static void generateStarList() {
        RandomSource randomSource = RandomSource.create(123L);
        starList.clear();

        for(int i = 0; i < STAR_ATTEMPTS; ++i) {
            // -1..1
            float randX = randomSource.nextFloat() * 2.0F - 1.0F;
            float randY = randomSource.nextFloat() * 2.0F - 1.0F;
            float randZ = randomSource.nextFloat() * 2.0F - 1.0F;

            // 0.15..0.25 ???
            float starRadius = 0.15F + randomSource.nextFloat() * 0.1F;

            double squaredDistance = randX * randX + randY * randY + randZ * randZ;
            if (squaredDistance < 1.0 && squaredDistance > 0.01) {
                starList.add(new Star(randX, randY, randZ, starRadius, 0.3f + randomSource.nextFloat() * 0.4f));
            }
        }
    }

    public static void updateStars(int ticks, VertexBuffer starBuffer) {
        if (!ExampleMod.toggle) return;
        BufferBuilder bufferBuilder = Tesselator.getInstance().getBuilder();
        bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION);
//
        for (Star star : starList) {
            star.tick(ticks);

            float[] vertexList = star.getVertices();

            bufferBuilder.vertex(vertexList[0], vertexList[1], vertexList[2]).endVertex();
            bufferBuilder.vertex(vertexList[3], vertexList[4], vertexList[5]).endVertex();
            bufferBuilder.vertex(vertexList[6], vertexList[7], vertexList[8]).endVertex();
            bufferBuilder.vertex(vertexList[9], vertexList[10], vertexList[11]).endVertex();
        }

        starBuffer.bind();
        starBuffer.upload(bufferBuilder.end());
        VertexBuffer.unbind();
    }
}
