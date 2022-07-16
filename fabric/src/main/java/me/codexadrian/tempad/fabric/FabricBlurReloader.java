package me.codexadrian.tempad.fabric;

import me.codexadrian.tempad.BlurReloader;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.resources.ResourceLocation;

import static me.codexadrian.tempad.Constants.MODID;

public class FabricBlurReloader extends BlurReloader implements IdentifiableResourceReloadListener {

    @Override
    public ResourceLocation getFabricId() {
        return new ResourceLocation(MODID, "timedoorblur");
    }
}
