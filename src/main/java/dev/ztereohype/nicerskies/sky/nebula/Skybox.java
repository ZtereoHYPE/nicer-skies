package dev.ztereohype.nicerskies.sky.nebula;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.ColorTargetState;
import com.mojang.blaze3d.pipeline.DepthStencilState;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.CompareOp;
import com.mojang.blaze3d.shaders.UniformType;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.textures.AddressMode;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuSampler;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.math.Axis;
import dev.ztereohype.nicerskies.NicerSkies;
import dev.ztereohype.nicerskies.config.Config;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;


public class Skybox {
    public static final int RESOLUTION = 768;

    private static final RenderPipeline SKYBOX_PIPELINE = RenderPipelines.register(
            RenderPipeline.builder()
                          .withUniform("DynamicTransforms",UniformType.UNIFORM_BUFFER)
                          .withUniform("Projection", UniformType.UNIFORM_BUFFER)
                          .withLocation(Identifier.fromNamespaceAndPath(NicerSkies.MOD_ID, "pipeline/twinkling_stars"))
                          .withVertexShader("core/position_tex")
                          .withFragmentShader("core/position_tex")
                          .withSampler("Sampler0")
                          .withColorTargetState(new ColorTargetState(BlendFunction.OVERLAY))
                          .withVertexFormat(DefaultVertexFormat.POSITION_TEX, VertexFormat.Mode.QUADS)
                          .build()
    );

    private final DynamicTexture skyTexture = new DynamicTexture("NicerSkies_skybox", RESOLUTION * 4, RESOLUTION * 4, false);
    private final ExecutorService skyExecutor = Executors.newCachedThreadPool();
    private final GpuSampler skyboxSampler;
    private final @Getter GpuBuffer skyboxBuffer;
    RenderSystem.AutoStorageIndexBuffer indexBuffer = RenderSystem.getSequentialBuffer(VertexFormat.Mode.QUADS);

    public Skybox() {
        try (MeshData skyboxData = generateVertices().build()) {
            this.skyboxBuffer = RenderSystem
                    .getDevice()
                    .createBuffer(() -> "Nicer Skies Skybox", GpuBuffer.USAGE_VERTEX, skyboxData.vertexBuffer());
        }

        skyboxSampler = RenderSystem.getDevice().createSampler(
                AddressMode.CLAMP_TO_EDGE, AddressMode.CLAMP_TO_EDGE,
                FilterMode.NEAREST, FilterMode.NEAREST,
                1, OptionalDouble.empty()
        );
    }

    @SuppressWarnings("ConstantConditions")
    public void render(PoseStack poseStack, float brightness) {
        // uniform values
        Matrix4fStack viewModelMatrix = RenderSystem.getModelViewStack();
        viewModelMatrix.pushMatrix();
        viewModelMatrix.mul(poseStack.last().pose());

        float alpha = NicerSkies.getInstance().getConfig().getNebulaStrength();
        if (!NicerSkies.getInstance().getConfig().getRenderDuringDay()) {
            alpha *= brightness;
        }
        GpuBufferSlice dynamicTransforms = RenderSystem
                .getDynamicUniforms()
                .writeTransform(viewModelMatrix, new Vector4f(alpha, alpha, alpha, alpha), new Vector3f(), new Matrix4f());

        // resources
        GpuTextureView color = Minecraft.getInstance().getMainRenderTarget().getColorTextureView();
        GpuTextureView depth = Minecraft.getInstance().getMainRenderTarget().getDepthTextureView();

        // render
        try (RenderPass renderPass = RenderSystem.getDevice()
                                                 .createCommandEncoder()
                                                 .createRenderPass(() -> "Nicer Skies Skybox", color, OptionalInt.empty(), depth, OptionalDouble.empty())) {
            renderPass.setPipeline(Skybox.SKYBOX_PIPELINE);
            RenderSystem.bindDefaultUniforms(renderPass);
            renderPass.setUniform("DynamicTransforms", dynamicTransforms);
            renderPass.bindTexture("Sampler0", this.skyTexture.getTextureView(), this.skyboxSampler);
            renderPass.setVertexBuffer(0, this.skyboxBuffer);
            renderPass.setIndexBuffer(this.indexBuffer.getBuffer(24), this.indexBuffer.type());
            renderPass.drawIndexed(0, 0, 36, 1);
        }

        viewModelMatrix.popMatrix();
    }

