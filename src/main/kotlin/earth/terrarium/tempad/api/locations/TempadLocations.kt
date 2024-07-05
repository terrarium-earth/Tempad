package earth.terrarium.tempad.api.locations

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import com.teamresourceful.bytecodecs.base.ByteCodec
import com.teamresourceful.bytecodecs.base.`object`.ObjectByteCodec
import com.teamresourceful.resourcefullib.common.bytecodecs.ExtraByteCodecs
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.player.Player
import java.util.UUID

@JvmRecord
data class ProviderSettings(val id: ResourceLocation, val exportable: Boolean = true, val downloadable: Boolean = true, val deletable: Boolean = true) {
    companion object {
        val CODEC: Codec<ProviderSettings> = RecordCodecBuilder.create {
            it.group(
                ResourceLocation.CODEC.fieldOf("id").forGetter { it.id },
                Codec.BOOL.optionalFieldOf("exportable", true).forGetter { it.exportable },
                Codec.BOOL.optionalFieldOf("downloadable", true).forGetter { it.downloadable },
                Codec.BOOL.optionalFieldOf("deletable", true).forGetter { it.deletable }
            ).apply(it, ::ProviderSettings)
        }

        val BYTE_CODEC = ObjectByteCodec.create(
            ExtraByteCodecs.RESOURCE_LOCATION.fieldOf { it.id },
            ByteCodec.BOOLEAN.fieldOf { it.exportable },
            ByteCodec.BOOLEAN.fieldOf { it.downloadable },
            ByteCodec.BOOLEAN.fieldOf { it.deletable },
            ::ProviderSettings
        )
    }
}

interface LocationHandler {
    val locations: Map<UUID, LocationData>
    operator fun minusAssign(locationId: UUID)
}

typealias LocationProvider = (Player) -> LocationHandler

object TempadLocations {
    private val registry: MutableMap<ProviderSettings, LocationProvider> = mutableMapOf()

    val providers: Set<ProviderSettings>
        get() = registry.keys

    @JvmStatic
    @JvmName("register")
    operator fun set(settings: ProviderSettings, provider: LocationProvider) {
        registry[settings] = provider
    }

    @JvmStatic
    operator fun get(settings: ProviderSettings): LocationProvider? = registry[settings]

    @JvmStatic
    operator fun get(id: ResourceLocation): LocationProvider? = registry.keys.find { it.id == id }?.let { registry[it] }

    @JvmStatic
    operator fun get(player: Player): Map<ProviderSettings, Map<UUID, LocationData>> = registry.mapValues { it.value(player).locations }
}