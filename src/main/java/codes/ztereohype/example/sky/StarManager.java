package codes.ztereohype.example.sky;

import codes.ztereohype.example.ExampleMod;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.util.RandomSource;

import java.util.ArrayList;

//todo: redo all of the star rendering to be able to use the same vertex buffer object with a uniform or with the posestack
public class StarManager {
    private static final int STAR_ATTEMPTS = 1500;
    public static ArrayList<Star> starList = new ArrayList<>();

    //todo: add a noise for patterns and star size
    public static void generateStarList() {
        RandomSource randomSource = RandomSource.create(123L);
        starList.clear();

        for (int i = 0; i < STAR_ATTEMPTS; ++i) {
            // -1..1
            float randX = randomSource.nextFloat() * 2.0F - 1.0F;
            float randY = randomSource.nextFloat() * 2.0F - 1.0F;
            float randZ = randomSource.nextFloat() * 2.0F - 1.0F;

            // 0.15..0.25 ???
            float starRadius = 0.15F + randomSource.nextFloat() * 0.1F;

            double squaredDistance = randX * randX + randY * randY + randZ * randZ;
            if (squaredDistance < 1.0 && squaredDistance > 0.01) {
                starList.add(new Star(randX, randY, randZ, starRadius, 0.03f + randomSource.nextFloat() * 0.04f));
            }
        }
    }

    public static void updateStars(int ticks, VertexBuffer starBuffer) {
        if (!ExampleMod.toggle) return;
        BufferBuilder bufferBuilder = Tesselator.getInstance().getBuilder();
        bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION);

        for (Star star : starList) {
            star.tick(ticks);
            star.setVertices(bufferBuilder);
        }

        starBuffer.bind();
        starBuffer.upload(bufferBuilder.end());
        VertexBuffer.unbind();
    }
}
