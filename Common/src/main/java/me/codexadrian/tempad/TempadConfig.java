package me.codexadrian.tempad;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import me.codexadrian.tempad.data.tempad_options.*;
import me.codexadrian.tempad.platform.Services;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

public class TempadConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();


    @SerializedName("timedoorPlacementOffsetInBlocks")
    private int distanceFromPlayer = 3;

    @SerializedName("timedoorCloseWaitingTimeInTicks")
    private int timedoorWait = 60;

    @SerializedName("onEntityThroughDoorAdditionalTimeInTicks")
    private int timedoorAddWaitTime = 40;
    @SerializedName("timedoorUsageType")
    private String tempadOption = "TIMER";

    @SerializedName("timedoorUsageType")
    private String heWhoRemainsTempadOption = "UNLIMITED";

    public int getDistanceFromPlayer() {
        return distanceFromPlayer;
    }

    public int getTimedoorAddWaitTime() {
        return timedoorAddWaitTime;
    }

    public int getTimedoorWait() {
        return timedoorWait;
    }

    public static TempadConfig loadConfig(Path configFolder) throws IOException {
        Path configPath = configFolder.resolve(Constants.MODID + ".json");

        if (!Files.exists(configPath)) {
            TempadConfig config = new TempadConfig();
            try (Writer writer = new FileWriter(configPath.toFile())) {
                GSON.toJson(config, writer);
            }
            Constants.LOG.info("Created config file for mod " + Constants.MODID);

            return config;
        }

        return GSON.fromJson(new InputStreamReader(Files.newInputStream(configPath)), TempadConfig.class);
    }

    @SerializedName("configurationOptionsForTempad")
    private TempadOptionConfig tempadOptions = new TempadOptionConfig(180, 40, 4000000, 100000);

    @SerializedName("configurationOptionsForHeWhoRemainsTempad")
    private TempadOptionConfig heWhoRemainsOptions = new TempadOptionConfig(60, 10, 10000000, 10000);

    public TempadOption getTempadOption() {
        return switch (tempadOption) {
            case "ENERGY" -> EnergyOption.NORMAL_INSTANCE;
            case "ITEM" -> ItemOption.NORMAL_INSTANCE;
            case "UNLIMITED" -> UnlimitedOption.NORMAL_INSTANCE;
            case "EXPERIENCE" -> ExperienceOption.NORMAL_INSTANCE;
            default -> TimerOption.NORMAL_INSTANCE;
        };
    }

    public TempadOption getHeWhoRemainsOption() {
        return switch (heWhoRemainsTempadOption) {
            case "ENERGY" -> EnergyOption.ADVANCED_INSTANCE;
            case "ITEM" -> ItemOption.ADVANCED_INSTANCE;
            case "UNLIMITED" -> UnlimitedOption.ADVANCED_INSTANCE;
            case "EXPERIENCE" -> ExperienceOption.ADVANCED_INSTANCE;
            default -> TimerOption.ADVANCED_INSTANCE;
        };
    }

    public TempadOptionConfig getTempadOptions() {
        return tempadOptions;
    }

    public TempadOptionConfig getHeWhoRemainsOptions() {
        return heWhoRemainsOptions;
    }

    public class TempadOptionConfig {
        @SerializedName("timedoor_cooldown_if_usage_type_is_set_to_TIMER")
        private final int cooldownTime;

        @SerializedName("timedoor_experience_cost_if_usage_type_is_set_to_EXPERIENCE")
        private final int tempadExperienceCost;

        @SerializedName("timedoor_energy_capacity_if_usage_type_is_set_to_ENERGY")
        private final int tempadEnergyCapacity;

        @SerializedName("timedoor_energy_cost_if_usage_type_is_set_to_ENERGY")
        private final int tempadEnergyCost;

        public TempadOptionConfig(int cooldownTime, int tempadExperienceCost, int tempadEnergyCapacity, int tempadEnergyCost) {
            this.cooldownTime = cooldownTime;
            this.tempadExperienceCost = tempadExperienceCost;
            this.tempadEnergyCapacity = tempadEnergyCapacity;
            this.tempadEnergyCost = tempadEnergyCost;
        }

        public int getCooldownTime() {
            return cooldownTime;
        }

        public int getTempadExperienceCost() {
            return tempadExperienceCost;
        }

        public int getTempadEnergyCapacity() {
            return tempadEnergyCapacity;
        }

        public int getTempadEnergyCost() {
            return tempadEnergyCost;
        }
    }
}
