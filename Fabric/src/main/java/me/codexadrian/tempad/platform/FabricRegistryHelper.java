package me.codexadrian.tempad.platform;

import me.codexadrian.tempad.FabricTempad;
import me.codexadrian.tempad.entity.TimedoorEntity;
import me.codexadrian.tempad.platform.services.IRegistryHelper;
import me.codexadrian.tempad.tempad.TempadItem;
import net.minecraft.world.entity.EntityType;

public class FabricRegistryHelper implements IRegistryHelper {
    @Override
    public TempadItem getItem() {
        return FabricTempad.TEMPAD;
    }

    @Override
    public EntityType<TimedoorEntity> getTimedoor() {
        return FabricTempad.TIMEDOOR_ENTITY_ENTITY_TYPE;
    }
}
