package codes.ztereohype.nicerskies.gui;

import codes.ztereohype.nicerskies.NicerSkies;
import codes.ztereohype.nicerskies.config.Config;
import codes.ztereohype.nicerskies.core.NebulaSeedManager;
import codes.ztereohype.nicerskies.gui.widget.Separator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;

import java.util.List;
import java.util.function.Supplier;


public class ConfigScreen extends Screen {
    private final Screen lastScreen;
    private final Config config;
    private final Config.ConfigData newConfig;

    private boolean invalidated = false;

    protected ConfigScreen(Screen lastScreen) {
        super(Component.translatable("nicer_skies.menu.settings"));
        this.lastScreen = lastScreen;

        this.config = NicerSkies.getInstance().getConfig();
        this.newConfig = config.getConfigData().toBuilder().build();
    }

    @Override
    public void init() {
        int btnDst = 24;
        int nebulaOptMargin = this.width / 2 + (this.width / 2 - 150) / 2;

        // initial values
        boolean renderNebulas = newConfig.isRenderNebulas();
        boolean twinkleStars = newConfig.isTwinklingStars();
        boolean lightmapTweaked = newConfig.isLightmapTweaked();

        boolean renderDuringDay = newConfig.getNebulaConfig().isRenderDuringDay();
        float nebulaStrength = newConfig.getNebulaConfig().getNebulaStrength();
        float nebulaNoiseAmount = newConfig.getNebulaConfig().getNebulaNoiseAmount();
        int nebulaBaseColourAmount = newConfig.getNebulaConfig().getBaseColourAmount();
        float nebulaNoiseScale = newConfig.getNebulaConfig().getNebulaNoiseScale();

        int Y = 60;
        addRenderableWidget(new Checkbox(20, Y, 20, 20, Component.translatable("nicer_skies.option.render_nebulas"), renderNebulas) {
            @Override
            public void onPress() {
                super.onPress();
                newConfig.setRenderNebulas(this.selected());
                invalidated = true;
            }
        });

        addRenderableWidget(new Checkbox(20, (Y += btnDst), 20, 20, Component.translatable("nicer_skies.option.twinkle_stars"), twinkleStars) {
            @Override
            public void onPress() {
                super.onPress();
                newConfig.setTwinklingStars(this.selected());
                invalidated = true;
            }
        });

        addRenderableWidget(new Checkbox(20, (Y += btnDst), 20, 20, Component.translatable("nicer_skies.option.custom_lightmap"), lightmapTweaked) {
            @Override
            public void onPress() {
                super.onPress();
                newConfig.setLightmapTweaked(this.selected());
                Minecraft.getInstance().gameRenderer.lightTexture().tick();
                invalidated = true;
            }
        });

        addRenderableOnly(new Separator(this.width / 2, 30, this.height - 70));
        Y = 60;

        addRenderableWidget(new Checkbox(nebulaOptMargin, Y, 20, 20, Component.literal("Render During Day"), renderDuringDay) {
            @Override
            public void onPress() {
                super.onPress();
                newConfig.getNebulaConfig().setRenderDuringDay(this.selected());
                invalidated = true;
            }
        });

        addRenderableWidget(new AbstractSliderButton(nebulaOptMargin, (Y += btnDst), 150, 20, Component.translatable("nicer_skies.option.nebula_transparency", (int) (nebulaStrength * 100) + "%"), nebulaStrength) {
            @Override
            protected void updateMessage() {
                this.setMessage(Component.translatable("nicer_skies.option.nebula_transparency", (int) (this.value * 100) + "%"));
            }

            @Override
            protected void applyValue() {
                newConfig.getNebulaConfig().setNebulaStrength((float) this.value);
                invalidated = true;
            }
        });

        // Nebula Amount
        addRenderableWidget(new AbstractSliderButton(nebulaOptMargin, (Y += btnDst), 150, 20, Component.translatable("nicer_skies.option.nebula_amount", (int) (nebulaNoiseAmount * 100) + "%"), nebulaNoiseAmount) {
            @Override
            protected void updateMessage() {
                this.setMessage(Component.translatable("nicer_skies.option.nebula_amount",  (int) (this.value * 100) + "%"));
            }

            @Override
            protected void applyValue() {
                newConfig.getNebulaConfig().setNebulaNoiseAmount((float) this.value);
                invalidated = true;
            }
        });

        // Background Strength
        addRenderableWidget(new AbstractSliderButton(nebulaOptMargin, (Y += btnDst), 150, 20, Component.translatable("nicer_skies.option.background_strength", nebulaBaseColourAmount), nebulaBaseColourAmount / 255f) {
            @Override
            protected void updateMessage() {
                this.setMessage(Component.translatable("nicer_skies.option.background_strength", (int) (this.value * 255)));
            }

            @Override
            protected void applyValue() {
                newConfig.getNebulaConfig().setBaseColourAmount((int) (this.value * 255));
                invalidated = true;
            }
        });


        // Nebula Scale
        addRenderableWidget(new AbstractSliderButton(nebulaOptMargin, (Y += btnDst), 150, 20, Component.translatable("nicer_skies.option.nebula_scale", nebulaNoiseScale), mapValueToScale(nebulaNoiseScale)) {
            @Override
            protected void updateMessage() {
                this.setMessage(Component.translatable("nicer_skies.option.nebula_scale", mapScaleToValue((float) this.value)));
            }

            @Override
            protected void applyValue() {
                newConfig.getNebulaConfig().setNebulaNoiseScale(mapScaleToValue((float) this.value));
                invalidated = true;
            }
        });

        // Reset
        addRenderableWidget(new Button(nebulaOptMargin, (Y += btnDst), 150, 20, Component.translatable("nicer_skies.menu.reset"), (button) -> {
            newConfig.setNebulaConfig(Config.DEFAULT_CONFIG.getNebulaConfig().toBuilder().build());
            this.rebuildWidgets();
            invalidated = true;
        }, Supplier::get) {
            @Override
            public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
                this.active = !isDefaultNebulaSettings();
                super.render(g, mouseX, mouseY, partialTick);
            }
        });

