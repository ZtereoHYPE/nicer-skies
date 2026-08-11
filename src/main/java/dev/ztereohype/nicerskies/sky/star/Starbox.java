package dev.ztereohype.nicerskies.sky.star;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.shaders.UniformType;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.ztereohype.nicerskies.NicerSkies;
import dev.ztereohype.nicerskies.core.Gradient;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.util.RandomSource;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.levelgen.synth.ImprovedNoise;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.List;
import java.util.OptionalDouble;
import java.util.OptionalInt;


public class Starbox {
    public static final RenderPipeline STAR_PIPELINE = RenderPipelines.register(
            RenderPipeline.builder()
                          .withUniform("DynamicTransforms", UniformType.UNIFORM_BUFFER)
                          .withUniform("Projection", UniformType.UNIFORM_BUFFER)
                          .withLocation(Identifier.fromNamespaceAndPath(NicerSkies.MOD_ID, "pipeline/twinkling_stars"))
                          .withVertexShader("core/position_color")
                          .withFragmentShader("core/position_color")
                          .withBlend(BlendFunction.OVERLAY)
                          .withDepthWrite(false)
                          .withVertexFormat(DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.QUADS)
                          .build()
    );

    private static final int STAR_COUNT = 1500;
    private static final int STARBOX_SIZE = DefaultVertexFormat.POSITION_COLOR.getVertexSize() * STAR_COUNT * 4;

    private final Star[] starList = new Star[STAR_COUNT];
    private final @Getter GpuBuffer starBuffer;
    RenderSystem.AutoStorageIndexBuffer indexBuffer = RenderSystem.getSequentialBuffer(VertexFormat.Mode.QUADS);

    private @Getter int starIndexCount = 0;

    public Starbox() {
        this.starBuffer = RenderSystem.getDevice().createBuffer(
            () -> "Star Vertex Buffer",
            GpuBuffer.USAGE_VERTEX | GpuBuffer.USAGE_COPY_DST,
            STARBOX_SIZE
        );
    }

    public void generateStars(RandomSource randomSource, Gradient starGradient) {
        ImprovedNoise noise = new ImprovedNoise(randomSource);

        for (int i = 0; i < STAR_COUNT; ++i) {
            float randX = randomSource.nextFloat() * 2.0F - 1.0F;
            float randY = randomSource.nextFloat() * 2.0F - 1.0F;
            float randZ = randomSource.nextFloat() * 2.0F - 1.0F;

            float resizeSpeed = 0.03f + randomSource.nextFloat() * 0.04f;
            float spinSpeed = randomSource.nextFloat() * 0.02f - 0.01f;

            int[] starRgb = starGradient.getAt(randomSource.nextFloat());
            int starColor = ARGB.color(starRgb[0], starRgb[1], starRgb[2]);

            float starRadius = 0.15F + randomSource.nextFloat() * 0.15F;
            double starValue = noise.noise(randX * 2.5f, randY * 2.5f, randZ * 2.5f) + 0.5;

            float squaredDistance = randX * randX + randY * randY + randZ * randZ;
            if (squaredDistance < 1.0 && squaredDistance > 0.01 && starValue > 0.2) {
                starList[i] = new Star(randX, randY, randZ, starRadius, starColor, resizeSpeed, spinSpeed);
            } else --i;
        }
    }

    public void updateStars(int ticks) {
        try (ByteBufferBuilder byteBufferBuilder = ByteBufferBuilder.exactlySized(STARBOX_SIZE)) {
            BufferBuilder bufferBuilder = new BufferBuilder(byteBufferBuilder, VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);

            ProfilerFiller profilerFiller = Profiler.get();
            profilerFiller.push("tick stars");
            for (int i = 0; i < STAR_COUNT; i++) {
                starList[i].tick(ticks);
            }
            profilerFiller.pop();

            profilerFiller.push("set vertices");
            for (int i = 0; i < STAR_COUNT; i++) {
                starList[i].setVertices(bufferBuilder);
            }
            profilerFiller.pop();

            profilerFiller.push("update mesh");
            try (MeshData meshData = bufferBuilder.buildOrThrow()) {
                this.starIndexCount = meshData.drawState().indexCount();
                // Note: it would be nice to not have to use a new command buffer.
                RenderSystem.getDevice().createCommandEncoder().writeToBuffer(starBuffer.slice(), meshData.vertexBuffer());
            }
            profilerFiller.pop();
        }
    }

    public void render(PoseStack poseStack, float brightness) {
        // uniform values
        Matrix4fStack viewModelMatrix = RenderSystem.getModelViewStack();
        viewModelMatrix.pushMatrix();
        viewModelMatrix.mul(poseStack.last().pose());
        GpuBufferSlice gpuBufferSlice = RenderSystem
                .getDynamicUniforms()
                .writeTransform(viewModelMatrix, new Vector4f(brightness, brightness, brightness, brightness), new Vector3f(), new Matrix4f());

        // resources
        GpuTextureView color = Minecraft.getInstance().getMainRenderTarget().getColorTextureView();
        GpuTextureView depth = Minecraft.getInstance().getMainRenderTarget().getDepthTextureView();

        // render pass
        try (RenderPass renderPass = RenderSystem.getDevice()
                                                 .createCommandEncoder()
                                                 .createRenderPass(() -> "Nicer Skies Stars", color, OptionalInt.empty(), depth, OptionalDouble.empty())) {
            renderPass.setPipeline(STAR_PIPELINE);
            RenderSystem.bindDefaultUniforms(renderPass);
            renderPass.setUniform("DynamicTransforms", gpuBufferSlice);
            renderPass.setVertexBuffer(0, this.starBuffer);
            renderPass.setIndexBuffer(indexBuffer.getBuffer(this.starIndexCount), indexBuffer.type());
            renderPass.drawIndexed(0, 0, this.starIndexCount, 1);
        }

        viewModelMatrix.popMatrix();
    }
}

