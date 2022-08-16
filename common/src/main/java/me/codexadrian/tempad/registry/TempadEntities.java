package me.codexadrian.tempad.registry;

import dev.architectury.injectables.annotations.ExpectPlatform;
import me.codexadrian.tempad.entity.TimedoorEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

import java.util.function.Supplier;

public class TempadEntities {
    public static final Supplier<EntityType<TimedoorEntity>> TIMEDOOR_ENTITY_TYPE = registerEntity("timedoor", TimedoorEntity::new, MobCategory.MISC, .4F, 2.3F, true);
    public static final Supplier<EntityType<TimedoorEntity>> BLOCK_TIMEDOOR_ENTITY_TYPE = registerEntity("block_timedoor", TimedoorEntity::new, MobCategory.MISC, .4F, 2.3F, false);

    @ExpectPlatform
    public static <T extends Entity> Supplier<EntityType<T>> registerEntity(String name, EntityType.EntityFactory<T> factory, MobCategory group, float width, float height, boolean disablesSaving) {
        throw new AssertionError();
    }

    public static void register() {

    }
}
