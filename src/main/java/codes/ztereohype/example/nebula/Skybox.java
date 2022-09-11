package codes.ztereohype.example.nebula;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Matrix4f;
import lombok.Getter;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.DynamicTexture;

public class Skybox {
    public static final int RESOLUTION = 256;

    public final DynamicTexture skyTexture = new DynamicTexture(RESOLUTION * 4, RESOLUTION * 4, false);

    private final @Getter VertexBuffer skyboxBuffer = new VertexBuffer();

    public Skybox() {
        generateVertices();
    }

    public void render(PoseStack poseStack, Matrix4f projectionMatrix) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, skyTexture.getId());

        this.skyboxBuffer.bind();
        this.skyboxBuffer.drawWithShader(poseStack.last()
                                                  .pose(), projectionMatrix, GameRenderer.getPositionTexShader());
        VertexBuffer.unbind();
    }

    private void generateVertices() {
        BufferBuilder skyboxBuilder = Tesselator.getInstance().getBuilder();
        skyboxBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);

        // +z face
        skyboxBuilder.vertex(-1F, -1F, 1F).uv(0.25f, 0.25f).endVertex();
        skyboxBuilder.vertex(-1F, 1F, 1F).uv(0.25f, 0.5f).endVertex();
        skyboxBuilder.vertex(1F, 1F, 1F).uv(0.5f, 0.5f).endVertex();
        skyboxBuilder.vertex(1F, -1F, 1F).uv(0.5f, 0.25f).endVertex();

        // -z face
        skyboxBuilder.vertex(-1F, -1F, -1F).uv(0.75f, 0.25f).endVertex();
        skyboxBuilder.vertex(1F, -1F, -1F).uv(1f, 0.25f).endVertex();
        skyboxBuilder.vertex(1F, 1F, -1F).uv(1f, 0.5f).endVertex();
        skyboxBuilder.vertex(-1F, 1F, -1F).uv(0.75f, 0.5f).endVertex();

        // bottom face
        skyboxBuilder.vertex(-1F, -1F, -1F).uv(0.5f, 0.5f).endVertex();
        skyboxBuilder.vertex(-1F, -1F, 1F).uv(0.5f, 0.75f).endVertex();
        skyboxBuilder.vertex(1F, -1F, 1F).uv(0.75f, 0.75f).endVertex();
        skyboxBuilder.vertex(1F, -1F, -1F).uv(0.75f, 0.5f).endVertex();

//        skyboxBuilder.vertex(-1F, -1F, -1F).uv(0f, 0f).endVertex();
//        skyboxBuilder.vertex(-1F, -1F, 1F).uv(0f, 1f).endVertex();
//        skyboxBuilder.vertex(1F, -1F, 1F).uv(1f, 1f).endVertex();
//        skyboxBuilder.vertex(1F, -1F, -1F).uv(1f, 0f).endVertex();

        // top face
        skyboxBuilder.vertex(-1F, 1F, -1F).uv(0.5f, 0f).endVertex();
        skyboxBuilder.vertex(1F, 1F, -1F).uv(0.75f, 0f).endVertex();
        skyboxBuilder.vertex(1F, 1F, 1F).uv(0.75f, 0.25f).endVertex();
        skyboxBuilder.vertex(-1F, 1F, 1F).uv(0.5f, 0.25f).endVertex();

        // +x face
        skyboxBuilder.vertex(1F, -1F, -1F).uv(0.5f, 0.25f).endVertex();
        skyboxBuilder.vertex(1F, -1F, 1F).uv(0.75f, 0.25f).endVertex();
        skyboxBuilder.vertex(1F, 1F, 1F).uv(0.75f, 0.5f).endVertex();
        skyboxBuilder.vertex(1F, 1F, -1F).uv(0.5f, 0.5f).endVertex();

        // -x face
        skyboxBuilder.vertex(-1F, -1F, -1F).uv(0f, 0.25f).endVertex();
        skyboxBuilder.vertex(-1F, 1F, -1F).uv(0f, 0.5f).endVertex();
        skyboxBuilder.vertex(-1F, 1F, 1F).uv(0.25f, 0.5f).endVertex();
        skyboxBuilder.vertex(-1F, -1F, 1F).uv(0.25f, 0.25f).endVertex();

        skyboxBuffer.bind();
        skyboxBuffer.upload(skyboxBuilder.end());
//        VertexBuffer.unbind();
    }
}
