package dev.ztereohype.nicerskies.mixin;

import dev.ztereohype.nicerskies.NicerSkies;
import dev.ztereohype.nicerskies.config.Config;
import dev.ztereohype.nicerskies.sky.NicerSkiesRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(value = LevelRenderer.class, priority = 999)
public abstract class LevelRendererMixin {
    @Shadow
    private int ticks;

    @Inject(at = @At("HEAD"), method = "tick")
    private void nicerSkies_tickStars(CallbackInfo ci) {
        Config config = NicerSkies.getInstance().getConfig();
        NicerSkiesRenderer nicerSkiesRenderer = NicerSkies.getInstance().getRenderer();

        if (!config.areTwinlkingStarsEnabled()) return;

        ProfilerFiller profilerFiller = Profiler.get();
        profilerFiller.push("Nicer Skies");
        nicerSkiesRenderer.tick(ticks);
        profilerFiller.pop();
    }
}
