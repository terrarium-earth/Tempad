package me.codexadrian.tempad;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import me.codexadrian.tempad.platform.Services;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

public class TempadConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    @SerializedName("cooldownTimeInSeconds")
    private int cooldownTime = 180;

    @SerializedName("timedoorPlacementOffsetInBlocks")
    private int distanceFromPlayer = 3;

    @SerializedName("timedoorCloseWaitingTimeInTicks")
    private int timedoorWait = 60;

    @SerializedName("onEntityThroughDoorAdditionalTimeInTicks")
    private int timedoorAddWaitTime = 40;

    public int getCooldownTime() {
        return cooldownTime;
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
}
