package earth.terrarium.tempad.common.config;

import com.teamresourceful.resourcefulconfig.api.annotations.*;
import com.teamresourceful.resourcefulconfig.api.types.entries.Observable;

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
    CommonConfig.RudimentaryTempad.class,
    CommonConfig.Tempad.class,
    CommonConfig.TimeTwister.class,
    CommonConfig.Chronometer.class,
    CommonConfig.Cell.class,
    CommonConfig.Metronome.class,
})
public final class CommonConfig {
    @ConfigEntry(id = "allow_interdimensional_travel")
    @Comment("Whether or not Time Doors are allowed to be opened to dimensions other than the one they are in.")
    public static boolean allowInterdimensionalTravel = true;

    @ConfigEntry(id = "allow_intradimensional_travel")
    @Comment("Whether or not Time Doors are allowed to be opened to the same dimension they are in.")
    public static boolean allowIntradimensionalTravel = true;

    @ConfigEntry(id = "allow_location_saving")
    @Comment("Whether or not locations can be saved to the Tempad.")
    public static boolean allowLocationSaving = true;

    @ConfigEntry(id = "max_history_size")
    @Comment("The maximum amount of locations that the Time Twister will save location history for.")
    public static int maxHistorySize = 512;

    @Category("timedoor")
    public static final class TimeDoor {
        @ConfigEntry(id = "placement_distance")
        @Comment("The distance between the Time Door and the player when placed.")
        public static int placementDistance = 3;

        @ConfigEntry(id = "log_when_open")
        @Comment("Whether or not the Time Door should log when it is opened in the console.")
        public static boolean logWhenOpen = false;

        @ConfigEntry(id= "cost_per_door")
        @Comment("The cost that any device requires to open a timedoor for 10 seconds")
        public static int costPerDoor = 1000;

        @ConfigEntry(id= "cost_to_persist")
        @Comment("The cost that any device requires to open add 1 second to a timedoor's life")
        public static int costToPersist = 10;

        @ConfigEntry(id = "time_in_world")
        @Comment("The amount of time in ticks that a Time Door will remain open")
        public static int timeInWorld = 200;
    }

    @Category("timedoor_projector")
    public static final class RudimentaryTempad {
        @ConfigEntry(id = "capacity")
        @Comment("The amount of fuel that the Rudimentary Tempad can store.")
        public static Observable<Integer> capacityRudi = Observable.of(4000);
    }

    @Category("tempad")
    public static final class Tempad {
        @ConfigEntry(id = "capacity")
        @Comment("The amount of fuel that the Tempad can store (without the time twister being installed).")
        public static Observable<Integer> capacityTempad = Observable.of(6000);

        @ConfigEntry(id = "max_offset")
        @Comment("The maximum range that you can set the portal position to be")
        public static Observable<Integer> maxOffsetTempad = Observable.of(5);
    }

    @Category("time_twister")
    public static final class TimeTwister {
        @ConfigEntry(id = "capacity")
        @Comment("The amount of fuel that the Time Twister can store.")
        public static Observable<Integer> capacityTimeTwister = Observable.of(3000);

        @ConfigEntry(id= "cost_to_backtrack")
        @Comment("The cost that the Time Twister consumes to backtrack to a location")
        public static int costToBacktrack = 500;
    }

    @Category("chronon_generator")
    public static final class ChrononGenerator {
        @ConfigEntry(id = "capacity")
        @Comment("The amount of fuel that the Chronometer can store.")
        public static Observable<Integer> capacitorGenerator = Observable.of(0);

        @ConfigEntry(id = "generation_rate")
        @Comment("The amount of time it takes for the Chronometer to generate 1 chronon (in ticks).")
        public static int generationRate = 48;

        @ConfigEntry(id = "generation_amount")
        @Comment("The amount of chronons generated everytime the Chronometer does a tick of work.")
        public static int generationAmount = 1;
    }

    @Category("chronometer")
    public static final class Chronometer {
        @ConfigEntry(id = "capacity")
        @Comment("The amount of fuel that the Chronometer can store.")
        public static Observable<Integer> capacityChronometer = Observable.of(0);

        @ConfigEntry(id = "generation_rate")
        @Comment("The amount of time it takes for the Chronometer to generate 1 chronon (in ticks).")
        public static int generationRate = 36;

        @ConfigEntry(id = "generation_amount")
        @Comment("The amount of chronons generated everytime the Chronometer does a tick of work.")
        public static int generationAmount = 1;
    }

    @Category("metronome")
    public static final class Metronome {
        @ConfigEntry(id = "capacity")
        @Comment("The amount of fuel that the Chronometer can store.")
        public static Observable<Integer> capacityMetronome = Observable.of(6000);

        @ConfigEntry(id = "generation_rate")
        @Comment("The amount of time it takes for the Chronometer to generate 1 chronon (in ticks).")
        public static int generationRate = 24;

        @ConfigEntry(id = "generation_amount")
        @Comment("The number of chronons generated everytime the Chronometer does a tick of work.")
        public static int generationAmount = 1;

        @ConfigEntry(id = "should_scale_generation")
        @Comment("Whether or not the number of chronons generated per cycle is scaled to the number of metronomes placed")
        public static Observable<Boolean> scaleGeneration = Observable.of(true);

        @ConfigEntry(id = "generation_rate")
        @Comment("The number of chronons thats transferred to each internal slot or to a neighboring block per tick.")
        public static int transferRate = 20;

        @ConfigEntry(id = "should_scale_generation")
        @Comment("Whether or not the number of chronons generated per cycle is scaled to the number of metronomes placed")
        public static int jumpStartAmount = 2000;
    }

    @Category("chronon_cell")
    public static final class Cell {
        @ConfigEntry(id = "capacity")
        @Comment("The amount of fuel that the Capacitor can store.")
        public static Observable<Integer> capacityCapacitor = Observable.of(2000);
    }

    @Category("chronon_battery")
    public static final class Battery {
        @ConfigEntry(id = "capacity")
        @Comment("The amount of fuel that the Battery can store.")
        public static Observable<Integer> capacityBattery = Observable.of(4000);
    }

    public static final class Chronomark {
        @ConfigEntry(id = "max_offset")
        @Comment("The maximum range that you can set the portal position to be")
        public static Observable<Integer> maxOffsetChronomark = Observable.of(5);
    }
}
