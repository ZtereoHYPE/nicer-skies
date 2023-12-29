package dev.ztereohype.nicerskies.mixin;

import com.mojang.blaze3d.vertex.VertexBuffer;
import net.minecraft.client.renderer.LevelRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(LevelRenderer.class)
public interface LevelRendererAccessor {
    @Accessor("starBuffer")
    VertexBuffer nicerSkies_getStarBuffer();

    @Accessor("ticks")
    int nicerSkies_getTicks();
}
