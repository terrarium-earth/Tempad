package earth.terrarium.tempad.common.registry;

import earth.terrarium.botarium.common.data.DataManager;
import earth.terrarium.botarium.common.data.DataManagerRegistry;
import earth.terrarium.tempad.common.Tempad;
import earth.terrarium.tempad.common.options.containers.TempadEnergy;
import earth.terrarium.tempad.common.options.containers.TempadFluid;
import earth.terrarium.tempad.common.options.containers.TempadTimer;

public class TempadData {
    public static final DataManagerRegistry DATA = DataManagerRegistry.create(Tempad.MODID);

    public static final DataManager<TempadEnergy> ENERGY = DATA.register("energy", TempadEnergy::new, TempadEnergy.CODEC, false);
    public static final DataManager<TempadFluid> FLUID = DATA.register("fluid", TempadFluid::new, TempadFluid.CODEC, false);
    public static final DataManager<TempadTimer> TIMER = DATA.register("timer", TempadTimer::new, TempadTimer.CODEC, false);
}
