package earth.terrarium.tempad.common.network.c2s

import com.teamresourceful.bytecodecs.base.ByteCodec
import com.teamresourceful.bytecodecs.base.`object`.ObjectByteCodec
import com.teamresourceful.resourcefullib.common.bytecodecs.ExtraByteCodecs
import com.teamresourceful.resourcefullib.common.network.Packet
import com.teamresourceful.resourcefullib.common.network.base.NetworkHandle
import com.teamresourceful.resourcefullib.common.network.base.PacketType
import com.teamresourceful.resourcefullib.common.network.defaults.CodecPacketType
import earth.terrarium.tempad.api.access.ItemAccessAddress
import earth.terrarium.tempad.api.locations.TempadLocations
import earth.terrarium.tempad.common.entity.TimedoorEntity
import earth.terrarium.tempad.tempadId
import net.minecraft.resources.Identifier
import java.util.*

data class OpenTimedoorPacket(val providerId: Identifier, val locationId: UUID, val ctx: ItemAccessAddress<*>) :
    Packet<OpenTimedoorPacket> {
    companion object {
        val type = CodecPacketType.Server.create("open_timedoor".tempadId,
            ObjectByteCodec.create(
                ExtraByteCodecs.IDENTIFIER.fieldOf { it.providerId },
                ByteCodec.UUID.fieldOf { it.locationId },
                ItemAccessAddress.codec.fieldOf { it.ctx },
                ::OpenTimedoorPacket
            ),
            NetworkHandle.handle { message, player ->
                val (provider, id, ctxHolder) = message
                if (TempadLocations[provider] == null) return@handle
                val ctx = message.ctx.getAccess(player)
                val location = TempadLocations[player, ctx, provider]?.let { it[id] }
                location?.let {
                    TimedoorEntity.openTimedoor(player, ctx, provider, id, it)?.let { msg -> player.sendSystemMessage(msg) }
                }
            })
    }

    override fun type(): PacketType<OpenTimedoorPacket> = type
}
