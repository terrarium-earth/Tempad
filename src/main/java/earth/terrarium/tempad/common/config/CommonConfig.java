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
@Config(value = "tempad", categories = {
    CommonConfig.TimeDoor.class,
    CommonConfig.Tempad.class,
    CommonConfig.AdvancedTempad.class
})
public final class CommonConfig {
    @ConfigEntry(id = "allow_interdimensional_travel", type = EntryType.BOOLEAN)
    @Comment("Whether or not Time Doors are allowed to be opened to dimensions other than the one they are in.")
    public static boolean allowInterdimensionalTravel = true;

    @ConfigEntry(id = "allow_intradimensional_travel", type = EntryType.BOOLEAN)
    @Comment("Whether or not Time Doors are allowed to be opened to the same dimension they are in.")
    public static boolean allowIntradimensionalTravel = true;

    @ConfigEntry(id = "allow_location_saving", type = EntryType.BOOLEAN)
    @Comment("Whether or not locations can be saved to the Tempad.")
    public static boolean allowLocationSaving = true;

    @ConfigEntry(id = "exp_per_charge", type = EntryType.INTEGER)
    @Comment("The amount of experience to consume from the player per charge of the tempad, where 1 charge = 1 opening of the timedoor")
    public static Observable<Integer> expPerCharge = Observable.of(40);

    @ConfigEntry(id = "energy_per_charge", type = EntryType.INTEGER)
    @Comment("The amount of energy to consume from an item per charge of the tempad, where 1 charge = 1 opening of the timedoor")
    public static Observable<Integer> energyPerCharge = Observable.of(1000);

    @ConfigEntry(id = "max_history_size", type = EntryType.INTEGER)
    @Comment("The maximum amount of locations that the Time Twister will save location history for.")
    public static int maxHistorySize = 512;

    @Category("timedoor")
    public static final class TimeDoor {
        @ConfigEntry(id = "placement_distance", type = EntryType.INTEGER)
        @Comment("The distance between the Time Door and the player when placed.")
        public static int placementDistance = 3;

        @ConfigEntry(id = "idle_after_enter", type = EntryType.INTEGER)
        @Comment("The amount of time in ticks that a Time Door will remain open after an entity (that isn't the owner) enters it.")
        public static int idleAfterEnter = 200;

        @ConfigEntry(id = "idle_after_owner_enter", type = EntryType.INTEGER)
        @Comment("The amount of time in ticks that a Time Door will remain open after the owner enters it.")
        public static int idleAfterOwnerEnter = 100;

        @ConfigEntry(id = "log_when_open", type = EntryType.BOOLEAN)
        @Comment("Whether or not the Time Door should log when it is opened in the console.")
        public static boolean logWhenOpen = false;
    }

    @Category("tempad")
    public static final class Tempad {
        @ConfigEntry(id = "fuel_type", type = EntryType.STRING)
        @Comment("The type of fuel that the Tempad uses.")
        public static Observable<String> fuelType = Observable.of("tempad:infinite");

        @ConfigEntry(id = "capacity", type = EntryType.INTEGER)
        @Comment("The amount of fuel that the Tempad can store.")
        public static Observable<Integer> capacity = Observable.of(0);

        @ConfigEntry(id = "cooldown_time", type = EntryType.INTEGER)
        @Comment("The amount of time in ticks that the Tempad will be on cooldown after a teleport.")
        public static Observable<Integer> cooldownTime = Observable.of(20 * 3 * 60);
    }

    @Category("advanced_tempad")
    public static final class AdvancedTempad {
        @ConfigEntry(id = "fuel_type", type = EntryType.STRING)
        @Comment("The type of fuel that the Advanced Tempad uses.")
        public static Observable<String> fuelType = Observable.of("tempad:infinite");

        @ConfigEntry(id = "capacity", type = EntryType.INTEGER)
        @Comment("The amount of fuel that the Advanced Tempad can store.")
        public static Observable<Integer> capacity = Observable.of(0);

        @ConfigEntry(id = "cooldown_time", type = EntryType.INTEGER)
        @Comment("The amount of time in ticks that the Advanced Tempad will be on cooldown after a teleport.")
        public static Observable<Integer> cooldownTime = Observable.of(20 * 3 * 60);
    }
}
