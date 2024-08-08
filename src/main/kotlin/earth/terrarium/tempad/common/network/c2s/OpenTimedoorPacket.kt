package earth.terrarium.tempad.common.network.c2s

import com.teamresourceful.bytecodecs.base.ByteCodec
import com.teamresourceful.bytecodecs.base.`object`.ObjectByteCodec
import com.teamresourceful.resourcefullib.common.bytecodecs.ExtraByteCodecs
import com.teamresourceful.resourcefullib.common.network.Packet
import com.teamresourceful.resourcefullib.common.network.base.NetworkHandle
import com.teamresourceful.resourcefullib.common.network.base.PacketType
import com.teamresourceful.resourcefullib.common.network.defaults.CodecPacketType
import earth.terrarium.tempad.tempadId
import earth.terrarium.tempad.api.locations.TempadLocations
import earth.terrarium.tempad.api.context.ContextHolder
import earth.terrarium.tempad.common.entity.TimedoorEntity
import net.minecraft.resources.ResourceLocation
import java.util.*

data class OpenTimedoorPacket(val providerId: ResourceLocation, val locationId: UUID, val ctx: ContextHolder<*>) :
    Packet<OpenTimedoorPacket> {
    companion object {
        val type = CodecPacketType.Server.create(
            "open_timedoor".tempadId,
            ObjectByteCodec.create(
                ExtraByteCodecs.RESOURCE_LOCATION.fieldOf { it.providerId },
                ByteCodec.UUID.fieldOf { it.locationId },
                ContextHolder.codec.fieldOf { it.ctx },
                ::OpenTimedoorPacket
            ),
            NetworkHandle.handle { message, player ->
                if (TempadLocations[message.providerId] == null) return@handle
                val ctx = message.ctx.getCtx(player)
                val location = TempadLocations[player, ctx, message.providerId]?.let { it[message.locationId] }
                location?.let { TimedoorEntity.openTimedoor(player, ctx, it) }
            }
        )
    }

    override fun type(): PacketType<OpenTimedoorPacket> = type
}
