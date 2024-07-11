package dev.ztereohype.nicerskies.sky.nebula;

import net.minecraft.util.FastColor;


public class DebugSkyboxPainter extends SkyboxPainter {
    @Override
    int getTexelColour(float x, float y, float z) {
        return FastColor.ARGB32.color(255,0,0,255);
    }
}
