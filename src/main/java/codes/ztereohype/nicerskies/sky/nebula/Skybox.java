package codes.ztereohype.nicerskies.sky.nebula;

import codes.ztereohype.nicerskies.NicerSkies;
import codes.ztereohype.nicerskies.config.Config;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexBuffer;
import com.mojang.blaze3d.vertex.VertexFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.Mth;
import com.mojang.math.Matrix4f;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;


public class Skybox {
    public static final int RESOLUTION = 768;

    private final DynamicTexture skyTexture = new DynamicTexture(RESOLUTION * 4, RESOLUTION * 4, false);

    private final ExecutorService skyExecutor = Executors.newCachedThreadPool();

    private final @Getter VertexBuffer skyboxBuffer = new VertexBuffer();

    public Skybox(SkyboxPainter painter) {
        generateVertices();
        paint(painter);
    }

    @SuppressWarnings("ConstantConditions")
    public void render(PoseStack poseStack, Matrix4f projectionMatrix) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, skyTexture.getId());

        float alpha = getSkyboxBrightness(Minecraft.getInstance().level);

        RenderSystem.setShaderColor(alpha, alpha, alpha, 1F);

        this.skyboxBuffer.bind();
        this.skyboxBuffer.drawWithShader(poseStack.last()
                                                  .pose(), projectionMatrix, GameRenderer.getPositionTexShader());
    }

    public void paint(SkyboxPainter painter) {
        NativeImage skyNativeTex = this.skyTexture.getPixels();
        CountDownLatch latch = new CountDownLatch(PaintTask.TextureLocation.values().length);

        for (PaintTask.TextureLocation location : PaintTask.TextureLocation.values()) {
            skyExecutor.execute(new PaintTask(skyNativeTex, painter, location, latch));
        }

        try {
            latch.await(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
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

                    skyNativeTex.setPixelRGBA(location.getXLocation(texX), location.getYLocation(texY), painter.getTexelColour(x, y, z));
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
            TOP(CoordMap.X.getMap(), CoordMap.ONE.getMap(), CoordMap.Y.getMap(), 2, 0),
            BOTTOM(CoordMap.X.getMap(), CoordMap.NEG_ONE.getMap(), CoordMap.Y.getMap(), 2, 2),
            POS_Z(CoordMap.X.getMap(), CoordMap.Y.getMap(), CoordMap.ONE.getMap(), 1, 1),
            NEG_Z(CoordMap.X.getMap(), CoordMap.Y.getMap(), CoordMap.NEG_ONE.getMap(), 3, 1),
            POS_X(CoordMap.ONE.getMap(), CoordMap.Y.getMap(), CoordMap.X.getMap(), 2, 1),
            NEG_X(CoordMap.NEG_ONE.getMap(), CoordMap.Y.getMap(), CoordMap.X.getMap(), 0, 1);

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

    private void generateVertices() {
        BufferBuilder skyboxBuilder = Tesselator.getInstance().getBuilder();
        skyboxBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);

        // +z face
        skyboxBuilder.vertex(-80F, -80F, 80F).uv(0.25f, 0.25f).endVertex();
        skyboxBuilder.vertex(-80F, 80F, 80F).uv(0.25f, 0.5f).endVertex();
        skyboxBuilder.vertex(80F, 80F, 80F).uv(0.5f, 0.5f).endVertex();
        skyboxBuilder.vertex(80F, -80F, 80F).uv(0.5f, 0.25f).endVertex();

        // -z face
        skyboxBuilder.vertex(-80F, -80F, -80F).uv(0.75f, 0.25f).endVertex();
        skyboxBuilder.vertex(80F, -80F, -80F).uv(1f, 0.25f).endVertex();
        skyboxBuilder.vertex(80F, 80F, -80F).uv(1f, 0.5f).endVertex();
        skyboxBuilder.vertex(-80F, 80F, -80F).uv(0.75f, 0.5f).endVertex();

        // bottom face
        skyboxBuilder.vertex(-80F, -80F, -80F).uv(0.5f, 0.5f).endVertex();
        skyboxBuilder.vertex(-80F, -80F, 80F).uv(0.5f, 0.75f).endVertex();
        skyboxBuilder.vertex(80F, -80F, 80F).uv(0.75f, 0.75f).endVertex();
        skyboxBuilder.vertex(80F, -80F, -80F).uv(0.75f, 0.5f).endVertex();

        // top face
        skyboxBuilder.vertex(-80F, 80F, -80F).uv(0.5f, 0f).endVertex();
        skyboxBuilder.vertex(80F, 80F, -80F).uv(0.75f, 0f).endVertex();
        skyboxBuilder.vertex(80F, 80F, 80F).uv(0.75f, 0.25f).endVertex();
        skyboxBuilder.vertex(-80F, 80F, 80F).uv(0.5f, 0.25f).endVertex();

        // +x face
        skyboxBuilder.vertex(80F, -80F, -80F).uv(0.5f, 0.25f).endVertex();
        skyboxBuilder.vertex(80F, -80F, 80F).uv(0.75f, 0.25f).endVertex();
        skyboxBuilder.vertex(80F, 80F, 80F).uv(0.75f, 0.5f).endVertex();
        skyboxBuilder.vertex(80F, 80F, -80F).uv(0.5f, 0.5f).endVertex();

        // -x face
        skyboxBuilder.vertex(-80F, -80F, -80F).uv(0f, 0.25f).endVertex();
        skyboxBuilder.vertex(-80F, 80F, -80F).uv(0f, 0.5f).endVertex();
        skyboxBuilder.vertex(-80F, 80F, 80F).uv(0.25f, 0.5f).endVertex();
        skyboxBuilder.vertex(-80F, -80F, 80F).uv(0.25f, 0.25f).endVertex();

        skyboxBuffer.bind();
        skyboxBuffer.upload(skyboxBuilder.end());
    }

    private float getSkyboxBrightness(ClientLevel level) {
        Config config = NicerSkies.getInstance().getConfig();

        float strength = config.getNebulaStrength();
        boolean renderDuringDay = config.getRenderDuringDay();

        float timeOfDay = level.getTimeOfDay(0);
        float nightness = 1F - (Mth.cos(timeOfDay * (float) (Math.PI * 2)) * 4.0F + 0.5F);
        nightness = Mth.clamp(nightness, (renderDuringDay ? 1f : 0f), 1.0F);

        float rain = level.getRainLevel(0);

        return nightness * (1f - rain) * strength;
    }
}
