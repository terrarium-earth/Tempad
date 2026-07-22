package earth.terrarium.tempad.common.network.c2s

import com.teamresourceful.bytecodecs.base.ByteCodec
import com.teamresourceful.resourcefullib.common.network.Packet
import com.teamresourceful.resourcefullib.common.network.base.NetworkHandle
import com.teamresourceful.resourcefullib.common.network.base.PacketType
import com.teamresourceful.resourcefullib.common.network.defaults.CodecPacketType
import earth.terrarium.tempad.client.LiftwayEvents
import earth.terrarium.tempad.tempadId
import net.minecraft.core.Direction
import net.minecraft.server.level.ServerPlayer

class LiftActionPacket(val goinUp: Boolean): Packet<LiftActionPacket> {
    companion object {
        val type = CodecPacketType.Server.create(
            "lift_action".tempadId,
            ByteCodec.BOOLEAN.map(::LiftActionPacket, LiftActionPacket::goinUp),
            NetworkHandle.handle { message, player ->
                LiftwayEvents.lift(player as ServerPlayer, if(message.goinUp) Direction.UP else Direction.DOWN)
            }
        )
    }

    override fun type(): PacketType<LiftActionPacket> = type
}