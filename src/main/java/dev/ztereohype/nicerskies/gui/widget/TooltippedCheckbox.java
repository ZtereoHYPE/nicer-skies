package dev.ztereohype.nicerskies.gui.widget;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;


public class TooltippedCheckbox extends Checkbox {
    public TooltippedCheckbox(int i, int j, Component component, Font font, boolean bl, Checkbox.OnValueChange onValueChange, Tooltip tooltip) {
        super(i, j, 128, component, font, bl, onValueChange);
        this.setTooltip(tooltip);
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int i, int j, float f) {
        this.setAlpha(this.active ? 1.0F : 0.5F);
        super.renderWidget(guiGraphics, i, j, f);
    }
}
