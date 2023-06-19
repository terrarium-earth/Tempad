package me.codexadrian.tempad.common.utils;

import me.codexadrian.tempad.common.Tempad;
import me.codexadrian.tempad.common.TempadConfig;
import me.codexadrian.tempad.common.TempadType;

public class ConfigUtils {

    public static TempadConfig.TempadOptionConfig getOptionConfig(TempadType type) {
        return type == TempadType.NORMAL ? Tempad.getTempadConfig().getTempadOptions() : Tempad.getTempadConfig().getHeWhoRemainsOptions();
    }
}
