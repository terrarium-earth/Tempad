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
data class ProviderSettings(val id: ResourceLocation, val exportable: Boolean = true, val downloadable: Boolean = true) {
    companion object {
        val CODEC: Codec<ProviderSettings> = RecordCodecBuilder.create {
            it.group(
                ResourceLocation.CODEC.fieldOf("id").forGetter { it.id },
                Codec.BOOL.optionalFieldOf("exportable", true).forGetter { it.exportable },
                Codec.BOOL.optionalFieldOf("downloadable", true).forGetter { it.downloadable }
            ).apply(it, ::ProviderSettings)
        }

        val BYTE_CODEC = ObjectByteCodec.create(
            ExtraByteCodecs.RESOURCE_LOCATION.fieldOf { it.id },
            ByteCodec.BOOLEAN.fieldOf { it.exportable },
            ByteCodec.BOOLEAN.fieldOf { it.downloadable },
            ::ProviderSettings
        )
    }
}

interface LocationHandler {
    fun removeLocation(player: Player, locationId: UUID)
    fun getLocations(player: Player): Map<UUID, LocationData>
}

object TempadLocations: Iterable<Map.Entry<ProviderSettings, LocationHandler>> {
    private val providers: MutableMap<ProviderSettings, LocationHandler> = mutableMapOf()

    @JvmStatic
    fun register(settings: ProviderSettings, provider: LocationHandler) {
        providers[settings] = provider
    }

    @JvmStatic
    operator fun get(settings: ProviderSettings): LocationHandler? = providers[settings]

    @JvmStatic
    operator fun get(id: ResourceLocation): LocationHandler? = providers.keys.find { it.id == id }?.let { providers[it] }

    fun getProviders(): Set<ProviderSettings> = providers.keys

    override operator fun iterator(): Iterator<Map.Entry<ProviderSettings, LocationHandler>> = providers.iterator()
}