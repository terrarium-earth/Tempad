package me.codexadrian.tempad.common.config;

import com.teamresourceful.resourcefulconfig.common.annotations.Comment;
import com.teamresourceful.resourcefulconfig.common.annotations.Config;
import com.teamresourceful.resourcefulconfig.common.annotations.ConfigEntry;
import com.teamresourceful.resourcefulconfig.common.annotations.IntRange;
import com.teamresourceful.resourcefulconfig.common.config.EntryType;
import com.teamresourceful.resourcefulconfig.web.annotations.Link;
import com.teamresourceful.resourcefulconfig.web.annotations.WebInfo;
import me.codexadrian.tempad.common.Tempad;

@Config(Tempad.MODID)
@WebInfo(
        icon = "watch",
        color = "#ff6f00",
        title = "Tempad",
        description = "Adds a device that allows you to teleport to any location stored on your Tempad.",
        links = {
                @Link(title = "CurseForge", value = "https://www.curseforge.com/minecraft/mc-mods/tempad", icon = "curseforge"),
                @Link(title = "Modrinth", value = "https://modrinth.com/mod/tempad", icon = "modrinth"),
                @Link(title = "GitHub", value = "https://modrinth.com/mod/tempad", icon = "github"),
                @Link(title = "Discord", value = "https://discord.terrarium.earth", icon = "gamepad-2")
        }
)
public final class TempadConfig {
    @ConfigEntry(id = "distanceFromPlayer", type = EntryType.INTEGER, translation = "config.tempad.distance_from_player")
    @Comment("The distance from the player that the Tempad is summoned.")
    @IntRange(min = 1, max = 10)
    public static int distanceFromPlayer = 3;

    @ConfigEntry(id = "timedoorWaitTime", type = EntryType.INTEGER, translation = "config.tempad.timedoor_wait_time")
    @Comment("The amount of time in ticks that the timedoor will wait before closing itself after the owner walked through it.")
    public static int timedoorWait = 60;

    @ConfigEntry(id = "timedoorAddWaitTime", type = EntryType.INTEGER, translation = "config.tempad.timedoor_add_wait_time")
    @Comment("The amount of time in ticks that the Tempad will add to the wait time when the player is in the Tempad.")
    public static int timedoorAddWaitTime = 40;

    @ConfigEntry(id = "timedoorFuelAmount", type = EntryType.INTEGER, translation = "config.tempad.timedoor_fuel_amount")
    @Comment("The amount of fuel that the timedoor will consume on opening of the timedoor.")
    public static int tempadFuelConsumptionValue = 180;

    @ConfigEntry(id = "timedoorFuelCapacity", type = EntryType.INTEGER, translation = "config.tempad.timedoor_fuel_capacity")
    @Comment("The amount of fuel that the timedoor can hold.")
    public static int tempadFuelCapacityValue = 1000;

    @ConfigEntry(id = "advancedTimedoorFuelAmount", type = EntryType.INTEGER, translation = "config.tempad.advanced_timedoor_fuel_amount")
    @Comment("The amount of fuel that the advanced timedoor will consume on opening of the timedoor.")
    public static int advancedTempadfuelConsumptionValue = 1;

    @ConfigEntry(id = "advancedTimedoorFuelCapacity", type = EntryType.INTEGER, translation = "config.tempad.advanced_timedoor_fuel_capacity")
    @Comment("The amount of fuel that the advanced timedoor can hold.")
    public static int advancedTempadfuelCapacityValue = 1000;

    @ConfigEntry(id = "timedoorFuelType", type = EntryType.STRING, translation = "config.tempad.timedoor_fuel_type")
    @Comment("The type of fuel that the timedoor will consume.")
    public static String tempadFuelType = "tempad:timer";

    @ConfigEntry(id = "advancedTimedoorFuelType", type = EntryType.STRING, translation = "config.tempad.advanced_timedoor_fuel_type")
    @Comment("The type of fuel that the advanced timedoor will consume.")
    public static String advancedTempadFuelType = "tempad:unlimited";
}
