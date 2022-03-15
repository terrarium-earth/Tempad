package me.codexadrian.tempad;

import me.codexadrian.tempad.platform.Services;

import java.io.IOException;

public class Tempad {

    private static TempadConfig tempadConfig;

    public static void init() {
        try {
            tempadConfig = TempadConfig.loadConfig(Services.PLATFORM.getConfigDir());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static TempadConfig getTempadConfig() {
        return tempadConfig;
    }
}
