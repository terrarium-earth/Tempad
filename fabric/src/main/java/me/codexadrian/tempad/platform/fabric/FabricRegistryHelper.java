package me.codexadrian.tempad.platform.fabric;

import me.codexadrian.tempad.fabric.FabricTempadRegistry;
import me.codexadrian.tempad.entity.TimedoorEntity;
import me.codexadrian.tempad.platform.services.IRegistryHelper;
import me.codexadrian.tempad.tempad.TempadItem;
import net.minecraft.world.entity.EntityType;

public class FabricRegistryHelper implements IRegistryHelper {
    @Override
    public TempadItem getItem() {
        return FabricTempadRegistry.TEMPAD;
    }

    @Override
    public TempadItem getCreativeItem() {
        return FabricTempadRegistry.TEMPAD;
    }

    @Override
    public EntityType<TimedoorEntity> getTimedoor() {
        return FabricTempadRegistry.TIMEDOOR_ENTITY_ENTITY_TYPE;
    }
}
