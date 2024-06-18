package earth.terrarium.tempad.common.registries

import com.teamresourceful.bytecodecs.base.ByteCodec
import com.teamresourceful.resourcefullib.common.bytecodecs.StreamCodecByteCodec
import com.teamresourceful.resourcefullibkt.common.createRegistry
import com.teamresourceful.resourcefullibkt.common.getValue
import earth.terrarium.tempad.Tempad
import earth.terrarium.tempad.common.entity.TimedoorEntity
import earth.terrarium.tempad.common.utils.COLOR_BYTE_CODEC
import earth.terrarium.tempad.common.utils.entityType
import earth.terrarium.tempad.common.utils.register
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.syncher.EntityDataSerializer
import net.minecraft.world.entity.MobCategory
import net.neoforged.neoforge.registries.NeoForgeRegistries

object ModEntities {
    val DATA_SERIALIZERS = NeoForgeRegistries.ENTITY_DATA_SERIALIZERS.createRegistry(Tempad.MOD_ID)
    val ENTITIES = BuiltInRegistries.ENTITY_TYPE.createRegistry(Tempad.MOD_ID)

    val COLOR_SERIALIZER by DATA_SERIALIZERS.register("color") { createSerializer(COLOR_BYTE_CODEC) }

    private fun <T> createSerializer(codec: ByteCodec<T>): EntityDataSerializer<T> {
        return EntityDataSerializer.forValueType(StreamCodecByteCodec.to(codec))
    }

    val TIMEDOOR_ENTITY by ENTITIES.register("timedoor",
        entityType(::TimedoorEntity, MobCategory.MISC) {

        }
    )
}