    public void paint(SkyboxPainter painter) {
        NativeImage skyNativeTex = this.skyTexture.getPixels();
        CountDownLatch latch = new CountDownLatch(PaintTask.TextureLocation.values().length);

        for (PaintTask.TextureLocation location : PaintTask.TextureLocation.values()) {
            skyExecutor.execute(new PaintTask(skyNativeTex, painter, location, latch));
        }

        try {
            latch.await(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException("Failed to paint skybox", e);
        }

        this.skyTexture.upload();
    }

    @AllArgsConstructor
    private static class PaintTask implements Runnable {
        private NativeImage skyNativeTex;
        private SkyboxPainter painter;
        private TextureLocation location;
        private CountDownLatch latch;

        public void run() {
            for (int texY = 0; texY < RESOLUTION; texY++) {
                for (int texX = 0; texX < RESOLUTION; texX++) {
                    float x = location.getXFunc().apply(texX, texY);
                    float y = location.getYFunc().apply(texX, texY);
                    float z = location.getZFunc().apply(texX, texY);

                    skyNativeTex.setPixelABGR(location.getXLocation(texX), location.getYLocation(texY), painter.getTexelColour(x, y, z));
                }
            }

            latch.countDown();
        }

        @Getter
        @AllArgsConstructor
        private enum CoordMap {
            X((texX, texY) -> (texX / (float) RESOLUTION) * 2 - 1),
            Y((texX, texY) -> (texY / (float) RESOLUTION) * 2 - 1),
            ONE((texX, texY) -> 1F),
            NEG_ONE((texX, texY) -> -1F);

            private final BiFunction<Integer, Integer, Float> map;
        }

        @AllArgsConstructor
        public enum TextureLocation {
            TOP(CoordMap.X.getMap(),        CoordMap.ONE.getMap(),      CoordMap.Y.getMap(),        2,  0),
            BOTTOM(CoordMap.X.getMap(),     CoordMap.NEG_ONE.getMap(),  CoordMap.Y.getMap(),        2,  2),
            POS_Z(CoordMap.X.getMap(),      CoordMap.Y.getMap(),        CoordMap.ONE.getMap(),      1,  1),
            NEG_Z(CoordMap.X.getMap(),      CoordMap.Y.getMap(),        CoordMap.NEG_ONE.getMap(),  3,  1),
            POS_X(CoordMap.ONE.getMap(),    CoordMap.Y.getMap(),        CoordMap.X.getMap(),        2,  1),
            NEG_X(CoordMap.NEG_ONE.getMap(), CoordMap.Y.getMap(),       CoordMap.X.getMap(),        0,  1);

            private final @Getter BiFunction<Integer, Integer, Float> xFunc;
            private final @Getter BiFunction<Integer, Integer, Float> yFunc;
            private final @Getter BiFunction<Integer, Integer, Float> zFunc;
            private final int shiftX;
            private final int shiftY;

            public int getXLocation(int texX) {
                return texX + shiftX * RESOLUTION;
            }

            public int getYLocation(int texY) {
                return texY + shiftY * RESOLUTION;
            }
        }
    }

    private static BufferBuilder generateVertices() {
        BufferBuilder skyboxBuilder = Tesselator
                .getInstance()
                .begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);

        // +z face
        skyboxBuilder.addVertex(-60f, -60f, 60f).setUv(0.25f, 0.25f);
        skyboxBuilder.addVertex(-60f, 60f, 60f).setUv(0.25f, 0.5f);
        skyboxBuilder.addVertex(60f, 60f, 60f).setUv(0.5f, 0.5f);
        skyboxBuilder.addVertex(60f, -60f, 60f).setUv(0.5f, 0.25f);

        // -z face
        skyboxBuilder.addVertex(-60f, -60f, -60f).setUv(0.75f, 0.25f);
        skyboxBuilder.addVertex(60f, -60f, -60f).setUv(1f, 0.25f);
        skyboxBuilder.addVertex(60f, 60f, -60f).setUv(1f, 0.5f);
        skyboxBuilder.addVertex(-60f, 60f, -60f).setUv(0.75f, 0.5f);

        // bottom face
        skyboxBuilder.addVertex(-60f, -60f, -60f).setUv(0.5f, 0.5f);
        skyboxBuilder.addVertex(-60f, -60f, 60f).setUv(0.5f, 0.75f);
        skyboxBuilder.addVertex(60f, -60f, 60f).setUv(0.75f, 0.75f);
        skyboxBuilder.addVertex(60f, -60f, -60f).setUv(0.75f, 0.5f);

        // top face
        skyboxBuilder.addVertex(-60f, 60f, -60f).setUv(0.5f, 0f);
        skyboxBuilder.addVertex(60f, 60f, -60f).setUv(0.75f, 0f);
        skyboxBuilder.addVertex(60f, 60f, 60f).setUv(0.75f, 0.25f);
        skyboxBuilder.addVertex(-60f, 60f, 60f).setUv(0.5f, 0.25f);

        // +x face
        skyboxBuilder.addVertex(60f, -60f, -60f).setUv(0.5f, 0.25f);
        skyboxBuilder.addVertex(60f, -60f, 60f).setUv(0.75f, 0.25f);
        skyboxBuilder.addVertex(60f, 60f, 60f).setUv(0.75f, 0.5f);
        skyboxBuilder.addVertex(60f, 60f, -60f).setUv(0.5f, 0.5f);

        // -x face
        skyboxBuilder.addVertex(-60f, -60f, -60f).setUv(0f, 0.25f);
        skyboxBuilder.addVertex(-60f, 60f, -60f).setUv(0f, 0.5f);
        skyboxBuilder.addVertex(-60f, 60f, 60f).setUv(0.25f, 0.5f);
        skyboxBuilder.addVertex(-60f, -60f, 60f).setUv(0.25f, 0.25f);

        return skyboxBuilder;
    }
}
