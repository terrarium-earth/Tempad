package earth.terrarium.tempad.common.network.c2s

import com.teamresourceful.bytecodecs.base.ByteCodec
import com.teamresourceful.resourcefullib.common.network.Packet
import com.teamresourceful.resourcefullib.common.network.base.NetworkHandle
import com.teamresourceful.resourcefullib.common.network.base.PacketType
import com.teamresourceful.resourcefullib.common.network.defaults.CodecPacketType
import earth.terrarium.tempad.Tempad.Companion.tempadId
import earth.terrarium.tempad.common.data.favoriteLocation
import earth.terrarium.tempad.common.entity.TimedoorEntity

data class OpenFavoritePacket(val slotId: Int): Packet<OpenFavoritePacket> {
    companion object {
        val TYPE = CodecPacketType.Server.create(
            "open_favorite".tempadId,
            ByteCodec.INT.map(
                { OpenFavoritePacket(it) },
                { it.slotId },
            ),
            NetworkHandle.handle { message, player ->
                player.favoriteLocation?.let { TimedoorEntity.openTimedoor(player, message.slotId, it) }
            }
        )
    }

    override fun type(): PacketType<OpenFavoritePacket> = TYPE
}
