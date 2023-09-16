package codes.ztereohype.nicerskies.gui.widget;

import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;

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
}
