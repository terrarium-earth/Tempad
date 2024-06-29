package earth.terrarium.tempad.common.config;

import com.teamresourceful.resourcefulconfig.api.annotations.*;
import com.teamresourceful.resourcefulconfig.api.types.entries.Observable;
import com.teamresourceful.resourcefulconfig.api.types.options.EntryType;

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
public final class CommonConfig {
    @ConfigEntry(id = "allow_interdimensional_travel", type = EntryType.BOOLEAN)
    @Comment("Whether or not Time Doors are allowed to be opened to dimensions other than the one they are in.")
    public static boolean allowInterdimensionalTravel = true;

    @ConfigEntry(id = "allow_exporting", type = EntryType.BOOLEAN)
    @Comment("Whether or not Tempads are allowed to export their stored locations.")
    public static boolean allowExporting = true;

    @ConfigEntry(id = "allow_location_saving", type = EntryType.BOOLEAN)
    @Comment("Whether or not locations can be saved to the Tempad.")
    public static boolean allowLocationSaving = true;

    public static final class TimeDoor {
        @ConfigEntry(id = "placement_distance", type = EntryType.INTEGER)
        @Comment("The distance between the Time Door and the player when placed.")
        public static int placementDistance = 3;

        @ConfigEntry(id = "idle_after_enter", type = EntryType.INTEGER)
        @Comment("The amount of time in ticks that a Time Door will remain open after an entity (that isn't the owner) enters it.")
        public static int idleAfterEnter = 20;

        @ConfigEntry(id = "idle_after_owner_enter", type = EntryType.INTEGER)
        @Comment("The amount of time in ticks that a Time Door will remain open after the owner enters it.")
        public static int idleAfterOwnerEnter = 20;
    }

    public static final class Tempad {
        @ConfigEntry(id = "fuel_type", type = EntryType.STRING)
        @Comment("The type of fuel that the Tempad uses.")
        public static Observable<String> fuelType = Observable.of("tempad:none");

        @ConfigEntry(id = "consume_amount", type = EntryType.INTEGER)
        @Comment("The amount of fuel that the Tempad consumes per teleport.")
        public static Observable<Integer> consumeAmount = Observable.of(0);

        @ConfigEntry(id = "capacity", type = EntryType.INTEGER)
        @Comment("The amount of fuel that the Tempad can store.")
        public static Observable<Integer> capacity = Observable.of(0);

        @ConfigEntry(id = "cooldown_time", type = EntryType.INTEGER)
        @Comment("The amount of time in ticks that the Tempad will be on cooldown after a teleport.")
        public static Observable<Integer> cooldownTime = Observable.of(20 * 3 * 60);
    }

    public static final class AdvancedTempad {
        @ConfigEntry(id = "fuel_type", type = EntryType.STRING)
        @Comment("The type of fuel that the Advanced Tempad uses.")
        public static Observable<String> fuelType = Observable.of("tempad:none");

        @ConfigEntry(id = "consume_amount", type = EntryType.INTEGER)
        @Comment("The amount of fuel that the Advanced Tempad consumes per teleport.")
        public static Observable<Integer> consumeAmount = Observable.of(0);

        @ConfigEntry(id = "capacity", type = EntryType.INTEGER)
        @Comment("The amount of fuel that the Advanced Tempad can store.")
        public static Observable<Integer> capacity = Observable.of(0);

        @ConfigEntry(id = "cooldown_time", type = EntryType.INTEGER)
        @Comment("The amount of time in ticks that the Advanced Tempad will be on cooldown after a teleport.")
        public static Observable<Integer> cooldownTime = Observable.of(20 * 3 * 60);
    }

    public static final class ModCompat {
        @ConfigEntry(id = "enable_waystones", type = EntryType.BOOLEAN)
        @Comment("Whether or not compat with Waystones are enabled.")
        public static boolean enableWaystones = true;
    }
}
