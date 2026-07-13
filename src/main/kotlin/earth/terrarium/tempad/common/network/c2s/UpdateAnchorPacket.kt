package earth.terrarium.tempad.common.network.c2s

import com.teamresourceful.bytecodecs.base.ByteCodec
import com.teamresourceful.bytecodecs.base.`object`.ObjectByteCodec
import com.teamresourceful.resourcefullib.common.bytecodecs.ExtraByteCodecs
import com.teamresourceful.resourcefullib.common.color.Color
import com.teamresourceful.resourcefullib.common.network.Packet
import com.teamresourceful.resourcefullib.common.network.base.PacketType
import earth.terrarium.tempad.common.block.timedoor_marker.AbstractMarkerBe
import earth.terrarium.tempad.common.block.timedoor_marker.TimedoorMarkerBE
import earth.terrarium.tempad.common.block.timedoor_marker.TimedoorMarkerBlock
import earth.terrarium.tempad.common.network.ServerPacketCompanion
import earth.terrarium.tempad.common.registries.accessId
import earth.terrarium.tempad.common.registries.canAccess
import earth.terrarium.tempad.common.registries.color
import earth.terrarium.tempad.common.registries.locked
import earth.terrarium.tempad.common.registries.owner
import earth.terrarium.tempad.tempadId
import net.minecraft.core.BlockPos
import net.minecraft.network.chat.Component
import net.minecraft.resources.Identifier
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.player.Player

data class UpdateAnchorPacket(val blockPos: BlockPos, val color: Color, val name: String, val access: Boolean, val locked: Boolean): Packet<UpdateAnchorPacket> {
    override fun type(): PacketType<UpdateAnchorPacket> = Companion

    companion object: ServerPacketCompanion<UpdateAnchorPacket> {
        override val id: Identifier = "update_anchor".tempadId
        override val byteCodec: ByteCodec<UpdateAnchorPacket> = ObjectByteCodec.create(
            ExtraByteCodecs.BLOCK_POS.fieldOf(UpdateAnchorPacket::blockPos),
            Color.BYTE_CODEC.fieldOf(UpdateAnchorPacket::color),
            ByteCodec.STRING.fieldOf(UpdateAnchorPacket::name),
            ByteCodec.BOOLEAN.fieldOf(UpdateAnchorPacket::access),
            ByteCodec.BOOLEAN.fieldOf(UpdateAnchorPacket::locked),
            ::UpdateAnchorPacket
        )

        override fun onReceive(packet: UpdateAnchorPacket, player: Player) {
            (player.level().getBlockEntity(packet.blockPos) as? TimedoorMarkerBE)?.let {
                if(!player.mayInteract(player.level() as ServerLevel, packet.blockPos) || (it.locked && it.owner?.id != player.gameProfile.id)) return

                it.color = packet.color
                it.posName = Component.literal(packet.name)
                it.canAccess = packet.access
                it.locked = packet.locked && it.owner?.id == player.gameProfile.id
                it.setChanged()
            }
        }
    }
}