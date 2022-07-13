package me.codexadrian.tempad;

import me.codexadrian.tempad.entity.TimedoorEntity;
import me.codexadrian.tempad.network.NetworkHandler;
import me.codexadrian.tempad.tempad.TempadItem;
import me.codexadrian.tempad.tempad.TempadOfHeWhoRemainsItem;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

public class FabricTempad implements ModInitializer {
    public static final EntityType<TimedoorEntity> TIMEDOOR_ENTITY_ENTITY_TYPE = FabricEntityTypeBuilder.create(MobCategory.MISC, TimedoorEntity::new).dimensions(EntityDimensions.scalable(.4F, 2.3F)).disableSaving().build();
    public static final TempadItem TEMPAD = new TempadItem(Tempad.getTempadConfig().getTempadOption(), new Item.Properties().stacksTo(1).tab(CreativeModeTab.TAB_TOOLS).rarity(Rarity.EPIC));
    public static final TempadItem CREATIVE_TEMPAD = new TempadItem(Tempad.getTempadConfig().getHeWhoRemainsOption(), new Item.Properties().stacksTo(1).tab(CreativeModeTab.TAB_TOOLS).rarity(Rarity.EPIC));
    @Override
    public void onInitialize() {
        Registry.register(Registry.ENTITY_TYPE, new ResourceLocation(Constants.MODID, "timedoor"), TIMEDOOR_ENTITY_ENTITY_TYPE);
        Registry.register(Registry.ITEM, new ResourceLocation(Constants.MODID, "tempad"), TEMPAD);
        Registry.register(Registry.ITEM, new ResourceLocation(Constants.MODID, "he_who_remains_tempad"), CREATIVE_TEMPAD);
        NetworkHandler.register();
        Tempad.init();
    }
}
