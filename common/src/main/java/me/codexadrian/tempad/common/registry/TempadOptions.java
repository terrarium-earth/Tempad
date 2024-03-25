package me.codexadrian.tempad.common.registry;

import me.codexadrian.tempad.api.options.FuelOptionsApi;
import me.codexadrian.tempad.common.Tempad;
import me.codexadrian.tempad.common.config.ConfigCache;
import me.codexadrian.tempad.common.options.impl.*;
import net.minecraft.resources.ResourceLocation;

@SuppressWarnings("unchecked")
public class TempadOptions {
    public static void init() {
        FuelOptionsApi.API.register(new ResourceLocation(Tempad.MODID, "unlimited"), new UnlimitedOption());
        FuelOptionsApi.API.register(new ResourceLocation(Tempad.MODID, "experience_level"), new ExperienceLevelOption()); // Needed due to a typo in previous versions
        FuelOptionsApi.API.register(new ResourceLocation(Tempad.MODID, "experience_levels"), new ExperienceLevelOption());
        FuelOptionsApi.API.register(new ResourceLocation(Tempad.MODID, "experience_points"), new ExperiencePointsOption());
        FuelOptionsApi.API.register(new ResourceLocation(Tempad.MODID, "item"), new ItemOption());
        FuelOptionsApi.API.register(new ResourceLocation(Tempad.MODID, "timer"), new TimerOption());

        FuelOptionsApi.API.register(EnergyOption.ID, new EnergyOption());
        FuelOptionsApi.API.register(FluidOption.ID, new FluidOption());

        FuelOptionsApi.API.attachItemOption((stack, context) -> FuelOptionsApi.API.getOption(ResourceLocation.tryParse(ConfigCache.tempadFuelType)), TempadRegistry.TEMPAD);
        FuelOptionsApi.API.attachItemOption((stack, context) -> FuelOptionsApi.API.getOption(ResourceLocation.tryParse(ConfigCache.advancedTempadFuelType)), TempadRegistry.CREATIVE_TEMPAD);
    }
}
