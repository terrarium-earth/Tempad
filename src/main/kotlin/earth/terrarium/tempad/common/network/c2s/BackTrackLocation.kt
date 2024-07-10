package earth.terrarium.tempad.common.network.c2s

import com.teamresourceful.resourcefullib.common.network.Packet
import com.teamresourceful.resourcefullib.common.network.base.NetworkHandle
import com.teamresourceful.resourcefullib.common.network.base.PacketType
import com.teamresourceful.resourcefullib.common.network.defaults.CodecPacketType
import earth.terrarium.tempad.Tempad.Companion.tempadId
import earth.terrarium.tempad.common.registries.travelHistory
import earth.terrarium.tempad.common.utils.DATE_BYTE_CODEC
import java.util.*

class BackTrackLocation(val time: Date): Packet<BackTrackLocation> {
    companion object {
        val TYPE = CodecPacketType.Server.create(
            "back_track_location".tempadId,
            DATE_BYTE_CODEC.map(::BackTrackLocation, BackTrackLocation::time),
            NetworkHandle.handle { packet, player ->
                player.travelHistory.backtrackTo(player, packet.time)
            }
        )
    }

    override fun type(): PacketType<BackTrackLocation> = TYPE
}