package earth.terrarium.tempad.common.network.c2s

import com.teamresourceful.bytecodecs.base.ByteCodec
import com.teamresourceful.bytecodecs.base.`object`.ObjectByteCodec
import com.teamresourceful.resourcefullib.common.bytecodecs.ExtraByteCodecs
import com.teamresourceful.resourcefullib.common.network.Packet
import com.teamresourceful.resourcefullib.common.network.base.NetworkHandle
import com.teamresourceful.resourcefullib.common.network.defaults.CodecPacketType
import earth.terrarium.tempad.Tempad.Companion.tempadId
import earth.terrarium.tempad.api.fuel.PlayerContainerContext
import earth.terrarium.tempad.api.locations.TempadLocations
import earth.terrarium.tempad.common.items.TempadItem
import earth.terrarium.tempad.common.utils.ctx
import net.minecraft.resources.ResourceLocation
import java.util.UUID

data class DeleteLocationPacket(val providerId: ResourceLocation, val locationId: UUID, val tempadSlot: Int): Packet<DeleteLocationPacket> {
    companion object {
        val TYPE = CodecPacketType.Server.create(
            "delete_location".tempadId,
            ObjectByteCodec.create(
                ExtraByteCodecs.RESOURCE_LOCATION.fieldOf { it.providerId },
                ByteCodec.UUID.fieldOf { it.locationId },
                ByteCodec.INT.fieldOf { it.tempadSlot },
                ::DeleteLocationPacket
            ),
            NetworkHandle.handle { message, player ->
                if ({ it.item is TempadItem } !in player.inventory) return@handle
                TempadLocations[player.ctx(message.tempadSlot), message.providerId]!! -= message.locationId
            }
        )
    }

    override fun type() = TYPE
}
