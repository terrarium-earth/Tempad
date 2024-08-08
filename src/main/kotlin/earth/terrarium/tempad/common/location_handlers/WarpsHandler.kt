package earth.terrarium.tempad.common.location_handlers

import com.mojang.serialization.Codec
import com.teamresourceful.resourcefullib.common.utils.files.CodecSavedData
import earth.terrarium.tempad.tempadId
import earth.terrarium.tempad.api.locations.StaticNamedGlobalPos
import earth.terrarium.tempad.api.locations.LocationHandler
import earth.terrarium.tempad.api.locations.ProviderSettings
import earth.terrarium.tempad.api.context.ItemContext
import earth.terrarium.tempad.api.context.SyncableContext
import earth.terrarium.tempad.api.locations.NamedGlobalPos
import net.minecraft.core.UUIDUtil
import net.minecraft.world.entity.player.Player
import java.util.*

class WarpsHandler(player: Player, ctx: SyncableContext<*>): LocationHandler {
    companion object {
        val CODEC: Codec<Map<UUID, NamedGlobalPos>> = Codec.unboundedMap(UUIDUtil.CODEC, NamedGlobalPos.codec).stable()
        val DATA = CodecSavedData.create(CODEC, "tempad/warps").alwaysDirty().defaultValue { emptyMap() }
        val SETTINGS = ProviderSettings("warps".tempadId, downloadable = false)
    }

    override val locations: Map<UUID, StaticNamedGlobalPos>
        get() = emptyMap()

    override fun minusAssign(locationId: UUID) {}
}