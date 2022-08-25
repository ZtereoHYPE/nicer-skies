package codes.ztereohype.example.mixin;

import codes.ztereohype.example.sky.StarManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Matrix4f;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.world.level.material.FogType;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(LevelRenderer.class)
public abstract class MixinStarRendering {
    @Shadow private VertexBuffer starBuffer;
    @Shadow private int ticks;
    @Shadow private ClientLevel level;

    @Inject(at = @At("HEAD"), method = "createStars", cancellable = true)
    private void generateStars(CallbackInfo ci) {
        StarManager.generateSky();
        starBuffer = new VertexBuffer();
        StarManager.updateStars(ticks, starBuffer);
        ci.cancel();
    }

    @Inject(at = @At("HEAD"), method = "tick")
    private void tickStars(CallbackInfo ci) {
        if (this.level.getStarBrightness(0) < 0.0F) return;
        StarManager.updateStars(ticks, starBuffer);
    }

    @ModifyArg(
            method = "renderSky",
            at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/VertexBuffer;drawWithShader(Lcom/mojang/math/Matrix4f;Lcom/mojang/math/Matrix4f;Lnet/minecraft/client/renderer/ShaderInstance;)V", ordinal = 1),
            index = 2
    )
    private ShaderInstance injectStarColour(ShaderInstance shaderInstance) {
        return GameRenderer.getPositionColorShader();
    }

    @Inject(
            method = "renderSky",
            at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;disableTexture()V", ordinal = 2, shift = At.Shift.AFTER),
            locals = LocalCapture.CAPTURE_FAILHARD
    )
    private void drawNebulas(PoseStack poseStack, Matrix4f projectionMatrix, float partialTick, Camera camera, boolean bl, Runnable skyFogSetup, CallbackInfo ci, FogType fogType, Vec3 vec3, float f, float g, float h, BufferBuilder bufferBuilder, ShaderInstance shaderInstance, float[] fs, float i, Matrix4f matrix4f2, float k, int r, int s, int m, float t, float o, float p, float q) {
        Matrix4f matrix = matrix4f2;
//        matrix.multiply(projectionMatrix);

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, StarManager.skyTexture.getId());

        BufferBuilder leBufferBuilder = Tesselator.getInstance().getBuilder();
        leBufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);

        // +z face
        leBufferBuilder.vertex(matrix,  -1F, -1F, 1F).uv(0.25f, 0.25f).endVertex();
        leBufferBuilder.vertex(matrix, -1F, 1F, 1F).uv(0.25f, 0.5f).endVertex();
        leBufferBuilder.vertex(matrix, 1F, 1F, 1F).uv(0.5f, 0.5f).endVertex();
        leBufferBuilder.vertex(matrix, 1F, -1F, 1F).uv(0.5f, 0.25f).endVertex();

        // -z face
        leBufferBuilder.vertex(matrix, -1F, -1F, -1F).uv(0.75f, 0.25f).endVertex();
        leBufferBuilder.vertex(matrix, 1F, -1F, -1F).uv(1f, 0.25f).endVertex();
        leBufferBuilder.vertex(matrix, 1F, 1F, -1F).uv(1f, 0.5f).endVertex();
        leBufferBuilder.vertex(matrix, -1F, 1F, -1F).uv(0.75f, 0.5f).endVertex();

        // bottom face
        leBufferBuilder.vertex(matrix, -1F, -1F, -1F).uv(0.5f, 0.5f).endVertex();
        leBufferBuilder.vertex(matrix, -1F, -1F, 1F).uv(0.5f, 0.75f).endVertex();
        leBufferBuilder.vertex(matrix, 1F, -1F, 1F).uv(0.75f, 0.75f).endVertex();
        leBufferBuilder.vertex(matrix, 1F, -1F, -1F).uv(0.75f, 0.5f).endVertex();

//        leBufferBuilder.vertex(matrix, -1F, -1F, -1F).uv(0f, 0f).endVertex();
//        leBufferBuilder.vertex(matrix, -1F, -1F, 1F).uv(0f, 1f).endVertex();
//        leBufferBuilder.vertex(matrix, 1F, -1F, 1F).uv(1f, 1f).endVertex();
//        leBufferBuilder.vertex(matrix, 1F, -1F, -1F).uv(1f, 0f).endVertex();

        // top face
        leBufferBuilder.vertex(matrix, -1F, 1F, -1F).uv(0.5f, 0f).endVertex();
        leBufferBuilder.vertex(matrix, 1F, 1F, -1F).uv(0.75f, 0f).endVertex();
        leBufferBuilder.vertex(matrix, 1F, 1F, 1F).uv(0.75f, 0.25f).endVertex();
        leBufferBuilder.vertex(matrix, -1F, 1F, 1F).uv(0.5f, 0.25f).endVertex();

        // +x face
        leBufferBuilder.vertex(matrix, 1F, -1F, -1F).uv(0.5f, 0.25f).endVertex();
        leBufferBuilder.vertex(matrix, 1F, -1F, 1F).uv(0.75f, 0.25f).endVertex();
        leBufferBuilder.vertex(matrix, 1F, 1F, 1F).uv(0.75f, 0.5f).endVertex();
        leBufferBuilder.vertex(matrix, 1F, 1F, -1F).uv(0.5f, 0.5f).endVertex();

        // -x face
        leBufferBuilder.vertex(matrix, -1F, -1F, -1F).uv(0f, 0.25f).endVertex();
        leBufferBuilder.vertex(matrix, -1F, 1F, -1F).uv(0f, 0.5f).endVertex();
        leBufferBuilder.vertex(matrix, -1F, 1F, 1F).uv(0.25f, 0.5f).endVertex();
        leBufferBuilder.vertex(matrix, -1F, -1F, 1F).uv(0.25f, 0.25f).endVertex();

        BufferUploader.drawWithShader(leBufferBuilder.end());
    }
}
