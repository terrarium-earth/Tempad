package me.codexadrian.tempad.platform;

import me.codexadrian.tempad.ForgeTempad;
import me.codexadrian.tempad.entity.TimedoorEntity;
import me.codexadrian.tempad.platform.services.IRegistryHelper;
import me.codexadrian.tempad.tempad.TempadItem;
import net.minecraft.world.entity.EntityType;

public class ForgeRegistryHelper implements IRegistryHelper {
    @Override
    public TempadItem getItem() {
        return ForgeTempad.TEMPAD.get();
    }

    @Override
    public EntityType<TimedoorEntity> getTimedoor() {
        return ForgeTempad.TIMEDOOR.get();
    }
}
