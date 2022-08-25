package codes.ztereohype.example;

import codes.ztereohype.example.sky.Star;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.synth.ImprovedNoise;
import org.openjdk.jmh.annotations.*;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
public class StarUpdateBenchmark {
    public ArrayList<Star> starList = new ArrayList<>();
    public BufferBuilder starBufferBuilder = Tesselator.getInstance().getBuilder();

    @Setup(Level.Trial)
    public void setUp() {
        starBufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        RandomSource randomSource = RandomSource.create(123L);
        ImprovedNoise noise = new ImprovedNoise(randomSource);
        starList.clear();

        for (int i = 0; i < 1700; ++i) {
            // -1..1
            float randX = randomSource.nextFloat() * 2.0F - 1.0F;
            float randY = randomSource.nextFloat() * 2.0F - 1.0F;
            float randZ = randomSource.nextFloat() * 2.0F - 1.0F;

            float twinkleSpeed = 0.03f + randomSource.nextFloat() * 0.04f;

            double[] starDerivatives = new double[] {0,0,0};
            float starValue = (float) noise.noiseWithDerivative(randX*3, randY*3, randZ*3, starDerivatives) * 0.5f + 0.5f;

            float maxDerivative = (float) Math.max(Math.abs(starDerivatives[0]), Math.max(Math.abs(starDerivatives[1]), Math.abs(starDerivatives[2]))); //kinda normal distr around 1?
            float temperature = maxDerivative * 8000;

            // 0.15..0.25 ???
            float starRadius = 0.15F + randomSource.nextFloat() * 0.15F;
            starRadius *= starValue;

            float squaredDistance = randX * randX + randY * randY + randZ * randZ;

            if (squaredDistance < 1.0 && squaredDistance > 0.01 && starRadius > 0.13) {
                starList.add(new Star(randX, randY, randZ, starRadius, temperature, twinkleSpeed));
            } else --i;
        }
    }

    @Warmup(iterations = 1)
    @Measurement(iterations = 15, time = 2000, timeUnit = TimeUnit.MILLISECONDS)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    @Benchmark
    public void updateStars1() {
        starBufferBuilder.clear();
        for (Star star : starList) {
            star.tick(134);
            star.setVertices(starBufferBuilder);
        }
    }

    @Warmup(iterations = 1)
    @Measurement(iterations = 15, time = 2000, timeUnit = TimeUnit.MILLISECONDS)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    @Benchmark
    public void updateStars2() {
        starBufferBuilder.clear();
        starList.stream().parallel().forEach(star -> star.tick(134));
        for (Star star : starList) {
            star.setVertices(starBufferBuilder);
        }
    }

    @Warmup(iterations = 1)
    @Measurement(iterations = 15, time = 2000, timeUnit = TimeUnit.MILLISECONDS)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    @Benchmark
    public void updateStars3() {
        starBufferBuilder.clear();
        for (Star star : starList) {
            star.tick(134);
//            star.setVerticesTwo(starBufferBuilder);
        }
    }

    @Warmup(iterations = 1)
    @Measurement(iterations = 15, time = 2000, timeUnit = TimeUnit.MILLISECONDS)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    @Benchmark
    public void updateStars4() {
        starBufferBuilder.clear();
        starList.stream().parallel().forEach(star -> star.tick(134));
        for (Star star : starList) {
//            star.setVerticesTwo(starBufferBuilder);
        }
    }
}
