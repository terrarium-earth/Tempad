package me.codexadrian.tempad.api.apps;

import net.minecraft.client.gui.components.WidgetSprites;

public interface TempadApp {
    WidgetSprites getWidgetSprites();

    void open();
}
