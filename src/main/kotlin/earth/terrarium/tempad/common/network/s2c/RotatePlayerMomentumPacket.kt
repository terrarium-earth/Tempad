package earth.terrarium.tempad.common.network.s2c

import com.teamresourceful.bytecodecs.base.ByteCodec
import com.teamresourceful.resourcefullib.common.network.Packet
import com.teamresourceful.resourcefullib.common.network.base.NetworkHandle
import com.teamresourceful.resourcefullib.common.network.base.PacketType
import com.teamresourceful.resourcefullib.common.network.defaults.CodecPacketType
import earth.terrarium.tempad.tempadId
import net.minecraft.client.Minecraft
import net.minecraft.world.phys.Vec3

class RotatePlayerMomentumPacket(val angle: Float): Packet<RotatePlayerMomentumPacket> {
    companion object {
        val type = CodecPacketType.Client.create(
            "rotate_player_momentum".tempadId,
            ByteCodec.FLOAT.map(::RotatePlayerMomentumPacket, RotatePlayerMomentumPacket::angle),
            NetworkHandle.handle { message ->
                Minecraft.getInstance().player?.let {
                    it.deltaMovement = Vec3.ZERO // TODO rotate player momentum
                }
            }
        )
    }

    override fun type(): PacketType<RotatePlayerMomentumPacket> = type
}