package me.codexadrian.tempad.platform.services;

import me.codexadrian.tempad.entity.TimedoorEntity;
import me.codexadrian.tempad.items.TempadItem;
import net.minecraft.world.entity.EntityType;

public interface IRegistryHelper {
    TempadItem getItem();

    TempadItem getCreativeItem();

    EntityType<TimedoorEntity> getTimedoor();
}
