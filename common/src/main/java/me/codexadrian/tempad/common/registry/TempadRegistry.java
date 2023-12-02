package me.codexadrian.tempad.common.registry;

import com.teamresourceful.resourcefullib.common.registry.RegistryEntry;
import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistries;
import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistry;
import me.codexadrian.tempad.common.Tempad;
import me.codexadrian.tempad.common.entity.TimedoorEntity;
import me.codexadrian.tempad.common.items.AdvancedTempadItem;
import me.codexadrian.tempad.common.items.LocationCard;
import me.codexadrian.tempad.common.items.TempadItem;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.Item;

public class TempadRegistry {
    public static final ResourcefulRegistry<Item> ITEMS = ResourcefulRegistries.create(BuiltInRegistries.ITEM, Tempad.MODID);
    public static final ResourcefulRegistry<EntityType<?>> ENTITIES = ResourcefulRegistries.create(BuiltInRegistries.ENTITY_TYPE, Tempad.MODID);

    //items
    public static final RegistryEntry<Item> TEMPAD = ITEMS.register("tempad", () -> new TempadItem(new Item.Properties().stacksTo(1)));
    public static final RegistryEntry<Item> CREATIVE_TEMPAD = ITEMS.register("he_who_remains_tempad", () -> new AdvancedTempadItem(new Item.Properties().stacksTo(1)));
    public static final RegistryEntry<Item> LOCATION_CARD = ITEMS.register("location_card", () -> new LocationCard(new Item.Properties().stacksTo(1)));

    //the one entity in this mod
    public static final RegistryEntry<EntityType<TimedoorEntity>> TIMEDOOR_ENTITY = ENTITIES.register("timedoor", () -> EntityType.Builder.of(TimedoorEntity::new, MobCategory.MISC).sized(0.4F, 2.3F).noSave().build("timedoor"));
}
