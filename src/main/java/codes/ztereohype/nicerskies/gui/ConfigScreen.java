package codes.ztereohype.nicerskies.gui;

import codes.ztereohype.nicerskies.NicerSkies;
import codes.ztereohype.nicerskies.config.ConfigManager;
import codes.ztereohype.nicerskies.core.NebulaSeedManager;
import codes.ztereohype.nicerskies.gui.widget.Separator;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public class ConfigScreen extends Screen {
    private final Screen lastScreen;
    private final ConfigManager cm = NicerSkies.config;

    private boolean invalidated = false;

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

        addRenderableWidget(new Checkbox(20, 84, 20, 20, Component.literal("Twinlke Stars"), cm.getTwinklingStars()) {
            @Override
            public void onPress() {
                super.onPress();
                cm.setTwinklingStars(!cm.getTwinklingStars());
            }
        });

        addRenderableWidget(new Checkbox(20, 108, 20, 20, Component.literal("Custom Lightmap"), cm.getLightmapTweaked()) {
            @Override
            public void onPress() {
                super.onPress();
                cm.setLightmapTweaked(!cm.getLightmapTweaked());
                Minecraft.getInstance().gameRenderer.lightTexture().tick();
            }
        });

        addRenderableOnly(new Separator(this.width / 2, 30, this.height - 70));

//        CycleButton<NebulaType> nebulaType = CycleButton.builder((NebulaType value) -> Component.literal(value.getTypeString()))
//                                                        .withValues(NebulaType.values())
//                                                        .create(this.width / 2 + (this.width / 2 - 150) / 2, 60, 150, 20, Component.literal("Nebula Type"), (button, value) -> {
//                                                            invalidated = true;
//                                                            NicerSkies.config.setNebulaType(value);
//                                                        });
//
//        if (NebulaType.values().length < 2)
//            nebulaType.active = false; // deactivate while theres only one!
//
//        addRenderableWidget(nebulaType);

        float strength = cm.getNebulaStrength();
        addRenderableWidget(new AbstractSliderButton(this.width / 2 + (this.width / 2 - 150) / 2, 60, 150, 20, Component.literal("Nebula Strength: " + (int) (strength * 100) + "%"), strength) {
            @Override
            protected void updateMessage() {
                this.setMessage(Component.literal("Nebula Strength: " + (int) (this.value * 100) + "%"));
            }

            @Override
            protected void applyValue() {
                NicerSkies.config.setNebulaStrength((float) this.value);
            }
        });

        float noiseAmount = cm.getNebulaNoiseAmount();
        addRenderableWidget(new AbstractSliderButton(this.width / 2 + (this.width / 2 - 150) / 2, 84, 150, 20, Component.literal("Nebula Amount: " + (int) (noiseAmount * 100) + "%"), noiseAmount) {
            @Override
            protected void updateMessage() {
                invalidated = true;
                this.setMessage(Component.literal("Nebula Amount: " + (int) (this.value * 100) + "%"));
            }

            @Override
            protected void applyValue() {
                NicerSkies.config.setNebulaNoiseAmount((float) this.value);
            }
        });

        int baseColourAmount = cm.getNebulaBaseColourAmount();
        addRenderableWidget(new AbstractSliderButton(this.width / 2 + (this.width / 2 - 150) / 2, 108, 150, 20, Component.literal("Background Strength: " + baseColourAmount), baseColourAmount / 255f) {
            @Override
            protected void updateMessage() {
                invalidated = true;
                this.setMessage(Component.literal("Background Strength: " + (int) (this.value * 255)));
            }

            @Override
            protected void applyValue() {
                NicerSkies.config.setNebulaBaseColourAmount((int) (this.value * 255));
            }
        });

        float nebulaNoiseScale = cm.getNebulaNoiseScale();
        addRenderableWidget(new AbstractSliderButton(this.width / 2 + (this.width / 2 - 150) / 2, 132, 150, 20, Component.literal("Nebula Scale: " + nebulaNoiseScale), Math.round((nebulaNoiseScale - 0.5f) / 1.5f * 100) / 100f) {
            @Override
            protected void updateMessage() {
                invalidated = true;
                this.setMessage(Component.literal("Nebula Scale: " + (getNebulaNoiseScale())));
            }

            @Override
            protected void applyValue() {
                NicerSkies.config.setNebulaNoiseScale(getNebulaNoiseScale());
            }

            private float getNebulaNoiseScale() {
                return (float) Math.round((this.value * 1.5f + 0.5f) * 100) / 100f;
            }
        });

        //reload nebula button
        addRenderableWidget(new Button(this.width / 2 + (this.width / 2 - 150) / 2, 156, 74, 20, Component.literal("Apply"), (button) -> {
            NicerSkies.skyManager.generateSky(NebulaSeedManager.getSeed());
            invalidated = false;
        }) {
            @Override
            public void render(@NotNull PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
                this.active = invalidated && NebulaSeedManager.canGetSeed();
                super.render(poseStack, mouseX, mouseY, partialTick);
            }
        });

        //reset to default
        addRenderableWidget(new Button(this.width / 2 + (this.width / 2 - 150) / 2 + 76, 156, 74, 20, Component.literal("Reset"), (button) -> {
            cm.resetNebulaSettings();
            this.clearWidgets();
            this.init();
            invalidated = true;
        }) {
            @Override
            public void render(@NotNull PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
                this.active = !isDefaultNebulaSettings();
                super.render(poseStack, mouseX, mouseY, partialTick);
            }
        });


        addRenderableWidget(new Button(this.width / 2 - 100, this.height - 30, 200, 20, Component.literal("Back"), (button) -> {
            this.onClose();
        }));
    }

    @Override
    public void render(@NotNull PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(poseStack);
        super.render(poseStack, mouseX, mouseY, partialTick);

        drawCenteredString(poseStack, this.font, this.title, this.width / 2, 10, 16777215);
        drawCenteredString(poseStack, this.font, "Toggle Features", this.width / 4, 36, 16777215);
        drawCenteredString(poseStack, this.font, "Nebula Settings", 3 * this.width / 4, 36, 16777215);
    }

    @Override
    public void onClose() {
        if (invalidated) {
            NicerSkies.skyManager.generateSky(NebulaSeedManager.getSeed());
        }
        minecraft.setScreen(lastScreen);
    }

    private boolean isDefaultNebulaSettings() {
        return cm.nebulaConfigEquals(ConfigManager.DEFAULT_CONFIG);
    }
}
