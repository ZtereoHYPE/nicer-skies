package codes.ztereohype.example.mixin;

import codes.ztereohype.example.ExampleMod;
import codes.ztereohype.example.StarManager;
import net.minecraft.client.KeyboardHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyboardHandler.class)
public class MixinKeyboardHandler {
    private long cooldown = 0;
    @Inject(at = @At("HEAD"), method = "keyPress")
    private void printKey(long windowPointer, int key, int scanCode, int action, int modifiers, CallbackInfo ci) {
        if (key == 92) {
//            StarManager.generateStarList();
            long time = System.currentTimeMillis();
            if (time - cooldown > 200) {
                ExampleMod.toggle = !ExampleMod.toggle;
                cooldown = time;
            }

        }
    }
}
