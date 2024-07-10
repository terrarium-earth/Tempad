package earth.terrarium.tempad.common.network.c2s

import com.teamresourceful.bytecodecs.base.ByteCodec
import com.teamresourceful.bytecodecs.base.`object`.ObjectByteCodec
import com.teamresourceful.resourcefullib.common.color.Color
import com.teamresourceful.resourcefullib.common.network.Packet
import com.teamresourceful.resourcefullib.common.network.base.NetworkHandle
import com.teamresourceful.resourcefullib.common.network.defaults.CodecPacketType
import earth.terrarium.tempad.Tempad.Companion.tempadId
import earth.terrarium.tempad.api.locations.LocationData
import earth.terrarium.tempad.common.items.TempadItem
import earth.terrarium.tempad.common.registries.locationData
import earth.terrarium.tempad.common.utils.COLOR_BYTE_CODEC

data class CreateLocationPacket(val name: String, val color: Color) : Packet<CreateLocationPacket> {
    companion object {
        val TYPE = CodecPacketType.Server.create(
            "create_location".tempadId,
            ObjectByteCodec.create(
                ByteCodec.STRING.fieldOf { it.name },
                COLOR_BYTE_CODEC.fieldOf { it.color },
                ::CreateLocationPacket
            ),
            NetworkHandle.handle { message, player ->
                if ({ it.item is TempadItem } !in player.inventory) return@handle
                player.locationData += LocationData(
                    message.name,
                    player.position(),
                    player.level().dimension(),
                    player.yRot,
                    message.color
                )
            }
        )
    }

    override fun type() = TYPE
}
