package codes.ztereohype.nicerskies.mixin;

import codes.ztereohype.nicerskies.ClientLevelAccessor;
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

import java.util.function.Supplier;

@Mixin(ClientLevel.class)
public abstract class ClientLevelMixin implements ClientLevelAccessor {
    @Unique
    private long hashedSeed;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void generateSkies(ClientPacketListener clientPacketListener, ClientLevel.ClientLevelData clientLevelData, ResourceKey resourceKey, Holder holder, int i, int j, Supplier supplier, LevelRenderer levelRenderer, boolean bl, long hashedSeed, CallbackInfo ci) {
        this.hashedSeed = hashedSeed;
    }

    @Override
    public long nicerSkies_getHashedSeed() {
        return hashedSeed;
    }
}
