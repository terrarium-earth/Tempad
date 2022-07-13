package me.codexadrian.tempad;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import me.codexadrian.tempad.data.tempad_options.*;

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

    @SerializedName("kangTimedoorUsageType")
    private String heWhoRemainsTempadOption = "UNLIMITED";

    @SerializedName("configurationOptionsForTempad")
    private TempadOptionConfig tempadOptions = new TempadOptionConfig(180, 40, 5,4000000, 100000);

    @SerializedName("configurationOptionsForHeWhoRemainsTempad")
    private TempadOptionConfig heWhoRemainsOptions = new TempadOptionConfig(60, 10, 3, 10000000, 10000);

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

    public int getDistanceFromPlayer() {
        return distanceFromPlayer;
    }

    public int getTimedoorAddWaitTime() {
        return timedoorAddWaitTime;
    }

    public int getTimedoorWait() {
        return timedoorWait;
    }

    public TempadOption getTempadOption() {
        return switch (tempadOption) {
            case "ENERGY" -> EnergyOption.NORMAL_INSTANCE;
            case "ITEM" -> ItemOption.NORMAL_INSTANCE;
            case "UNLIMITED" -> UnlimitedOption.NORMAL_INSTANCE;
            case "EXP_POINTS" -> ExperiencePointsOption.NORMAL_INSTANCE;
            case "EXP_LEVELS" -> ExperienceLevelOption.NORMAL_INSTANCE;
            default -> TimerOption.NORMAL_INSTANCE;
        };
    }

    public TempadOption getHeWhoRemainsOption() {
        return switch (heWhoRemainsTempadOption) {
            case "ENERGY" -> EnergyOption.ADVANCED_INSTANCE;
            case "ITEM" -> ItemOption.ADVANCED_INSTANCE;
            case "UNLIMITED" -> UnlimitedOption.ADVANCED_INSTANCE;
            case "EXP_POINTS" -> ExperiencePointsOption.ADVANCED_INSTANCE;
            case "EXP_LEVELS" -> ExperienceLevelOption.ADVANCED_INSTANCE;
            default -> TimerOption.ADVANCED_INSTANCE;
        };
    }

    public TempadOptionConfig getTempadOptions() {
        return tempadOptions;
    }

    public TempadOptionConfig getHeWhoRemainsOptions() {
        return heWhoRemainsOptions;
    }

    public static class TempadOptionConfig {
        @SerializedName("timedoor_cooldown_if_usage_type_is_set_to_TIMER")
        private int cooldownTime;

        @SerializedName("timedoor_experience_cost_if_usage_type_is_set_to_EXP_POINTS")
        private int expCost;

        @SerializedName("timedoor_experience_level_cost_if_usage_type_is_set_to_EXP_LEVELs")
        private int expLevelCost;

        @SerializedName("timedoor_energy_capacity_if_usage_type_is_set_to_ENERGY")
        private int energyCapacity;

        @SerializedName("timedoor_energy_cost_if_usage_type_is_set_to_ENERGY")
        private int energyCost;

        public TempadOptionConfig(int cooldownTime, int tempadExperienceCost, int tempadExperienceLevelCost, int tempadEnergyCapacity, int tempadEnergyCost) {
            this.cooldownTime = cooldownTime;
            this.expCost = tempadExperienceCost;
            this.expLevelCost = tempadExperienceLevelCost;
            this.energyCapacity = tempadEnergyCapacity;
            this.energyCost = tempadEnergyCost;
        }

        public int getCooldownTime() {
            return cooldownTime;
        }

        public int getExperienceCost() {
            return expCost;
        }

        public int getExperienceLevelCost() {
            return expLevelCost;
        }

        public int getEnergyCapacity() {
            return energyCapacity;
        }

        public int getEnergyCost() {
            return energyCost;
        }
    }
}
