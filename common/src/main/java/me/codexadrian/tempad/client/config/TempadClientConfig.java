package me.codexadrian.tempad.client.config;

import com.teamresourceful.resourcefulconfig.common.annotations.Comment;
import com.teamresourceful.resourcefulconfig.common.annotations.Config;
import com.teamresourceful.resourcefulconfig.common.annotations.ConfigEntry;
import com.teamresourceful.resourcefulconfig.common.config.EntryType;
import me.codexadrian.tempad.client.TempadClient;
import me.codexadrian.tempad.common.Tempad;

@Config("tempad-client")
public class TempadClientConfig {
    @ConfigEntry(id = "color", type = EntryType.INTEGER, translation = "config.tempad.color")
    @Comment("The color of the Tempad's gui and the portals.")
    public static int color = Tempad.ORANGE;

    @ConfigEntry(id = "colorOptions", type = EntryType.INTEGER, translation = "config.tempad.color_options")
    @Comment("The color options for the Tempad's gui and the portals.")
    public static int[] colorOptions = {
            0xFFFFFF,
            0xF51302,
            0xF77B05,
            0xF89506,
            0xFAB306,
            0xFBCF01,
            0xFEF304,
            0xEBFE05,
            0xCBFD03,
            0x82FE01,
            0x53FE00,
            0x53FE84,
            0x53FEB1,
            0x52FEDF,
            0x52FEF8,
            0x45DAFE,
            0x3ABDFE,
            0x2A93FB,
            0x165EFB,
            0x061AFB,
            0x471AFC,
            0x6519FC,
            0x7C19FC,
            0x9019FE,
            0xB319FD,
            0xD618FC,
            0xF418FC,
            0xEE28B0,
            0xEC3785,
            0xEB3860,
    };

    @ConfigEntry(id = "renderBlur", type = EntryType.BOOLEAN, translation = "config.tempad.render_blur")
    @Comment("Whether or not to render the blur on summoned Timedoors. This feature is experimental, having it enabled may make it incompatible with certain mods.")
    public static boolean isFancyTimedoorRendererEnabled = !TempadClient.isIncompatibleModLoaded();
}
