package codes.ztereohype.nicerskies.gui.widget;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.components.TooltipAccessor;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

import java.util.ArrayList;
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
        if (tooltip == null) return List.of();
        String tooltipText = tooltip.getString();

        // horrible
        String[] words = tooltipText.split(" +");
        List<String> lines = new ArrayList<>();
        String line = "";
        for (String word : words) {
            if (line.length() + word.length() > 30) {
                lines.add(line);
                line = "";
            }
            line += word + " ";
        }
        lines.add(line);

        return lines.stream().map((str)-> FormattedCharSequence.forward(str, tooltip.getStyle())).toList();
    }

    @Override
    public void render(PoseStack poseStack, int i, int j, float f) {
        this.setAlpha(this.active ? 1.0F : 0.5F);
        super.render(poseStack, i, j, f);
    }
}
