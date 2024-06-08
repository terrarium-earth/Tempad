package earth.terrarium.tempad.common.compat.botarium;

import earth.terrarium.botarium.common.energy.EnergyApi;
import earth.terrarium.botarium.common.energy.base.BotariumEnergyItem;
import earth.terrarium.botarium.common.energy.base.EnergyContainer;
import earth.terrarium.botarium.common.fluid.FluidApi;
import earth.terrarium.botarium.common.fluid.base.BotariumFluidItem;
import earth.terrarium.botarium.common.fluid.base.ItemFluidContainer;
import earth.terrarium.tempad.api.power.PowerSettings;
import earth.terrarium.tempad.api.power.PowerSettingsApi;
import earth.terrarium.tempad.common.options.impl.EnergyOption;
import earth.terrarium.tempad.common.options.impl.FluidOption;
import earth.terrarium.tempad.common.registry.TempadData;
import earth.terrarium.tempad.common.registry.TempadRegistry;

public class BotariumTempadOptionRegistry {
    public static final BotariumFluidItem<? extends ItemFluidContainer> FLUID_CAP = holder -> {
        PowerSettings settings = PowerSettingsApi.API.get(holder);
        if (settings == null || !settings.getOptionId().equals(FluidOption.ID)) return null;
        var data = TempadData.FLUID.getData(holder);
        data.setCapacity(settings.getFuelCapacity());
        data.setupContainer(holder);
        return data;
    };

    public static final BotariumEnergyItem<? extends EnergyContainer> ENERGY_CAP = holder -> {
        PowerSettings settings = PowerSettingsApi.API.get(holder);
        if (settings == null || !settings.getOptionId().equals(EnergyOption.ID)) return null;
        var data = TempadData.ENERGY.getData(holder);
        data.setupContainer(settings);
        return data;
    };

    public static void postInit() {
        EnergyApi.registerEnergyItem(TempadRegistry.TEMPAD, ENERGY_CAP);
        FluidApi.registerFluidItem(TempadRegistry.TEMPAD, FLUID_CAP);
        EnergyApi.registerEnergyItem(TempadRegistry.CREATIVE_TEMPAD, ENERGY_CAP);
        FluidApi.registerFluidItem(TempadRegistry.CREATIVE_TEMPAD, FLUID_CAP);
    }
}
