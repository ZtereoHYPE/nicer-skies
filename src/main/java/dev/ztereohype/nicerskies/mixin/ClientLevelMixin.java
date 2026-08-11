package dev.ztereohype.nicerskies.mixin;

import dev.ztereohype.nicerskies.ClientLevelAccessor;
import dev.ztereohype.nicerskies.NicerSkies;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
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
    private void saveHashedSeed(ClientPacketListener clientPacketListener, ClientLevel.ClientLevelData clientLevelData, ResourceKey resourceKey, Holder holder, int i, int j, LevelRenderer levelRenderer, boolean bl, long l, int k, CallbackInfo ci) {
        this.hashedSeed = l; // store the seed to be able to regenerate things in settings

        // build the required parts of the sky
        NicerSkies.getInstance().getRenderer().generateSky(l);
    }

    @Override
    public long nicerSkies_getHashedSeed() {
        return hashedSeed;
    }
}
