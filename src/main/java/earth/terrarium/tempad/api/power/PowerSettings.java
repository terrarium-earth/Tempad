package earth.terrarium.tempad.api.power;

import net.minecraft.resources.ResourceLocation;

public interface PowerSettings {
    ResourceLocation getOptionId();
    int getFuelCost();
    int getFuelCapacity();
}
