package codes.ztereohype.example.mixin;

import codes.ztereohype.example.ExampleMod;
import com.mojang.math.Vector3f;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.util.Mth;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(LightTexture.class)
public class MixinLightTexutre {
//    @ModifyVariable(method = "updateLightTexture",
//                    at = @At("STORE"),
//                    ordinal = 9)
//    private float mixinRedBrightness(float original) {
//        if (ExampleMod.toggle) {
//            return 1F;
//        } else {
//            return original;
//        }
//    }
//    @ModifyArgs(
//            method = "updateLightTexture",
//            at = @At(value = "INVOKE", target = "Lcom/mojang/math/Vector3f;set(FFF)V", ordinal = 0)
//    )
//        private void mixinRedBrightness(Args args) {
//        if (ExampleMod.toggle) {
//            args.set(0, 1F);
//            args.set(1, args.get(1));
//            args.set(2, args.get(2));
//        }
//    }

    @Inject(
            method = "updateLightTexture",
            at = @At(value = "INVOKE", target = "Lcom/mojang/math/Vector3f;clamp(FF)V", shift = At.Shift.BEFORE, ordinal = 2),
            locals = LocalCapture.CAPTURE_FAILHARD
    )
    private void injectWarmLight(float partialTicks, CallbackInfo ci, ClientLevel clientLevel, float f, float g, float h, float i, float j, float l, float k, Vector3f vector3f, float m, Vector3f vector3f2, int n, int o, float p, float q, float r, float s, float t, boolean bl, float v, Vector3f vector3f5) {
        //todo: find a way to make a touch more saturated and brighter(?)
        if (ExampleMod.toggle) {
            Vector3f warmTint = new Vector3f(0.36F, 0.15F, -0.19F);

            float warmness = o / 15f * // increase w/ blocklight
                             (1f - vector3f.x() * (1 - n/15f)) * // decrease in skylight w/ dayness
                             Math.min((15 - o)/9f, 1f); // decrease for the 3 highest block light levels

            warmTint.mul(warmness);
            warmTint.add(1f, 1f, 1f);

            Vector3f dramaticFactor = vector3f2.copy();
            dramaticFactor.mul(0.25f);
            dramaticFactor.add(0.75f, 0.75f, 0.76f);

            vector3f2.mul(dramaticFactor.x(), dramaticFactor.y(), dramaticFactor.z());
            vector3f2.mul(warmTint.x(), warmTint.y(), warmTint.z());
        }
    }
}
