package codes.ztereohype.nicerskies.gui.widget;

import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.TooltipAccessor;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;


public class TooltippedSliderButton extends AbstractSliderButton implements TooltipAccessor {
    private final Function<Double, Component> sliderMoved;
    private final Consumer<Double> sliderReleased;
    private final Component tooltip;

    public TooltippedSliderButton(int x, int y, int width, int height, Component text, double value, Function<Double, Component> sliderMoved, Consumer<Double> sliderReleased, Component tooltip) {
        super(x, y, width, height, text, value);

        this.sliderMoved = sliderMoved;
        this.sliderReleased = sliderReleased;
        this.tooltip = tooltip;
    }

    @Override
    protected void updateMessage() {
        this.setMessage(sliderMoved.apply(Double.valueOf(value)));
    }

    @Override
    protected void applyValue() {
        sliderReleased.accept(Double.valueOf(value));
    }

    @Override
    public @NotNull List<FormattedCharSequence> getTooltip() {
        if (tooltip == null) return List.of();
        return List.of(FormattedCharSequence.forward(tooltip.getString(), tooltip.getStyle()));
    }
}
