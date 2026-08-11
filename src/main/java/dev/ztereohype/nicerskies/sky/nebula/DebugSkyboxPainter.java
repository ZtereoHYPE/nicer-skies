package dev.ztereohype.nicerskies.sky.nebula;

import net.minecraft.util.ARGB;


public class DebugSkyboxPainter extends SkyboxPainter {
    @Override
    int getTexelColour(float x, float y, float z) {
        return ARGB.color(255, 0, 0, 255);
    }
}
