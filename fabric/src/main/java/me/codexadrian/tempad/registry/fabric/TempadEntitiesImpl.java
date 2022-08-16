package me.codexadrian.tempad.registry.fabric;

import me.codexadrian.tempad.Constants;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

import java.util.function.Supplier;

public class TempadEntitiesImpl {
    public static <T extends Entity> Supplier<EntityType<T>> registerEntity(String name, EntityType.EntityFactory<T> factory, MobCategory group, float width, float height, boolean disablesSaving) {
        EntityType<T> entity;
        if(disablesSaving) {
            entity = FabricEntityTypeBuilder.create(group, factory).dimensions(EntityDimensions.scalable(width, height)).disableSaving().build();
        } else {
            entity = FabricEntityTypeBuilder.create(group, factory).dimensions(EntityDimensions.scalable(width, height)).build();
        }
        var register = Registry.register(Registry.ENTITY_TYPE, new ResourceLocation(Constants.MODID, name), entity);
        return () -> register;
    }
}
