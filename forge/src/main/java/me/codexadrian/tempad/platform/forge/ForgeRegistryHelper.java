package me.codexadrian.tempad.platform.forge;

import me.codexadrian.tempad.forge.ForgeTempad;
import me.codexadrian.tempad.entity.TimedoorEntity;
import me.codexadrian.tempad.platform.services.IRegistryHelper;
import me.codexadrian.tempad.items.TempadItem;
import net.minecraft.world.entity.EntityType;

public class ForgeRegistryHelper implements IRegistryHelper {
    @Override
    public TempadItem getItem() {
        return ForgeTempad.TEMPAD.get();
    }

    @Override
    public TempadItem getCreativeItem() {
        return ForgeTempad.CREATIVE_TEMPAD.get();
    }

    @Override
    public EntityType<TimedoorEntity> getTimedoor() {
        return ForgeTempad.TIMEDOOR.get();
    }
}
