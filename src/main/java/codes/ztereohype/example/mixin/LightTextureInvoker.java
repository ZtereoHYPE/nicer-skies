package codes.ztereohype.example.mixin;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.dimension.DimensionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(LightTexture.class)
public interface LightTextureInvoker {
    @Accessor NativeImage getLightPixels();
}
