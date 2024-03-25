package me.codexadrian.tempad.common.registry;

import earth.terrarium.botarium.common.data.DataManager;
import earth.terrarium.botarium.common.data.DataManagerRegistry;
import me.codexadrian.tempad.common.Tempad;
import me.codexadrian.tempad.common.options.containers.TempadEnergy;
import me.codexadrian.tempad.common.options.containers.TempadFluid;
import me.codexadrian.tempad.common.options.containers.TempadTimer;

public class TempadData {
    public static final DataManagerRegistry DATA = DataManagerRegistry.create(Tempad.MODID);

    public static final DataManager<TempadEnergy> ENERGY = DATA.register("energy", TempadEnergy::new, TempadEnergy.CODEC, false);
    public static final DataManager<TempadFluid> FLUID = DATA.register("fluid", TempadFluid::new, TempadFluid.CODEC, false);
    public static final DataManager<TempadTimer> TIMER = DATA.register("timer", TempadTimer::new, TempadTimer.CODEC, false);
}
