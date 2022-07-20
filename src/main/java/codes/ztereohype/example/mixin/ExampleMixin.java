package codes.ztereohype.example.mixin;

import codes.ztereohype.example.ExampleMod;
import codes.ztereohype.example.StarManager;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Matrix4f;
import net.minecraft.client.Camera;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.util.RandomSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LevelRenderer.class)
public class ExampleMixin {
	@Shadow private VertexBuffer starBuffer;
	@Shadow private int ticks;

	@Inject(at = @At("HEAD"), method = "createStars", cancellable = true)
	private void generateStars(CallbackInfo ci) {
		StarManager.generateStarList();
		starBuffer = new VertexBuffer();
		StarManager.updateStars(0, starBuffer);
		ci.cancel();
	}

	@Inject(at = @At("HEAD"), method = "tick")
	private void tickStars(CallbackInfo ci) {
		StarManager.updateStars(ticks, starBuffer);
	}

//	@Inject(at = @At(value="INVOKE", shift = At.Shift.AFTER, target = "Lcom/mojang/blaze3d/vertex/VertexBuffer;bind()V", ordinal = 1), method = "renderSky")
//	private void pushPoseStack(PoseStack poseStack, Matrix4f projectionMatrix, float partialTick, Camera camera, boolean bl, Runnable skyFogSetup, CallbackInfo ci) {
////		float[] scaling = ExampleMod.getScaling();
////		poseStack.pushPose();
////		poseStack.scale(scaling[0], scaling[1], scaling[2]);
//
//	}
//
//	@Inject(at = @At(value="INVOKE", shift = At.Shift.AFTER, target = "Lcom/mojang/blaze3d/vertex/VertexBuffer;drawWithShader(Lcom/mojang/math/Matrix4f;Lcom/mojang/math/Matrix4f;Lnet/minecraft/client/renderer/ShaderInstance;)V", ordinal = 1), method = "renderSky")
//	private void popPoseStack(PoseStack poseStack, Matrix4f projectionMatrix, float partialTick, Camera camera, boolean bl, Runnable skyFogSetup, CallbackInfo ci) {
////		poseStack.popPose();
//	}
}
