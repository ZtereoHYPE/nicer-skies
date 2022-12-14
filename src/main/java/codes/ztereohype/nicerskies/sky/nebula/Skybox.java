package codes.ztereohype.nicerskies.sky.nebula;

import codes.ztereohype.nicerskies.NicerSkies;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Matrix4f;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.Mth;

public class Skybox {
    public static final int RESOLUTION = 512;

    private final DynamicTexture skyTexture = new DynamicTexture(RESOLUTION * 4, RESOLUTION * 4, false);

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

    // todo: maybe multithread the hecc out of the generation to sped up the loading time
    public void paint(SkyboxPainter painter) {
        NativeImage skyNativeTex = this.skyTexture.getPixels();

        // top face
        for (int texY = 0; texY < RESOLUTION; texY++) {
            for (int texX = 0; texX < RESOLUTION; texX++) {
                float x = (texX / (float) RESOLUTION) * 2 - 1;
                float y = 1;
                float z = (texY / (float) RESOLUTION) * 2 - 1;

                skyNativeTex.setPixelRGBA(texX + 2 * RESOLUTION, texY, painter.getColour(x, y, z));
            }
        }

        // bottom face
        for (int texY = 0; texY < RESOLUTION; texY++) {
            for (int texX = 0; texX < RESOLUTION; texX++) {
                float x = (texX / (float) RESOLUTION) * 2 - 1;
                float y = -1;
                float z = (texY / (float) RESOLUTION) * 2 - 1;

                skyNativeTex.setPixelRGBA(texX + 2 * RESOLUTION, texY + 2 * RESOLUTION, painter.getColour(x, y, z));
            }
        }

        // -x face
        for (int texY = 0; texY < RESOLUTION; texY++) {
            for (int texX = 0; texX < RESOLUTION; texX++) {
                float x = -1;
                float y = (texY / (float) RESOLUTION) * 2 - 1;
                float z = (texX / (float) RESOLUTION) * 2 - 1;

                skyNativeTex.setPixelRGBA(texX, texY + RESOLUTION, painter.getColour(x, y, z));
            }
        }

        // +x face
        for (int texY = 0; texY < RESOLUTION; texY++) {
            for (int texX = 0; texX < RESOLUTION; texX++) {
                float x = 1;
                float y = (texY / (float) RESOLUTION) * 2 - 1;
                float z = (texX / (float) RESOLUTION) * 2 - 1;

                skyNativeTex.setPixelRGBA(texX + 2 * RESOLUTION, texY + RESOLUTION, painter.getColour(x, y, z));
            }
        }

        // +z face
        for (int texY = 0; texY < RESOLUTION; texY++) {
            for (int texX = 0; texX < RESOLUTION; texX++) {
                float x = (texX / (float) RESOLUTION) * 2 - 1;
                float y = (texY / (float) RESOLUTION) * 2 - 1;
                float z = 1;

                skyNativeTex.setPixelRGBA(texX + RESOLUTION, texY + RESOLUTION, painter.getColour(x, y, z));
            }
        }

        // -z face
        for (int texY = 0; texY < RESOLUTION; texY++) {
            for (int texX = 0; texX < RESOLUTION; texX++) {
                float x = (texX / (float) RESOLUTION) * 2 - 1;
                float y = (texY / (float) RESOLUTION) * 2 - 1;
                float z = -1;

                skyNativeTex.setPixelRGBA(texX + 3 * RESOLUTION, texY + RESOLUTION, painter.getColour(x, y, z));
            }
        }

        this.skyTexture.upload();
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

//        skyboxBuilder.vertex(-1F, -1F, -1F).uv(0f, 0f).endVertex();
//        skyboxBuilder.vertex(-1F, -1F, 1F).uv(0f, 1f).endVertex();
//        skyboxBuilder.vertex(1F, -1F, 1F).uv(1f, 1f).endVertex();
//        skyboxBuilder.vertex(1F, -1F, -1F).uv(1f, 0f).endVertex();

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
        float config = NicerSkies.config.getNebulaStrength();

        float timeOfDay = level.getTimeOfDay(0);
        float nightness = 1F - (Mth.cos(timeOfDay * (float) (Math.PI * 2)) * 4.0F + 0.5F);
        nightness = Mth.clamp(nightness, (NicerSkies.config.getRenderDuringDay() ? 1f : 0f), 1.0F);

        float rain = level.getRainLevel(0);

        return nightness * (1f - rain) * config;
    }
}
