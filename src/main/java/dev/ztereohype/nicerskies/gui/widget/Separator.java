package dev.ztereohype.nicerskies.gui.widget;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Renderable;

public class Separator implements Renderable {
    private final int x;
    private final int y;
    private final int height;

    public Separator(int x, int y, int height) {
        this.x = x;
        this.y = y;
        this.height = height;
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        graphics.fill(x, y, x + 1, y + height, 0xFFFFFFFF);
    }
}