        // Apply
        addRenderableWidget(new Button(this.width / 2 + 4, this.height - 28, 150, 20, Component.translatable("nicer_skies.menu.apply"), (button) -> {
            config.updateConfig(newConfig);
            regenerateSky();
            invalidated = false;
        }, Supplier::get) {
            @Override
            public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
                this.active = invalidated;
                super.render(g, mouseX, mouseY, partialTick);
            }
        });

        // Back
        addRenderableWidget(Button.builder(Component.translatable("nicer_skies.menu.back"), (button) -> this.onClose())
                                  .pos(this.width / 2 - 154, this.height - 28)
                                  .size(150, 20)
                                  .build());
        super.init();
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(g);
        super.render(g, mouseX, mouseY, partialTick);

        g.drawCenteredString(this.font, this.title, this.width / 2, 10, 16777215);
        g.drawCenteredString(this.font, Component.translatable("nicer_skies.menu.subtitle.feature_toggles"), this.width / 4, 36, 16777215);
        g.drawCenteredString(this.font, Component.translatable("nicer_skies.menu.subtitle.nebula_settings"), 3 * this.width / 4, 36, 16777215);

        drawWrappedComponent(g, Component.translatable("nicer_skies.menu.compatibility_warning"), 20, 150, this.width / 2 - 40, 0xFFFF00);
    }

    @Override
    public void onClose() {
        minecraft.setScreen(lastScreen);
    }

    private void regenerateSky() {
        if (NebulaSeedManager.canGenerateSky()) {
            NicerSkies.getInstance().getSkyManager().generateSky(NebulaSeedManager.getSeed());
        }
        invalidated = false;
    }

    private boolean isDefaultNebulaSettings() {
        return newConfig.getNebulaConfig().equals(Config.DEFAULT_CONFIG.getNebulaConfig());
    }

    private void drawWrappedComponent(GuiGraphics g, FormattedText component, int x, int y, int wrapWidth, int color) {
        Minecraft mc = Minecraft.getInstance();
        List<FormattedText> lines = mc.font.getSplitter().splitLines(component, wrapWidth, Style.EMPTY);

        int amount = lines.size();
        for (int i = 0; i < amount; i++) {
            FormattedText renderable = lines.get(i);
            g.drawString(font, renderable.getString(), x, y + i * 9, color);
        }
    }

    private float mapScaleToValue(float value) {
        return (float) Math.round(1f / (value * 1.5f + 0.5f) * 100) / 100f;
    }

    // inverse of above
    private float mapValueToScale(float value) {
        return ((1f / value) - 0.5f) / 1.5f;
    }
}
