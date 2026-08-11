package dev.ztereohype.nicerskies.mixin;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.ztereohype.nicerskies.NicerSkies;
import dev.ztereohype.nicerskies.config.Config;
import dev.ztereohype.nicerskies.core.HashedSeedManager;
import dev.ztereohype.nicerskies.sky.NicerSkiesRenderer;
import net.minecraft.client.renderer.SkyRenderer;
import net.minecraft.world.level.MoonPhase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(value = SkyRenderer.class)
abstract class SkyRendererMixin {
    // Note: The original star buffer is still built in order to allow toggling between the two
    @Inject(at = @At("TAIL"), method = "buildStars")
    private void buildStars(CallbackInfoReturnable<GpuBuffer> cir) {
        NicerSkiesRenderer renderer = NicerSkies.getInstance().getRenderer();
        renderer.buildBuffers();

        // note: this method gets called at every resource re-load.
        // Ff we already know the seed by this time, it means we reloaded resources
        // while in-game so we can already re-generate the stars and paint the nebulas
        if (HashedSeedManager.canGenerateSky()) {
            renderer.generateSky(HashedSeedManager.getSeed());
        }
    }

    @Inject(
            method = "renderSunMoonAndStars",
            at = @At(value = "INVOKE",
                     target = "Lcom/mojang/blaze3d/vertex/PoseStack;popPose()V",
                     ordinal = 3,
                     shift = At.Shift.BEFORE)
    )
    private void renderNebulas(PoseStack poseStack, float f, float g, float h, MoonPhase moonPhase, float i, float j, CallbackInfo ci) {
        Config config = NicerSkies.getInstance().getConfig();
        NicerSkiesRenderer nicerSkiesRenderer = NicerSkies.getInstance().getRenderer();

        if (config.areNebulasEnabled() && nicerSkiesRenderer.getSkybox() != null) {
            poseStack.pushPose();
            poseStack.mulPose(Axis.XP.rotation(h));
            float brightness = j * 2.0f; // max star brightness is 0.5
            nicerSkiesRenderer.getSkybox().render(poseStack, brightness);
            poseStack.popPose();
        }
    }

    @Inject(method = "renderStars", at = @At("HEAD"), cancellable = true)
    private void renderTwinklingStars(float f, PoseStack poseStack, CallbackInfo ci) {
        Config config = NicerSkies.getInstance().getConfig();
        NicerSkiesRenderer nicerSkiesRenderer = NicerSkies.getInstance().getRenderer();

        if (config.areTwinlkingStarsEnabled() && nicerSkiesRenderer.getStarbox() != null) {
            nicerSkiesRenderer.getStarbox().render(poseStack, f);
            ci.cancel();
        }
    }
}
