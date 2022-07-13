package me.codexadrian.tempad.fabric;

import me.codexadrian.tempad.Constants;
import me.codexadrian.tempad.Tempad;
import me.codexadrian.tempad.network.NetworkHandler;
import net.fabricmc.api.ModInitializer;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;

public class FabricTempad implements ModInitializer {
    @Override
    public void onInitialize() {
        Tempad.init();
        Registry.register(Registry.ENTITY_TYPE, new ResourceLocation(Constants.MODID, "timedoor"), FabricTempadRegistry.TIMEDOOR_ENTITY_ENTITY_TYPE);
        Registry.register(Registry.ITEM, new ResourceLocation(Constants.MODID, "tempad"), FabricTempadRegistry.TEMPAD);
        Registry.register(Registry.ITEM, new ResourceLocation(Constants.MODID, "he_who_remains_tempad"), FabricTempadRegistry.CREATIVE_TEMPAD);
        NetworkHandler.register();
    }
}
