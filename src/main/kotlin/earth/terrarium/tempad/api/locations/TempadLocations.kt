package earth.terrarium.tempad.api.locations

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import com.teamresourceful.bytecodecs.base.ByteCodec
import com.teamresourceful.bytecodecs.base.`object`.ObjectByteCodec
import com.teamresourceful.resourcefullib.common.bytecodecs.ExtraByteCodecs
import earth.terrarium.tempad.api.context.ItemContext
import earth.terrarium.tempad.api.context.SyncableContext
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.player.Player
import java.util.*

interface LocationHandler {
    val locations: Map<UUID, NamedGlobalPos>

    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("delete")
    operator fun minusAssign(locationId: UUID)

    operator fun get(locationId: UUID): NamedGlobalPos? = locations[locationId]
}

typealias LocationProvider = (Player, SyncableContext<*>) -> LocationHandler

object TempadLocations {
    val registry: Map<ResourceLocation, LocationProvider>
        field = mutableMapOf()

    @JvmStatic
    @JvmName("register")
    operator fun set(settings: ResourceLocation, provider: LocationProvider) {
        registry[settings] = provider
    }

    @JvmStatic
    operator fun get(id: ResourceLocation): LocationProvider? = registry[id]

    @JvmStatic
    operator fun get(player: Player, ctx: SyncableContext<*>, id: ResourceLocation): LocationHandler? =
        registry[id]?.let { it(player, ctx) }

    @JvmStatic
    operator fun get(player: Player, ctx: SyncableContext<*>): Map<ResourceLocation, Map<UUID, NamedGlobalPos>> =
        registry.mapValues { it.value(player, ctx).locations }
}