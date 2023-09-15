package codes.ztereohype.nicerskies.sky.star;

import codes.ztereohype.nicerskies.core.Gradient;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexBuffer;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.synth.ImprovedNoise;

import java.util.ArrayList;
import java.util.List;


public class Starbox {
    private static final BufferBuilder STAR_BUFFER_BUILDER = Tesselator.getInstance().getBuilder();

    private final List<Star> starList = new ArrayList<>();
    private final VertexBuffer starBuffer;

    public Starbox(RandomSource randomSource, Gradient starGradient, VertexBuffer starBuffer) {
        this.starBuffer = starBuffer;
        this.generateStars(randomSource, starGradient);
    }

    private void generateStars(RandomSource randomSource, Gradient starGradient) {
        starList.clear();

        ImprovedNoise noise = new ImprovedNoise(randomSource);

        int stars = 1500;
        for (int i = 0; i < stars; ++i) {
            float randX = randomSource.nextFloat() * 2.0F - 1.0F;
            float randY = randomSource.nextFloat() * 2.0F - 1.0F;
            float randZ = randomSource.nextFloat() * 2.0F - 1.0F;

            float resizeSpeed = 0.03f + randomSource.nextFloat() * 0.04f;
            float spinSpeed = randomSource.nextFloat() * 0.02f - 0.01f;

            int[] starColor = starGradient.getAt(randomSource.nextFloat());

            float starRadius = 0.15F + randomSource.nextFloat() * 0.15F;
            double starValue = noise.noise(randX * 2.5f, randY * 2.5f, randZ * 2.5f) + 0.5;

            float squaredDistance = randX * randX + randY * randY + randZ * randZ;
            if (squaredDistance < 1.0 && squaredDistance > 0.01 && starValue > 0.2) {
                starList.add(new Star(randX, randY, randZ, starRadius, starColor, resizeSpeed, spinSpeed));
            } else --i;
        }
    }

    public void updateStars(int ticks) {
        // todo: make a fast path for when sodium is installed (vertexbufferwriter), or just make our own.
        STAR_BUFFER_BUILDER.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);

        for (Star star : starList) {
            star.tick(ticks);
            star.setVertices(STAR_BUFFER_BUILDER);
        }

        starBuffer.bind();
        starBuffer.upload(STAR_BUFFER_BUILDER.end());
    }
}

