package codes.ztereohype.nicerskies.mixin;

import codes.ztereohype.nicerskies.NicerSkies;
import codes.ztereohype.nicerskies.config.Config;
import codes.ztereohype.nicerskies.sky.SkyManager;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexBuffer;
import com.mojang.math.Matrix4f;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
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

@Mixin(value = LevelRenderer.class, priority = 999)
public abstract class LevelRendererMixin {
    @Shadow
    private VertexBuffer starBuffer;
    @Shadow
    private int ticks;
    @Shadow
    private ClientLevel level;

    @Shadow
    private Minecraft minecraft;

    @Inject(at = @At("HEAD"), method = "createStars", cancellable = true)
    private void generateStars(CallbackInfo ci) {
        Config config = NicerSkies.getInstance().getConfig();
        if (!config.areTwinlkingStarsEnabled()) return;

        starBuffer = new VertexBuffer();
        ci.cancel();
    }

    @Inject(at = @At("HEAD"), method = "tick")
    private void tickStars(CallbackInfo ci) {
        Config config = NicerSkies.getInstance().getConfig();
        SkyManager skyManager = NicerSkies.getInstance().getSkyManager();

        if (!config.areTwinlkingStarsEnabled()) return;
        if (this.level.getStarBrightness(0) < 0.0F) return;

        skyManager.tick(ticks);
    }

    @ModifyArg(
            method = "renderSky",
            at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/VertexBuffer;drawWithShader(Lcom/mojang/math/Matrix4f;Lcom/mojang/math/Matrix4f;Lnet/minecraft/client/renderer/ShaderInstance;)V", ordinal = 1),
            index = 2
    )
    private ShaderInstance injectStarColour(ShaderInstance shaderInstance) {
        Config config = NicerSkies.getInstance().getConfig();

        if (!config.areTwinlkingStarsEnabled()) return shaderInstance;

        return GameRenderer.getPositionColorShader();
    }

    @Inject(
            method = "renderSky",
            at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;disableTexture()V", ordinal = 2, shift = At.Shift.AFTER),
            locals = LocalCapture.CAPTURE_FAILHARD
    )
    private void drawSkybox(PoseStack poseStack, Matrix4f matrix4f, float f, Camera camera, boolean bl, Runnable runnable, CallbackInfo ci, FogType fogType, Vec3 vec3, float g, float h, float i, BufferBuilder bufferBuilder, ShaderInstance shaderInstance, float[] fs, float j, Matrix4f matrix4f3, float l, int s, int t, int n, float u, float p, float q, float r) {
        Config config = NicerSkies.getInstance().getConfig();
        SkyManager skyManager = NicerSkies.getInstance().getSkyManager();

        if (!config.renderInOtherDimensions() && minecraft.level.dimension() != ClientLevel.OVERWORLD) return;

        if (!config.areNebulasEnabled() || skyManager.getSkybox() == null) return;

        skyManager.getSkybox().render(poseStack, matrix4f);
    }
}
