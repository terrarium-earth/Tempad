package me.codexadrian.tempad.fabric;

import me.codexadrian.tempad.Constants;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

import java.util.function.Supplier;

public class TempadImpl {
    public static Supplier<SoundEvent> registerSound(String name) {
        var sound = Registry.register(BuiltInRegistries.SOUND_EVENT, new ResourceLocation(Constants.MODID, name), SoundEvent.createVariableRangeEvent(new ResourceLocation(Constants.MODID, name)));
        return () -> sound;
    }
}
