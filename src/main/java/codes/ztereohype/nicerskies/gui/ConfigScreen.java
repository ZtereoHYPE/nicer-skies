package codes.ztereohype.nicerskies.gui;

import codes.ztereohype.nicerskies.NicerSkies;
import codes.ztereohype.nicerskies.config.Config;
import codes.ztereohype.nicerskies.core.NebulaSeedManager;
import codes.ztereohype.nicerskies.gui.widget.Separator;
import codes.ztereohype.nicerskies.gui.widget.TooltippedCheckbox;
import codes.ztereohype.nicerskies.gui.widget.TooltippedSliderButton;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.TooltipAccessor;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ConfigScreen extends Screen {
    private final Screen lastScreen;
    private final Config config;
    private Config.ConfigData newConfig;

    private boolean invalidated = false;

    protected ConfigScreen(Screen lastScreen) {
        super(Component.translatable("nicer_skies.menu.settings"));
        this.lastScreen = lastScreen;

        this.config = NicerSkies.getInstance().getConfig();

        wipeConfig();
    }

    private void wipeConfig() {
        this.newConfig = config.getConfigData().toBuilder().build();
        this.newConfig.setNebulaConfig(config.getConfigData().getNebulaConfig().toBuilder().build());
    }

    @Override
    public void init() {
        int btnDst = 24;
        int nebulaOptMargin = this.width / 2 + (this.width / 2 - 150) / 2;

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

        addRenderableWidget(new TooltippedCheckbox(20, Y, 20, 20, Component.translatable("nicer_skies.option.render_nebulas"), renderNebulas, (selected) -> {
            newConfig.setRenderNebulas(selected);
            invalidated = true;
        }, null));

        addRenderableWidget(new TooltippedCheckbox(40, (Y += btnDst), 20, 20, Component.translatable("nicer_skies.option.dimensional_nebulas"), dimensionalNebulas, (selected) -> {
            newConfig.setNebulasInOtherDimensions(selected);
            invalidated = true;
        }, null) {
            @Override
            public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
                this.active = newConfig.isRenderNebulas();
                super.render(poseStack, mouseX, mouseY, partialTick);
            }
        });

        addRenderableWidget(new TooltippedCheckbox(20, (Y += btnDst), 20, 20, Component.translatable("nicer_skies.option.twinkle_stars"), twinkleStars, (selected) -> {
            newConfig.setTwinklingStars(selected);
            invalidated = true;
        }, null));

        addRenderableWidget(new TooltippedCheckbox(20, (Y += btnDst), 20, 20, Component.translatable("nicer_skies.option.custom_lightmap"), lightmapTweaked, (selected) -> {
            newConfig.setLightmapTweaked(selected);
            Minecraft.getInstance().gameRenderer.lightTexture().tick();
            invalidated = true;
        }, Component.translatable("nicer_skies.option.custom_lightmap.tooltip")));

        addRenderableOnly(new Separator(this.width / 2, 30, this.height - 70));
        Y = 60;

        // Render During Day
        addRenderableWidget(new TooltippedCheckbox(nebulaOptMargin, Y, 20, 20, Component.translatable("nicer_skies.option.render_during_day"), renderDuringDay, (selected) -> {
            newConfig.getNebulaConfig().setRenderDuringDay(selected);
            invalidated = true;
        }, Component.translatable("nicer_skies.option.render_during_day.tooltip")));

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
        }, Component.translatable("nicer_skies.option.nebula_transparency.tooltip")));

        // Nebula Amount
        addRenderableWidget(new TooltippedSliderButton(nebulaOptMargin, (Y += btnDst), 150, 20, Component.translatable("nicer_skies.option.nebula_amount",
                (int) (nebulaNoiseAmount *
                        100) +
                        "%"), nebulaNoiseAmount, value -> Component.translatable("nicer_skies.option.nebula_amount",
                (int) (value *
                        100) +
                        "%"), value -> {
            newConfig.getNebulaConfig().setNebulaNoiseAmount(value.floatValue());
            invalidated = true;
        }, Component.translatable("nicer_skies.option.nebula_amount.tooltip")));

        // Background Strength
        addRenderableWidget(new TooltippedSliderButton(nebulaOptMargin, Y += btnDst, 150, 20,
                Component.translatable("nicer_skies.option.background_strength", nebulaBaseColourAmount),
                nebulaBaseColourAmount / 255f,
                value -> Component.translatable("nicer_skies.option.background_strength", (int) (value * 255)),
                value -> {
            newConfig.getNebulaConfig().setBaseColourAmount((int) (value * 255));
            invalidated = true;
        }, Component.translatable("nicer_skies.option.background_strength.tooltip")));


        // Nebula Scale
        addRenderableWidget(new TooltippedSliderButton(nebulaOptMargin, Y += btnDst, 150, 20,
                Component.translatable("nicer_skies.option.nebula_scale", nebulaNoiseScale),
                mapValueToScale(nebulaNoiseScale), value -> Component.translatable("nicer_skies.option.nebula_scale", mapScaleToValue(value)),
                value -> {
            newConfig.getNebulaConfig().setNebulaNoiseScale(mapScaleToValue(value));
            invalidated = true;
        }, Component.translatable("nicer_skies.option.nebula_scale.tooltip")));

        // Reset
        addRenderableWidget(new Button(nebulaOptMargin, (Y += btnDst), 150, 20, Component.translatable("nicer_skies.menu.reset"), (button) -> {
            newConfig.setNebulaConfig(Config.DEFAULT_CONFIG.getNebulaConfig().toBuilder().build());
            this.rebuildWidgets();
            invalidated = true;
        }) {
            @Override
            public void render(@NotNull PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
                this.active = !isDefaultNebulaSettings();
                super.render(poseStack, mouseX, mouseY, partialTick);
            }
        });

        // Apply
        addRenderableWidget(new Button(this.width / 2 + 4, this.height - 28, 150, 20, Component.translatable("nicer_skies.menu.apply"), (button) -> {
            config.updateConfig(newConfig);
            regenerateSky();
            invalidated = false;
        }) {
            @Override
            public void render(@NotNull PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
                this.active = invalidated;
                super.render(poseStack, mouseX, mouseY, partialTick);
            }
        });

        // Back
        addRenderableWidget(new Button(this.width / 2 - 154, this.height - 28, 150, 20, Component.translatable("nicer_skies.menu.back"), (button) -> this.onClose()));
        super.init();
    }

    @Override
    public void render(@NotNull PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(poseStack);
        super.render(poseStack, mouseX, mouseY, partialTick);

        drawCenteredString(poseStack, this.font, this.title, this.width / 2, 10, 16777215);
        drawCenteredString(poseStack, this.font, Component.translatable("nicer_skies.menu.subtitle.feature_toggles"), this.width / 4, 36, 16777215);
        drawCenteredString(poseStack, this.font, Component.translatable("nicer_skies.menu.subtitle.nebula_settings"), 3 * this.width / 4, 36, 16777215);

        drawWrappedComponent(poseStack, Component.translatable("nicer_skies.menu.compatibility_warning"), 20, 160, this.width / 2 - 40);

        this.attemptRenderTooltip(poseStack, mouseX, mouseY);
    }

    @Override
    public void onClose() {
        wipeConfig();
        minecraft.setScreen(lastScreen);
    }

    private void attemptRenderTooltip(PoseStack poseStack, int mouseX, int mouseY) {
        for (GuiEventListener listener : this.children()) {
            if (listener instanceof TooltipAccessor) {
                if (listener.isMouseOver(mouseX, mouseY)) {
                    List<FormattedCharSequence> list = ((TooltipAccessor) listener).getTooltip();
                    if (!list.isEmpty()) {
                        this.renderTooltip(poseStack, list, mouseX, mouseY);
                    }
                }
            }
        }
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

    private void drawWrappedComponent(PoseStack poseStack, FormattedText component, int x, int y, int wrapWidth) {
        Minecraft mc = Minecraft.getInstance();
        List<FormattedText> lines = mc.font.getSplitter().splitLines(component, wrapWidth, Style.EMPTY);

        int amount = lines.size();
        for (int i = 0; i < amount; i++) {
            FormattedText renderable = lines.get(i);
            font.draw(poseStack, renderable.getString(), x, (float)(y + i * 9), 0xFFFFFFFF);
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
