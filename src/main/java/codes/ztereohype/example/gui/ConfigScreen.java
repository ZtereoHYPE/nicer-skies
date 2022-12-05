package codes.ztereohype.example.gui;

import codes.ztereohype.example.NicerSkies;
import codes.ztereohype.example.config.ConfigManager;
import codes.ztereohype.example.gui.widget.Separator;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class ConfigScreen extends Screen {
    private Screen lastScreen;
    private ConfigManager cm = NicerSkies.config;
    protected ConfigScreen(Screen lastScreen) {
        super(Component.literal("Nicer Skies Config"));
        this.lastScreen = lastScreen;
    }

    @Override
    public void init() {
        addRenderableWidget(new Checkbox(20, 60, 20, 20, Component.literal("Render nebulas"), cm.getNebulas()) {
            @Override
            public void onPress() {
                super.onPress();
                cm.setNebulas(!cm.getNebulas());
            }
        });
        addRenderableWidget(new Checkbox(20, 90, 20, 20, Component.literal("Twinlke Stars"), cm.getTwinklingStars()) {
            @Override
            public void onPress() {
                super.onPress();
                cm.setTwinklingStars(!cm.getTwinklingStars());
            }
        });
        addRenderableWidget(new Checkbox(20, 120, 20, 20, Component.literal("Custom Lightmap"), cm.getLightmapTweaked()) {
            @Override
            public void onPress() {
                super.onPress();
                cm.setLightmapTweaked(!cm.getLightmapTweaked());
            }
        });
        addRenderableOnly(new Separator(this.width / 2, 30, this.height - 40));

        addRenderableWidget(new Slider)
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(poseStack);
        super.render(poseStack, mouseX, mouseY, partialTick);

        drawCenteredString(poseStack, this.font, this.title, this.width / 2, 10, 16777215);
        drawCenteredString(poseStack, this.font, "Toggle Features", this.width / 4, 36, 16777215);
        drawCenteredString(poseStack, this.font, "Nebula Settings", 3 * this.width / 4, 36, 16777215);
    }
}
