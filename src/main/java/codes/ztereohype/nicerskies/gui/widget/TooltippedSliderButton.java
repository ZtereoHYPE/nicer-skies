package codes.ztereohype.nicerskies.gui.widget;

import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;


public class TooltippedSliderButton extends AbstractSliderButton {
    private final Function<Double, Component> sliderMoved;
    private final Consumer<Double> sliderReleased;

    public TooltippedSliderButton(int x, int y, int width, int height, Component text, double value, Function<Double, Component> sliderMoved, Consumer<Double> sliderReleased, Tooltip tooltip) {
        super(x, y, width, height, text, value);

        this.sliderMoved = sliderMoved;
        this.sliderReleased = sliderReleased;
        this.setTooltip(tooltip);
    }

    @Override
    protected void updateMessage() {
        this.setMessage(sliderMoved.apply(Double.valueOf(value)));
    }

    @Override
    protected void applyValue() {
        sliderReleased.accept(Double.valueOf(value));
    }
}
