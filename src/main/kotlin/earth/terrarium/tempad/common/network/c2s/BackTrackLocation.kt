package earth.terrarium.tempad.common.network.c2s

import com.teamresourceful.bytecodecs.base.ByteCodec
import com.teamresourceful.resourcefullib.common.network.Packet
import com.teamresourceful.resourcefullib.common.network.base.NetworkHandle
import com.teamresourceful.resourcefullib.common.network.base.PacketType
import com.teamresourceful.resourcefullib.common.network.defaults.CodecPacketType
import earth.terrarium.tempad.Tempad.Companion.tempadId
import earth.terrarium.tempad.common.registries.travelHistory
import java.util.*

class BackTrackLocation(val id: UUID): Packet<BackTrackLocation> {
    companion object {
        val TYPE = CodecPacketType.Server.create(
            "back_track_location".tempadId,
            ByteCodec.UUID.map(::BackTrackLocation, BackTrackLocation::id),
            NetworkHandle.handle { packet, player ->
                player.travelHistory.backtrackTo(player, packet.id)
            }
        )
    }

    override fun type(): PacketType<BackTrackLocation> = TYPE
}