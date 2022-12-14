package codes.ztereohype.nicerskies.gui.widget;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.Renderable;
import org.jetbrains.annotations.NotNull;

public class Separator extends GuiComponent implements Renderable {
    private final int x;
    private final int y;
    private final int height;

    public Separator(int x, int y, int height) {
        this.x = x;
        this.y = y;
        this.height = height;
    }

    @Override
    public void render(@NotNull PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        fill(poseStack, x, y, x + 1, y + height, 0xFFFFFFFF);
    }
}
