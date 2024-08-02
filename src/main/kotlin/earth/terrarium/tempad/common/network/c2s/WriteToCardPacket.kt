package earth.terrarium.tempad.common.network.c2s

import com.teamresourceful.bytecodecs.base.ByteCodec
import com.teamresourceful.bytecodecs.base.`object`.ObjectByteCodec
import com.teamresourceful.resourcefullib.common.bytecodecs.ExtraByteCodecs
import com.teamresourceful.resourcefullib.common.network.Packet
import com.teamresourceful.resourcefullib.common.network.base.NetworkHandle
import com.teamresourceful.resourcefullib.common.network.base.PacketType
import com.teamresourceful.resourcefullib.common.network.defaults.CodecPacketType
import earth.terrarium.tempad.Tempad.Companion.tempadId
import earth.terrarium.tempad.api.locations.TempadLocations
import earth.terrarium.tempad.api.context.ContextHolder
import earth.terrarium.tempad.common.menu.menuCtx
import net.minecraft.resources.ResourceLocation
import java.util.UUID

class WriteToCardPacket(val providerId: ResourceLocation, val locationId: UUID, val ctx: ContextHolder<*>): Packet<WriteToCardPacket> {
    companion object {
        val type = CodecPacketType.Server.create(
            "write_to_card".tempadId,
            ObjectByteCodec.create(
                ExtraByteCodecs.RESOURCE_LOCATION.fieldOf { it.providerId },
                ByteCodec.UUID.fieldOf { it.locationId },
                ContextHolder.codec.fieldOf { it.ctx },
                ::WriteToCardPacket
            ),
            NetworkHandle.handle { message, player ->
                if (TempadLocations[message.providerId] == null) return@handle
                val ctx = message.ctx.getCtx(player)
                TempadLocations[player, ctx, message.providerId]?.writeToCard(message.locationId, player.menuCtx)
            }
        )
    }

    override fun type(): PacketType<WriteToCardPacket> = type
}