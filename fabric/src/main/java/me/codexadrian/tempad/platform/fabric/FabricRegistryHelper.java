package me.codexadrian.tempad.platform.fabric;

import me.codexadrian.tempad.fabric.FabricTempad;
import me.codexadrian.tempad.entity.TimedoorEntity;
import me.codexadrian.tempad.platform.services.IRegistryHelper;
import me.codexadrian.tempad.items.TempadItem;
import net.minecraft.world.entity.EntityType;

public class FabricRegistryHelper implements IRegistryHelper {
    @Override
    public TempadItem getItem() {
        return FabricTempad.TEMPAD;
    }

    @Override
    public TempadItem getCreativeItem() {
        return FabricTempad.CREATIVE_TEMPAD;
    }

    @Override
    public EntityType<TimedoorEntity> getTimedoor() {
        return FabricTempad.TIMEDOOR_ENTITY_ENTITY_TYPE;
    }
}
