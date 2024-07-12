package earth.terrarium.tempad.common.location_handlers

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import com.mojang.serialization.codecs.UnboundedMapCodec
import com.teamresourceful.resourcefullib.common.utils.files.CodecSavedData
import earth.terrarium.tempad.Tempad.Companion.tempadId
import earth.terrarium.tempad.api.fuel.ItemContext
import earth.terrarium.tempad.api.locations.LocationData
import earth.terrarium.tempad.api.locations.LocationHandler
import earth.terrarium.tempad.api.locations.ProviderSettings
import earth.terrarium.tempad.common.data.WarpPadSaveHandler
import net.minecraft.core.UUIDUtil
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.player.Player
import java.util.*

class WarpsHandler(ctx: ItemContext): LocationHandler {
    companion object {
        val CODEC: Codec<Map<UUID, LocationData>> = Codec.unboundedMap(UUIDUtil.CODEC, LocationData.CODEC).stable()
        val DATA = CodecSavedData.create(CODEC, "tempad/warps").alwaysDirty().defaultValue { emptyMap() }
        val SETTINGS = ProviderSettings("warps".tempadId, downloadable = false)
    }

    override val locations: Map<UUID, LocationData>
        get() = emptyMap()

    override fun minusAssign(locationId: UUID) {}
}