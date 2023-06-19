package me.codexadrian.tempad.common.fabric;

import me.codexadrian.tempad.common.Tempad;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

import java.util.function.Supplier;

public class TempadImpl {
    public static Supplier<SoundEvent> registerSound(String name) {
        var sound = Registry.register(BuiltInRegistries.SOUND_EVENT, new ResourceLocation(Tempad.MODID, name), SoundEvent.createVariableRangeEvent(new ResourceLocation(Tempad.MODID, name)));
        return () -> sound;
    }
}
