package me.codexadrian.tempad.utils;

import me.codexadrian.tempad.Tempad;
import me.codexadrian.tempad.TempadConfig;
import me.codexadrian.tempad.TempadType;

public class ConfigUtils {

    public static TempadConfig.TempadOptionConfig getOptionConfig(TempadType type) {
        return type == TempadType.NORMAL ? Tempad.getTempadConfig().getTempadOptions() : Tempad.getTempadConfig().getHeWhoRemainsOptions();
    }
}