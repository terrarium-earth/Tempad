package me.codexadrian.tempad;

import me.codexadrian.tempad.entity.TimedoorEntity;
import me.codexadrian.tempad.network.NetworkHandler;
import me.codexadrian.tempad.tempad.TempadItem;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.Optional;

import static me.codexadrian.tempad.Constants.MODID;

@Mod(MODID)
public class ForgeTempad {
    public static Capability<ColorData> INSTANCE = CapabilityManager.get(new CapabilityToken<>(){});
    public static final ResourceLocation CAPABILITY_KEY = new ResourceLocation(MODID, "tempad_color");
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITIES, MODID);
    public static final RegistryObject<EntityType<TimedoorEntity>> TIMEDOOR = ENTITIES.register("timedoor", () -> EntityType.Builder.of(TimedoorEntity::new, MobCategory.MISC).sized(.4F, 2.3F).build("timedoor"));
    public static final RegistryObject<TempadItem> TEMPAD = ITEMS.register("tempad", ()-> new TempadItem(new Item.Properties().stacksTo(1).tab(CreativeModeTab.TAB_TOOLS)));

    public ForgeTempad() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        ITEMS.register(bus);
        ENTITIES.register(bus);
        NetworkHandler.register();
        bus.addListener(ForgeTempad::onClientSetup);
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.register(ColorData.class);
    }

    @SubscribeEvent
    public void onAttachCapabilityToPlayer(AttachCapabilitiesEvent<Entity> event) {
        if(event.getObject() instanceof Player) event.addCapability(CAPABILITY_KEY, new ColorDataProvider());
    }

    @SubscribeEvent
    public void onPlayerClone(PlayerEvent.Clone event) {
        if(event.isWasDeath()) {
            event.getOriginal().reviveCaps();
            Optional<ColorData> oldColorData = event.getOriginal().getCapability(INSTANCE).resolve();
            Optional<ColorData> newColorData = event.getPlayer().getCapability(INSTANCE).resolve();
            event.getOriginal().invalidateCaps();
            if(oldColorData.isPresent() && newColorData.isPresent()) {
                ColorData oldColor = oldColorData.get();
                ColorData newColor = newColorData.get();
                newColor.color = oldColor.color;
            }
        }
    }

    public static void onClientSetup(FMLClientSetupEvent event) {
        ForgeTempadClient.init();
    }
}
