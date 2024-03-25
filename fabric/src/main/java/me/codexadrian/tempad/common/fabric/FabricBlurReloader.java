package me.codexadrian.tempad.common.fabric;

import me.codexadrian.tempad.client.BlurReloader;
import me.codexadrian.tempad.common.Tempad;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.resources.ResourceLocation;

public class FabricBlurReloader extends BlurReloader implements IdentifiableResourceReloadListener {

    @Override
    public ResourceLocation getFabricId() {
        return new ResourceLocation(Tempad.MODID, "timedoorblur");
    }
}
