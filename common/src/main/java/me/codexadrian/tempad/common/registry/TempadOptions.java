package me.codexadrian.tempad.common.registry;

import me.codexadrian.tempad.api.options.FuelOptionsApi;
import me.codexadrian.tempad.common.Tempad;
import me.codexadrian.tempad.common.options.impl.*;
import net.minecraft.resources.ResourceLocation;

public class TempadOptions {
    public static void init() {
        FuelOptionsApi.API.register(new ResourceLocation(Tempad.MODID, "unlimited"), new UnlimitedOption());
        FuelOptionsApi.API.register(new ResourceLocation(Tempad.MODID, "experience_level"), new ExperienceLevelOption()); // Needed due to a typo in previous versions
        FuelOptionsApi.API.register(new ResourceLocation(Tempad.MODID, "experience_levels"), new ExperienceLevelOption());
        FuelOptionsApi.API.register(new ResourceLocation(Tempad.MODID, "experience_points"), new ExperiencePointsOption());
        FuelOptionsApi.API.register(new ResourceLocation(Tempad.MODID, "item"), new ItemOption());
        FuelOptionsApi.API.register(new ResourceLocation(Tempad.MODID, "timer"), new TimerOption());
        FuelOptionsApi.API.register(new ResourceLocation(Tempad.MODID, "energy"), new EnergyOption());
        FuelOptionsApi.API.register(new ResourceLocation(Tempad.MODID, "fluid"), new FluidOption());
    }
}
