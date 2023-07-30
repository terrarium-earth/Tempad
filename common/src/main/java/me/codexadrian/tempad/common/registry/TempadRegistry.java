package me.codexadrian.tempad.common.registry;

import com.teamresourceful.resourcefullib.common.registry.RegistryEntry;
import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistries;
import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistry;
import me.codexadrian.tempad.api.options.TempadOptionApi;
import me.codexadrian.tempad.common.Tempad;
import me.codexadrian.tempad.common.config.TempadConfig;
import me.codexadrian.tempad.common.entity.TimedoorEntity;
import me.codexadrian.tempad.common.tempad.TempadItem;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.Item;

public class TempadRegistry {
    public static final ResourcefulRegistry<Item> ITEMS = ResourcefulRegistries.create(BuiltInRegistries.ITEM, Tempad.MODID);
    public static final ResourcefulRegistry<EntityType<?>> ENTITIES = ResourcefulRegistries.create(BuiltInRegistries.ENTITY_TYPE, Tempad.MODID);

    //items
    public static final RegistryEntry<Item> TEMPAD = ITEMS.register("tempad", () -> new TempadItem(new Item.Properties(), TempadOptionApi.getOption(TempadConfig.tempadFuelType), TempadConfig.tempadFuelConsumptionValue, TempadConfig.tempadFuelCapacityValue));
    public static final RegistryEntry<Item> CREATIVE_TEMPAD = ITEMS.register("he_who_remains_tempad", () -> new TempadItem(new Item.Properties(), TempadOptionApi.getOption(TempadConfig.advancedTempadFuelType), TempadConfig.advancedTempadfuelConsumptionValue, TempadConfig.advancedTempadfuelCapacityValue));

    //the one entity in this mod
    public static final RegistryEntry<EntityType<TimedoorEntity>> TIMEDOOR_ENTITY = ENTITIES.register("timedoor", () -> EntityType.Builder.of(TimedoorEntity::new, MobCategory.MISC).sized(0.4F, 2.3F).noSave().build("timedoor"));
}
