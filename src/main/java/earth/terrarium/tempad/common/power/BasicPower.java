package earth.terrarium.tempad.common.power;

import earth.terrarium.tempad.api.power.PowerSettings;
import earth.terrarium.tempad.common.config.CommonConfigCache;
import net.minecraft.resources.ResourceLocation;

public class BasicPower implements PowerSettings {
    public static final BasicPower INSTANCE = new BasicPower();

    @Override
    public ResourceLocation getOptionId() {
        return ResourceLocation.tryParse(CommonConfigCache.tempadFuelType);
    }

    @Override
    public int getFuelCost() {
        return CommonConfigCache.tempadFuelConsumptionValue;
    }

    @Override
    public int getFuelCapacity() {
        return CommonConfigCache.tempadFuelCapacityValue;
    }
}
