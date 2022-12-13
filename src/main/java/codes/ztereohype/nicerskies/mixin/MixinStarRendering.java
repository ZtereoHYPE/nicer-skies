package codes.ztereohype.nicerskies.mixin;

import codes.ztereohype.nicerskies.NicerSkies;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.world.level.material.FogType;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
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
        starBuffer = new VertexBuffer();

        BufferBuilder builder = Tesselator.getInstance().getBuilder();
        builder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);

        starBuffer.bind();
        starBuffer.upload(builder.end());

        ci.cancel();
    }

    @Inject(at = @At("HEAD"), method = "tick")
    private void tickStars(CallbackInfo ci) {
        if (!NicerSkies.config.getTwinklingStars() && NicerSkies.skyManager.isInitialized()) return;
        if (this.level.getStarBrightness(0) < 0.0F) return;
        NicerSkies.skyManager.tick(ticks, starBuffer);
    }

    @ModifyArg(
            method = "renderSky",
            at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/VertexBuffer;drawWithShader(Lorg/joml/Matrix4f;Lorg/joml/Matrix4f;Lnet/minecraft/client/renderer/ShaderInstance;)V", ordinal = 1),
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
    private void drawSkybox(PoseStack poseStack, Matrix4f matrix4f, float f, Camera camera, boolean bl, Runnable runnable, CallbackInfo ci, FogType fogType, Vec3 vec3, float g, float h, float i, BufferBuilder bufferBuilder, ShaderInstance shaderInstance, float[] fs, float j, Matrix4f matrix4f3, float l, int s, int t, int n, float u, float p, float q, float r) {
        if (!NicerSkies.config.getNebulas() || !NicerSkies.skyManager.isInitialized()) return;
        NicerSkies.skyManager.getSkybox().render(poseStack, matrix4f);
    }
}
