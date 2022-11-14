package codes.ztereohype.example.gui;

import codes.ztereohype.example.config.Config;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.lang.reflect.Field;

public class ConfigScreen extends Screen {
    private Screen lastScreen;
    protected ConfigScreen(Screen lastScreen) {
        super(Component.literal("Nicer Skies Config"));
        this.lastScreen = lastScreen;
    }

    @Override
    public void init() {
        Field[] fields = Config.class.getFields();
        for (int i = 0; i < fields.length; i++) {
            int finalI = i;
            addRenderableWidget(new Button(0, 0, 100, 20, Component.literal(fields[i].getName()), (button) -> {
                try {
                    fields[finalI].setBoolean(null, !fields[finalI].getBoolean(null));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }));
        }
    }
}
