package codes.ztereohype.example.gui;

import codes.ztereohype.example.NicerSkies;
import codes.ztereohype.example.config.ConfigManager;
import codes.ztereohype.example.config.NebulaType;
import codes.ztereohype.example.core.NebulaSeedManager;
import codes.ztereohype.example.gui.widget.Separator;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.components.CycleButton;
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
                Minecraft.getInstance().gameRenderer.lightTexture().tick();
            }
        });

        addRenderableOnly(new Separator(this.width / 2, 30, this.height - 70));

        {
            CycleButton<NebulaType> nebulaType = CycleButton.builder((NebulaType value) -> Component.literal(value.getTypeString()))
                                                            .withValues(NebulaType.values())
                                                            .withTooltip((type) -> minecraft.font.split(Component.literal("Currently disabled as there's only one type of nebula"), 200))
                                                            .create(this.width / 2 + (this.width / 2 - 150) / 2, 60, 150, 20, Component.literal("Nebula Type"), (button, value) -> {
                                                                NicerSkies.config.setNebulaType(value);
                                                            });

            if (NebulaType.values().length < 2)
                nebulaType.active = false; // deactivate while theres only one!

            addRenderableWidget(nebulaType);
        }

        float strength = cm.getNebulaStrength();
        addRenderableWidget(new AbstractSliderButton(this.width / 2 + (this.width / 2 - 150) / 2, 90, 150, 20, Component.literal("Nebula Strength: " + (int) (strength * 100) + "%"), strength) {
            @Override
            protected void updateMessage() {
                this.setMessage(Component.literal("Nebula Strength: " + (int) (this.value * 100) + "%"));
            }

            @Override
            protected void applyValue() {
                NicerSkies.config.setNebulaStrength((float) this.value);
            }
        });

        //reload nebula button
        addRenderableWidget(new Button(this.width / 2 + (this.width / 2 - 150) / 2, 120, 150, 20, Component.literal("Reload Sky"), (button) -> {
            NicerSkies.skyManager.generateSky(NebulaSeedManager.getSeed());
        }));

        addRenderableWidget(new Button(this.width / 2 - 100, this.height - 30, 200, 20, Component.literal("Back"), (button) -> {
            minecraft.setScreen(lastScreen);
        }));
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
