package earth.terrarium.tempad.common.registry;

import com.teamresourceful.resourcefullib.common.registry.RegistryEntry;
import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistries;
import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistry;
import earth.terrarium.tempad.common.Tempad;
import earth.terrarium.tempad.common.entity.TimedoorEntity;
import earth.terrarium.tempad.common.items.AdvancedTempadItem;
import earth.terrarium.tempad.common.items.LocationCard;
import earth.terrarium.tempad.common.items.PrinterItem;
import earth.terrarium.tempad.common.items.TempadItem;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;

public class TempadRegistry {
    public static final ResourcefulRegistry<Item> ITEMS = ResourcefulRegistries.create(BuiltInRegistries.ITEM, Tempad.MODID);
    public static final ResourcefulRegistry<EntityType<?>> ENTITIES = ResourcefulRegistries.create(BuiltInRegistries.ENTITY_TYPE, Tempad.MODID);
    public static final ResourcefulRegistry<CreativeModeTab> ITEM_GROUP = ResourcefulRegistries.create(BuiltInRegistries.CREATIVE_MODE_TAB, Tempad.MODID);


    //items
    public static final RegistryEntry<Item> TEMPAD = ITEMS.register("tempad", () -> new TempadItem(new Item.Properties().stacksTo(1)));
    public static final RegistryEntry<Item> CREATIVE_TEMPAD = ITEMS.register("he_who_remains_tempad", () -> new AdvancedTempadItem(new Item.Properties().stacksTo(1)));
    public static final RegistryEntry<Item> LOCATION_CARD = ITEMS.register("location_card", () -> new LocationCard(new Item.Properties()));
    public static final RegistryEntry<Item> TEMPORAL_SHEILDING = ITEMS.register("temporal_shielding", () -> new Item(new Item.Properties()));

    //blocks
    public static final RegistryEntry<Item> PRINTER_BLOCK = ITEMS.register("printer_block", () -> new PrinterItem(me.codexadrian.tempad.common.registry.TempadBlocks.LOCATION_PRINTER.get(), new Item.Properties()));
    public static final RegistryEntry<Item> WARP_PAD = ITEMS.register("warp_pad", () -> new BlockItem(me.codexadrian.tempad.common.registry.TempadBlocks.WARP_PAD.get(), new Item.Properties()));
    public static final RegistryEntry<Item> LOCATION_TERMINAL = ITEMS.register("location_terminal", () -> new BlockItem(me.codexadrian.tempad.common.registry.TempadBlocks.LOCATION_TERMINAL.get(), new Item.Properties()));

    public static final RegistryEntry<CreativeModeTab> TEMPAD_GROUP = ITEM_GROUP.register("main", () -> new CreativeModeTab.Builder(CreativeModeTab.Row.TOP, 0).title(Component.translatable("category.tempad")).icon(() -> TEMPAD.get().getDefaultInstance()).build());

    //the one entity in this mod
    public static final RegistryEntry<EntityType<TimedoorEntity>> TIMEDOOR_ENTITY = ENTITIES.register("timedoor", () -> EntityType.Builder.of(TimedoorEntity::new, MobCategory.MISC).sized(1.4F, 2.3F).noSave().build("timedoor"));
}
