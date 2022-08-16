package me.codexadrian.tempad.registry.forge;

import me.codexadrian.tempad.Constants;
import me.codexadrian.tempad.entity.TimedoorEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public class TempadEntitiesImpl {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, Constants.MODID);

    public static <T extends Entity> Supplier<EntityType<T>> registerEntity(String name, EntityType.EntityFactory<T> factory, MobCategory group, float width, float height, boolean disablesSaving) {
        return ENTITY_TYPES.register(name, () -> {
            if(disablesSaving) return EntityType.Builder.of(factory, group).sized(width, height).noSave().build(name);
            return EntityType.Builder.of(factory, group).sized(width, height).build(name);
        });
    }
}
