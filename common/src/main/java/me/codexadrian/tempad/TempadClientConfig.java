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

public class TempadClientConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    @SerializedName("color")
    private int color = Constants.ORANGE;

    @SerializedName("colorOptions")
    private int[] colorOptions = {
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

    @SerializedName("timedoorFancy")
    private boolean isFancyTimedoorRendererEnabled = !TempadClient.isIncompatibleModLoaded();

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int[] getColorOptions() {
        return colorOptions;
    }

    public boolean renderBlur() {
        return isFancyTimedoorRendererEnabled;
    }

    public static TempadClientConfig loadConfig(Path configFolder) throws IOException {
        Path configPath = configFolder.resolve(Constants.MODID + "_client.json");

        if (!Files.exists(configPath)) {
            TempadClientConfig config = new TempadClientConfig();
            try (Writer writer = new FileWriter(configPath.toFile())) {
                GSON.toJson(config, writer);
            }
            Constants.LOG.info("Created config file for mod " + Constants.MODID);

            return config;
        }

        return GSON.fromJson(new InputStreamReader(Files.newInputStream(configPath)), TempadClientConfig.class);
    }

    public static void saveConfig(TempadClientConfig config) {
        try {
            Path file = Services.PLATFORM.getConfigDir().resolve(Constants.MODID + "_client.json");

            String json = GSON.toJson(config);
            Files.writeString(file, json);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
