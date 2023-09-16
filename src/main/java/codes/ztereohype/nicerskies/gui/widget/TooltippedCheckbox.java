package codes.ztereohype.nicerskies.gui.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

import java.util.function.Consumer;


public class TooltippedCheckbox extends Checkbox {
    private final Consumer<Boolean> onPress;

    public TooltippedCheckbox(int i, int j, int k, int l, Component component, boolean bl, Consumer<Boolean> onPress, Tooltip tooltip) {
        super(i, j, k, l, component, bl);

        this.onPress = onPress;
        this.setTooltip(tooltip);
    }

    @Override
    public void onPress() {
        super.onPress();
        onPress.accept(this.selected());
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int i, int j, float f) {
        this.setAlpha(this.active ? 1.0F : 0.5F);
        super.renderWidget(guiGraphics, i, j, f);
    }
}
