package me.codexadrian.tempad.common.config;

import com.teamresourceful.resourcefulconfig.api.annotations.*;
import com.teamresourceful.resourcefulconfig.api.types.options.EntryType;
import me.codexadrian.tempad.common.Tempad;

@Config(Tempad.MODID)
@ConfigInfo(
        icon = "watch",
        title = "Tempad",
        description = "Adds a device that allows you to teleport to any location stored on your Tempad.",
        links = {
                @ConfigInfo.Link(text = "CurseForge", value = "https://www.curseforge.com/minecraft/mc-mods/tempad", icon = "curseforge"),
                @ConfigInfo.Link(text = "Modrinth", value = "https://modrinth.com/mod/tempad", icon = "modrinth"),
                @ConfigInfo.Link(text = "GitHub", value = "https://modrinth.com/mod/tempad", icon = "github"),
                @ConfigInfo.Link(text = "Discord", value = "https://discord.terrarium.earth", icon = "gamepad-2")
        }
)
@ConfigInfo.Color("#ff6f00")
public final class TempadConfig {
    @ConfigEntry(id = "distanceFromPlayer", type = EntryType.INTEGER, translation = "config.tempad.distance_from_player")
    @Comment("The distance from the player that the Tempad is summoned.")
    @ConfigOption.Range(min = 1, max = 10)
    public static int distanceFromPlayer = 3;

    @ConfigEntry(id = "timedoorWaitTime", type = EntryType.INTEGER, translation = "config.tempad.timedoor_wait_time")
    @Comment("The amount of time in ticks that the timedoor will wait before closing itself after the owner walked through it.")
    public static int timedoorWait = 60;

    @ConfigEntry(id = "timedoorAddWaitTime", type = EntryType.INTEGER, translation = "config.tempad.timedoor_add_wait_time")
    @Comment("The amount of time in ticks that the Tempad will add to the wait time when the player is in the Tempad.")
    public static int timedoorAddWaitTime = 40;

    // allow interdimensional travel
    @ConfigEntry(id = "allowInterdimensionalTravel", type = EntryType.BOOLEAN, translation = "config.tempad.allow_interdimensional_travel")
    @Comment("Whether or not the Tempad should allow interdimensional travel.")
    public static boolean allowInterdimensionalTravel = true;

    // allow exporting of locations
    @ConfigEntry(id = "allowExporting", type = EntryType.BOOLEAN, translation = "config.tempad.allow_exporting")
    @Comment("Whether or not the Tempad should allow exporting of locations onto Location Cards.")
    public static boolean allowExporting = true;

    //enable waystones compat
    @ConfigEntry(id = "waystonesCompat", type = EntryType.BOOLEAN, translation = "config.tempad.waystones_compat")
    @Comment("Whether or not the Tempad should allow teleporting to waystones.")
    public static boolean waystonesCompat = true;

    //enable fabric waystones compat
    @ConfigEntry(id = "fabricWaystonesCompat", type = EntryType.BOOLEAN, translation = "config.tempad.fabric_waystones_compat")
    @Comment("Whether or not the Tempad should allow teleporting to waystones from the Fabric version of Waystones by LordDeatHunter (fwaystones) (ignore on forge).")
    public static boolean fabricWaystonesCompat = true;

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
