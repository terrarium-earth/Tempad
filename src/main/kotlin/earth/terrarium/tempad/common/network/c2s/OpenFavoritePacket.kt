package earth.terrarium.tempad.common.network.c2s

import com.teamresourceful.resourcefullib.common.network.Packet
import com.teamresourceful.resourcefullib.common.network.base.NetworkHandle
import com.teamresourceful.resourcefullib.common.network.base.PacketType
import com.teamresourceful.resourcefullib.common.network.defaults.CodecPacketType
import earth.terrarium.tempad.Tempad.Companion.tempadId
import earth.terrarium.tempad.api.test.ContextHolder
import earth.terrarium.tempad.common.data.getPinnedLocation
import earth.terrarium.tempad.common.entity.TimedoorEntity

data class OpenFavoritePacket(val ctx: ContextHolder<*>): Packet<OpenFavoritePacket> {
    companion object {
        val TYPE = CodecPacketType.Server.create(
            "open_favorite".tempadId,
            ContextHolder.codec.map(::OpenFavoritePacket, OpenFavoritePacket::ctx),
            NetworkHandle.handle { message, player ->
                val ctx = message.ctx.getCtx(player)
                player.getPinnedLocation(ctx)?.let { TimedoorEntity.openTimedoor(player, ctx, it) }
            }
        )
    }

    override fun type(): PacketType<OpenFavoritePacket> = TYPE
}
