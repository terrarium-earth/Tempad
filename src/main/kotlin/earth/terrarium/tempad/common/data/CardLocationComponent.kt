package earth.terrarium.tempad.common.data

import com.mojang.serialization.codecs.RecordCodecBuilder
import com.teamresourceful.bytecodecs.base.`object`.ObjectByteCodec
import com.teamresourceful.resourcefullib.common.bytecodecs.ExtraByteCodecs
import earth.terrarium.tempad.api.locations.NamedGlobalPos
import earth.terrarium.tempad.api.locations.TempadLocations
import net.minecraft.resources.ResourceLocation

data class CardLocationComponent(val providerId: ResourceLocation, val pos: NamedGlobalPos) {
    companion object {
        val codec = RecordCodecBuilder.create<CardLocationComponent> {
            it.group(
                ResourceLocation.CODEC.fieldOf("providerId").forGetter { it.providerId },
                NamedGlobalPos.codec.fieldOf("pos").forGetter { it.pos }
            ).apply(it, ::CardLocationComponent)
        }

        val byteCodec = ObjectByteCodec.create(
            ExtraByteCodecs.RESOURCE_LOCATION.fieldOf { it.providerId },
            NamedGlobalPos.byteCodec.fieldOf { it.pos },
            ::CardLocationComponent
        )
    }

    val provider get() = TempadLocations[providerId]
}