package codes.ztereohype.nicerskies.gui.widget;

import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.components.TooltipAccessor;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.inventory.tooltip.TooltipComponent;

import java.util.List;
import java.util.function.Consumer;


public class TooltippedCheckbox extends Checkbox implements TooltipAccessor {
    private final Consumer<Boolean> onPress;
    private final Component tooltip;

    public TooltippedCheckbox(int i, int j, int k, int l, Component component, boolean bl, Consumer<Boolean> onPress, Component tooltip) {
        super(i, j, k, l, component, bl);

        this.onPress = onPress;
        this.tooltip = tooltip;
    }

    @Override
    public void onPress() {
        super.onPress();
        onPress.accept(this.selected());
    }

    @Override
    public List<FormattedCharSequence> getTooltip() {
        return List.of(FormattedCharSequence.forward(tooltip.getString(), tooltip.getStyle()));
    }
}
