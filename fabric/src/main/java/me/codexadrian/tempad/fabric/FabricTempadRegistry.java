package me.codexadrian.tempad.fabric;

import me.codexadrian.tempad.TempadType;
import me.codexadrian.tempad.entity.TimedoorEntity;
import me.codexadrian.tempad.tempad.TempadItem;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

public class FabricTempadRegistry {
    public static final EntityType<TimedoorEntity> TIMEDOOR_ENTITY_ENTITY_TYPE = FabricEntityTypeBuilder.create(MobCategory.MISC, TimedoorEntity::new).dimensions(EntityDimensions.scalable(.4F, 2.3F)).disableSaving().build();
    public static final TempadItem TEMPAD = new TempadItem(TempadType.NORMAL, new Item.Properties().rarity(Rarity.EPIC));
    public static final TempadItem CREATIVE_TEMPAD = new TempadItem(TempadType.HE_WHO_REMAINS, new Item.Properties().rarity(Rarity.EPIC));
}
