package me.codexadrian.tempad.forge;

import me.codexadrian.tempad.Constants;
import me.codexadrian.tempad.Tempad;
import me.codexadrian.tempad.utils.ConfigUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public class TempadImpl {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, Constants.MODID);

    public static Supplier<SoundEvent> registerSound(String name) {
        return SOUND_EVENTS.register(name, () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(Constants.MODID, name)));
    }
}
