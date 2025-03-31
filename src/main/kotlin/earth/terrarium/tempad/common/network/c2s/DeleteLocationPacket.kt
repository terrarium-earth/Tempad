package earth.terrarium.tempad.common.network.c2s

import com.teamresourceful.bytecodecs.base.ByteCodec
import com.teamresourceful.bytecodecs.base.`object`.ObjectByteCodec
import com.teamresourceful.resourcefullib.common.bytecodecs.ExtraByteCodecs
import com.teamresourceful.resourcefullib.common.network.Packet
import com.teamresourceful.resourcefullib.common.network.base.NetworkHandle
import com.teamresourceful.resourcefullib.common.network.defaults.CodecPacketType
import earth.terrarium.tempad.tempadId
import earth.terrarium.tempad.api.locations.TempadLocations
import earth.terrarium.tempad.api.context.ContextHolder
import earth.terrarium.tempad.api.context.SyncableContext
import earth.terrarium.tempad.common.items.TempadItem
import earth.terrarium.tempad.common.registries.ModItems
import net.minecraft.resources.ResourceLocation
import java.util.UUID

data class DeleteLocationPacket(val ctx: ContextHolder<*>, val providerId: ResourceLocation, val locationId: UUID): Packet<DeleteLocationPacket> {
    companion object {
        val type = CodecPacketType.Server.create(
            "delete_location".tempadId,
            ObjectByteCodec.create(
                ContextHolder.codec.fieldOf(DeleteLocationPacket::ctx),
                ExtraByteCodecs.RESOURCE_LOCATION.fieldOf { it.providerId },
                ByteCodec.UUID.fieldOf { it.locationId },
                ::DeleteLocationPacket
            ),
            NetworkHandle.handle { message, player ->
                if ({ it.item === ModItems.tempad } !in player.inventory) return@handle
                val ctx = message.ctx.getCtx(player)
                TempadLocations[player, ctx, message.providerId]!! -= message.locationId
            }
        )
    }

    override fun type() = type
}
