package dev.ztereohype.nicerskies.mixin;

import dev.ztereohype.nicerskies.ClientLevelAccessor;
import dev.ztereohype.nicerskies.NicerSkies;
import dev.ztereohype.nicerskies.config.Config;
import dev.ztereohype.nicerskies.sky.NicerSkiesRenderer;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.renderer.extract.LevelExtractor;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(ClientLevel.class)
public abstract class ClientLevelMixin implements ClientLevelAccessor {
    @Unique
    private long hashedSeed;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void nicerSkies_saveHashedSeed(ClientPacketListener connection, ClientLevel.ClientLevelData levelData, ResourceKey dimension, Holder dimensionType, int serverChunkRadius, int serverSimulationDistance, LevelExtractor levelExtractor, boolean isDebug, long biomeZoomSeed, int seaLevel, CallbackInfo ci) {
        this.hashedSeed = biomeZoomSeed; // store the seed to be able to regenerate things in settings

        // Note: sometimes, the seed is received after the buffers have been built by the renderer.
        // In that case it is safe to finish generating the sky.
        NicerSkiesRenderer renderer = NicerSkies.getInstance().getRenderer();
        if (renderer.hasBuiltBuffers()) {
            renderer.generateSky(biomeZoomSeed);
        }
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void nicerSkies_tickStars(CallbackInfo ci) {
        Config config = NicerSkies.getInstance().getConfig();
        NicerSkiesRenderer nicerSkiesRenderer = NicerSkies.getInstance().getRenderer();

        if (!config.areTwinlkingStarsEnabled()) return;

        ProfilerFiller profilerFiller = Profiler.get();
        profilerFiller.push("Nicer Skies");
        nicerSkiesRenderer.tick();
        profilerFiller.pop();
    }
    @Override
    public long nicerSkies_getHashedSeed() {
        return hashedSeed;
    }
}
