package earth.terrarium.tempad.common.network.c2s

import com.teamresourceful.bytecodecs.base.ByteCodec
import com.teamresourceful.bytecodecs.base.`object`.ObjectByteCodec
import com.teamresourceful.resourcefullib.common.bytecodecs.ExtraByteCodecs
import com.teamresourceful.resourcefullib.common.color.Color
import com.teamresourceful.resourcefullib.common.network.Packet
import com.teamresourceful.resourcefullib.common.network.base.PacketType
import earth.terrarium.tempad.common.block.MetronomeBe
import earth.terrarium.tempad.common.block.timedoor_marker.AbstractMarkerBe
import earth.terrarium.tempad.common.network.ServerPacketCompanion
import earth.terrarium.tempad.common.registries.accessId
import earth.terrarium.tempad.common.registries.color
import earth.terrarium.tempad.common.registries.locked
import earth.terrarium.tempad.common.registries.owner
import earth.terrarium.tempad.tempadId
import net.minecraft.core.BlockPos
import net.minecraft.network.chat.Component
import net.minecraft.resources.Identifier
import net.minecraft.world.entity.player.Player

data class UpdateMetronomePacket(val blockPos: BlockPos, val locked: Boolean): Packet<UpdateMetronomePacket> {
    override fun type(): PacketType<UpdateMetronomePacket> = Companion

    companion object: ServerPacketCompanion<UpdateMetronomePacket> {
        override val id: Identifier = "update_metronome".tempadId
        override val byteCodec: ByteCodec<UpdateMetronomePacket> = ObjectByteCodec.create(
            ExtraByteCodecs.BLOCK_POS.fieldOf(UpdateMetronomePacket::blockPos),
            ByteCodec.BOOLEAN.fieldOf(UpdateMetronomePacket::locked),
            ::UpdateMetronomePacket
        )

        override fun onReceive(packet: UpdateMetronomePacket, player: Player) {
            (player.level().getBlockEntity(packet.blockPos) as? MetronomeBe)?.let {
                if(!player.mayInteract(player.level(), packet.blockPos) || (it.locked && it.owner?.id != player.gameProfile.id)) return

                it.locked = packet.locked && it.owner?.id == player.gameProfile.id
                it.setChanged()
            }
        }
    }
}