package codes.ztereohype.nicerskies.mixin.debug;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.DebugScreenOverlay;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DebugScreenOverlay.class)
public abstract class MixinDebug {
    @Final @Shadow private Minecraft minecraft;

    @Inject(method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;)V", at = @At("HEAD"))
    private void injectRender(PoseStack poseStack, CallbackInfo ci) {
        int pixelSize = 6;

        NativeImage lightPixels = ((LightTextureInvoker) minecraft.gameRenderer.lightTexture()).getLightPixels();
        for (int x = 0; x < lightPixels.getWidth(); x++) {
            for (int y = 0; y < lightPixels.getHeight(); y++) {
                int colour = lightPixels.getPixelRGBA(x, y);

                int b = colour & 0xFF;
                int g = colour >> 8 & 0xFF;
                int r = colour >> 16 & 0xFF;

                int xCoord = minecraft.getWindow().getGuiScaledWidth() - (x * pixelSize);
                int yCoord = minecraft.getWindow().getGuiScaledHeight() - (y * pixelSize);

                GuiComponent.fill(poseStack, xCoord, yCoord, xCoord + pixelSize, yCoord + pixelSize, 0xFF000000 | b << 16 | g << 8 | r);
            }
        }
    }
}