package codes.ztereohype.example.mixin;

import codes.ztereohype.example.sky.StarManager;
import com.mojang.blaze3d.vertex.VertexBuffer;
import net.minecraft.client.renderer.LevelRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public class MixinStarRendering {
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
}
