package me.codexadrian.tempad.client.config;

import com.teamresourceful.resourcefulconfig.api.annotations.Comment;
import com.teamresourceful.resourcefulconfig.api.annotations.Config;
import com.teamresourceful.resourcefulconfig.api.annotations.ConfigEntry;
import com.teamresourceful.resourcefulconfig.api.types.options.EntryType;
import me.codexadrian.tempad.common.Tempad;

@Config("tempad-client")
public final class TempadClientConfig {
    @ConfigEntry(id = "color", type = EntryType.INTEGER, translation = "config.tempad.color")
    @Comment("The color of the Tempad's gui text and the portals.")
    public static int color = Tempad.ORANGE;

    @ConfigEntry(id = "renderBlur", type = EntryType.BOOLEAN, translation = "config.tempad.render_blur")
    @Comment("Whether or not to render the blur on summoned Timedoors. This feature is experimental, having it enabled may make it incompatible with certain mods.")
    public static boolean isFancyTimedoorRendererEnabled = false;
}
