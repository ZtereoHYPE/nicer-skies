package dev.ztereohype.nicerskies.gui;

import dev.ztereohype.nicerskies.NicerSkies;
import dev.ztereohype.nicerskies.config.Config;
import dev.ztereohype.nicerskies.core.NebulaSeedManager;
import dev.ztereohype.nicerskies.gui.widget.Separator;
import dev.ztereohype.nicerskies.gui.widget.TooltippedCheckbox;
import dev.ztereohype.nicerskies.gui.widget.TooltippedSliderButton;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;

import java.util.List;
import java.util.function.Supplier;


public class ConfigScreen extends Screen {
    private final Screen lastScreen;
    private final Config config;
    private Config.ConfigData newConfig;

    private boolean invalidated = false;

    private TooltippedCheckbox dimensionalNebulasBtn;
    private Button resetBtn;
    private Button applyBtn;

    protected ConfigScreen(Screen lastScreen) {
        super(Component.translatable("nicer_skies.menu.settings"));
        this.lastScreen = lastScreen;

        this.config = NicerSkies.getInstance().getConfig();

        wipeConfig();
    }

    @Override
    public void init() {
        int btnDst = 24;
        int nebulaOptMargin = this.width / 2 + (this.width / 2 - 150) / 2;
        Font f = Minecraft.getInstance().font;

        // initial values
        boolean renderNebulas = newConfig.isRenderNebulas();
        boolean dimensionalNebulas = newConfig.isNebulasInOtherDimensions();
        boolean twinkleStars = newConfig.isTwinklingStars();
        boolean lightmapTweaked = newConfig.isLightmapTweaked();

        boolean renderDuringDay = newConfig.getNebulaConfig().isRenderDuringDay();
        float nebulaStrength = newConfig.getNebulaConfig().getNebulaStrength();
        float nebulaNoiseAmount = newConfig.getNebulaConfig().getNebulaNoiseAmount();
        int nebulaBaseColourAmount = newConfig.getNebulaConfig().getBaseColourAmount();
        float nebulaNoiseScale = newConfig.getNebulaConfig().getNebulaNoiseScale();

        int Y = 60;

        addRenderableWidget(new TooltippedCheckbox(20, Y, Component.translatable("nicer_skies.option.render_nebulas"), f, renderNebulas, (cb, selected) -> {
            newConfig.setRenderNebulas(selected);
            invalidated = true;
        }, null));

        this.dimensionalNebulasBtn = new TooltippedCheckbox(40, (Y += btnDst), Component.translatable("nicer_skies.option.dimensional_nebulas"), f, dimensionalNebulas, (cb, selected) -> {
            newConfig.setNebulasInOtherDimensions(selected);
            invalidated = true;
        }, null);
        addRenderableWidget(this.dimensionalNebulasBtn);

        addRenderableWidget(new TooltippedCheckbox(20, (Y += btnDst), Component.translatable("nicer_skies.option.twinkle_stars"), f, twinkleStars, (cb, selected) -> {
            newConfig.setTwinklingStars(selected);
            invalidated = true;
        }, null));

        addRenderableWidget(new TooltippedCheckbox(20, (Y += btnDst), Component.translatable("nicer_skies.option.custom_lightmap"), f, lightmapTweaked, (cb, selected) -> {
            newConfig.setLightmapTweaked(selected);
            Minecraft.getInstance().gameRenderer.lightTexture().tick();
            invalidated = true;
        }, Tooltip.create(Component.translatable("nicer_skies.option.custom_lightmap.tooltip"))));

        addRenderableOnly(new Separator(this.width / 2, 30, this.height - 70));
        Y = 60;

        // Render During Day
        addRenderableWidget(new TooltippedCheckbox(nebulaOptMargin, Y, Component.translatable("nicer_skies.option.render_during_day"), f, renderDuringDay, (cb, selected) -> {
            newConfig.getNebulaConfig().setRenderDuringDay(selected);
            invalidated = true;
        }, Tooltip.create(Component.translatable("nicer_skies.option.render_during_day.tooltip"))));

        // Nebula Strength
        addRenderableWidget(new TooltippedSliderButton(nebulaOptMargin, (Y += btnDst), 150, 20, Component.translatable("nicer_skies.option.nebula_transparency",
                                                                                                                       (int) (nebulaStrength *
                                                                                                                              100) +
                                                                                                                       "%"), nebulaStrength, value -> Component.translatable("nicer_skies.option.nebula_transparency",
                                                                                                                                                                             (int) (value *
                                                                                                                                                                                    100) +
                                                                                                                                                                             "%"), value -> {
            newConfig.getNebulaConfig().setNebulaStrength(value.floatValue());
            invalidated = true;
        }, Tooltip.create(Component.translatable("nicer_skies.option.nebula_transparency.tooltip"))));

        // Nebula Amount
        addRenderableWidget(new TooltippedSliderButton(nebulaOptMargin,
                                                       (Y += btnDst),
                                                       150,
                                                       20,
                                                       Component.translatable("nicer_skies.option.nebula_amount",
                                                                              (int) (nebulaNoiseAmount * 100) + "%"),
                                                       nebulaNoiseAmount,
                                                       value -> Component.translatable("nicer_skies.option.nebula_amount",
                                                                                       (int) (value * 100) +
                                                                                       "%"), value -> {
            newConfig.getNebulaConfig().setNebulaNoiseAmount(value.floatValue());
            invalidated = true;
        }, Tooltip.create(Component.translatable("nicer_skies.option.nebula_amount.tooltip"))));

        // Background Strength
        addRenderableWidget(new TooltippedSliderButton(nebulaOptMargin, Y += btnDst, 150, 20,
                Component.translatable("nicer_skies.option.background_strength", nebulaBaseColourAmount),
                nebulaBaseColourAmount / 255f,
                value -> Component.translatable("nicer_skies.option.background_strength", (int) (value * 255)),
                value -> {
            newConfig.getNebulaConfig().setBaseColourAmount((int) (value * 255));
            invalidated = true;
        }, Tooltip.create(Component.translatable("nicer_skies.option.background_strength.tooltip"))));


        // Nebula Scale
        addRenderableWidget(new TooltippedSliderButton(nebulaOptMargin, Y += btnDst, 150, 20,
                Component.translatable("nicer_skies.option.nebula_scale", nebulaNoiseScale),
                mapValueToScale(nebulaNoiseScale), value -> Component.translatable("nicer_skies.option.nebula_scale", mapScaleToValue(value)),
                value -> {
            newConfig.getNebulaConfig().setNebulaNoiseScale(mapScaleToValue(value));
            invalidated = true;
        }, Tooltip.create(Component.translatable("nicer_skies.option.nebula_scale.tooltip"))));

        // Reset
        this.resetBtn = Button.builder(Component.translatable("nicer_skies.menu.reset"), (button) -> {
            newConfig.setNebulaConfig(Config.DEFAULT_CONFIG.getNebulaConfig().toBuilder().build());
            this.rebuildWidgets();
            invalidated = true;
        }).bounds(nebulaOptMargin, (Y += btnDst), 150, 20).build(); addRenderableWidget(this.resetBtn);

        // Apply
        this.applyBtn = Button.builder(Component.translatable("nicer_skies.menu.apply"), (button) -> {
            config.updateConfig(newConfig);
            regenerateSky();
            invalidated = false;
        }).bounds(this.width / 2 + 4, this.height - 28, 150, 20).build();
        addRenderableWidget(this.applyBtn);

        // Back
        addRenderableWidget(Button.builder(Component.translatable("nicer_skies.menu.back"), (button) -> this.onClose())
                                  .pos(this.width / 2 - 154, this.height - 28)
                                  .size(150, 20)
                                  .build());
        super.init();
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        this.dimensionalNebulasBtn.active = this.newConfig.isRenderNebulas();
        this.resetBtn.active = !isDefaultNebulaSettings();
        this.applyBtn.active = this.invalidated;

        this.renderBackground(g, 0, 0, 0);
        super.render(g, mouseX, mouseY, partialTick);

        g.drawCenteredString(this.font, this.title, this.width / 2, 10, 16777215);
        g.drawCenteredString(this.font, Component.translatable("nicer_skies.menu.subtitle.feature_toggles"),
                this.width / 4, 36, 16777215);
        g.drawCenteredString(this.font, Component.translatable("nicer_skies.menu.subtitle.nebula_settings"),
                3 * this.width / 4, 36, 16777215);

        drawWrappedComponent(g, Component.translatable("nicer_skies.menu.compatibility_warning"), 20, 160,
                this.width / 2 - 40);
    }

    @Override
    public void onClose() {
        wipeConfig();
        minecraft.setScreen(lastScreen);
    }

    private void wipeConfig() {
        this.newConfig = config.getConfigData().toBuilder().build();
        this.newConfig.setNebulaConfig(config.getConfigData().getNebulaConfig().toBuilder().build());
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

    private void drawWrappedComponent(GuiGraphics g, FormattedText component, int x, int y, int wrapWidth) {
        Minecraft mc = Minecraft.getInstance();
        List<FormattedText> lines = mc.font.getSplitter().splitLines(component, wrapWidth, Style.EMPTY);

        int amount = lines.size();
        for (int i = 0; i < amount; i++) {
            FormattedText renderable = lines.get(i);
            g.drawString(font, renderable.getString(), x, y + i * 9, 0xFFFFFFFF);
        }
    }

    private float mapScaleToValue(double value) {
        return (float) Math.round(1f / (value * 1.5f + 0.5f) * 100) / 100f;
    }

    // inverse of above
    private float mapValueToScale(double value) {
        return (float) ((1f / value) - 0.5f) / 1.5f;
    }
}
