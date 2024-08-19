package earth.terrarium.tempad.common.registries

import com.teamresourceful.bytecodecs.base.ByteCodec
import com.teamresourceful.resourcefullib.common.bytecodecs.ExtraByteCodecs
import com.teamresourceful.resourcefullib.common.bytecodecs.StreamCodecByteCodec
import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistries
import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistry
import com.teamresourceful.resourcefullibkt.common.getValue
import earth.terrarium.tempad.Tempad
import earth.terrarium.tempad.api.sizing.TimedoorSizing
import earth.terrarium.tempad.common.entity.TimedoorEntity
import earth.terrarium.tempad.common.utils.COLOR_BYTE_CODEC
import earth.terrarium.tempad.common.utils.VEC3_BYTE_CODEC
import earth.terrarium.tempad.common.utils.entityType
import earth.terrarium.tempad.common.utils.register
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.syncher.EntityDataSerializer
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.MobCategory
import net.neoforged.neoforge.registries.NeoForgeRegistries

object ModEntities {
    val serializers: ResourcefulRegistry<EntityDataSerializer<*>> = ResourcefulRegistries.create(NeoForgeRegistries.ENTITY_DATA_SERIALIZERS, Tempad.MOD_ID)
    val entities: ResourcefulRegistry<EntityType<*>> = ResourcefulRegistries.create(BuiltInRegistries.ENTITY_TYPE, Tempad.MOD_ID)

    val colorSerializer by serializers.register("color") { createSerializer(COLOR_BYTE_CODEC) }
    val vec3Serializer by serializers.register("vec3") { createSerializer(VEC3_BYTE_CODEC) }
    val dimensionKeySerializer by serializers.register("dimension_key") { createSerializer(ExtraByteCodecs.DIMENSION) }
    val sizingSerializer by serializers.register("sizing") { createSerializer(TimedoorSizing.codec) }

    private fun <T> createSerializer(codec: ByteCodec<T>): EntityDataSerializer<T> {
        return EntityDataSerializer.forValueType(StreamCodecByteCodec.to(codec))
    }

    val TIMEDOOR_ENTITY by entities.register("timedoor",
        entityType(::TimedoorEntity, MobCategory.MISC) {
            sized(1.4F, 2.3F)
            noSave()
        }
    )
}