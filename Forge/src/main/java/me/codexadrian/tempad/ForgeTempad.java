package me.codexadrian.tempad;

import me.codexadrian.tempad.entity.TimedoorEntity;
import me.codexadrian.tempad.network.NetworkHandler;
import me.codexadrian.tempad.tempad.TempadItem;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static me.codexadrian.tempad.Constants.MODID;

@Mod(MODID)
public class ForgeTempad {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITIES, MODID);
    public static final RegistryObject<EntityType<TimedoorEntity>> TIMEDOOR = ENTITIES.register("timedoor", () -> EntityType.Builder.of(TimedoorEntity::new, MobCategory.MISC).sized(.4F, 2.3F).noSave().build("timedoor"));
    public static final RegistryObject<TempadItem> TEMPAD = ITEMS.register("tempad", () -> new TempadItem(Tempad.getTempadConfig().getTempadOption(), new Item.Properties().stacksTo(1).tab(CreativeModeTab.TAB_TOOLS).rarity(Rarity.EPIC)));
    public static final RegistryObject<TempadItem> CREATIVE_TEMPAD = ITEMS.register("he_who_remains_tempad", () -> new TempadItem(Tempad.getTempadConfig().getHeWhoRemainsOption(), new Item.Properties().stacksTo(1).tab(CreativeModeTab.TAB_TOOLS).rarity(Rarity.EPIC)));

    public ForgeTempad() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        ITEMS.register(bus);
        ENTITIES.register(bus);
        NetworkHandler.register();
        bus.addListener(ForgeTempad::onClientSetup);
        MinecraftForge.EVENT_BUS.register(this);
        DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> ForgeTempadClient::registerBlurReloader);
        Tempad.init();
    }

    public static void onClientSetup(FMLClientSetupEvent event) {
        ForgeTempadClient.init();
    }
}
