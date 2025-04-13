package earth.terrarium.tempad.common.network.c2s

import com.teamresourceful.bytecodecs.base.ByteCodec
import com.teamresourceful.bytecodecs.base.`object`.ObjectByteCodec
import com.teamresourceful.resourcefullib.common.bytecodecs.ExtraByteCodecs
import com.teamresourceful.resourcefullib.common.color.Color
import com.teamresourceful.resourcefullib.common.network.Packet
import com.teamresourceful.resourcefullib.common.network.base.PacketType
import earth.terrarium.tempad.common.block.timedoor_marker.ChronomarkBE
import earth.terrarium.tempad.common.network.ServerPacketCompanion
import earth.terrarium.tempad.common.registries.accessId
import earth.terrarium.tempad.common.registries.angle
import earth.terrarium.tempad.common.registries.color
import earth.terrarium.tempad.common.registries.locked
import earth.terrarium.tempad.common.registries.owner
import earth.terrarium.tempad.common.registries.yOffset
import earth.terrarium.tempad.tempadId
import net.minecraft.core.BlockPos
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.player.Player

data class UpdateChronomarkPacket(val blockPos: BlockPos, val color: Color, val name: String, val access: ResourceLocation, val locked: Boolean, val yOffset: Float, val angle: Int): Packet<UpdateChronomarkPacket> {
    override fun type(): PacketType<UpdateChronomarkPacket> = Companion

    companion object: ServerPacketCompanion<UpdateChronomarkPacket> {
        override val id: ResourceLocation = "update_chronomark".tempadId
        override val byteCodec: ByteCodec<UpdateChronomarkPacket> = ObjectByteCodec.create(
            ExtraByteCodecs.BLOCK_POS.fieldOf(UpdateChronomarkPacket::blockPos),
            Color.BYTE_CODEC.fieldOf(UpdateChronomarkPacket::color),
            ByteCodec.STRING.fieldOf(UpdateChronomarkPacket::name),
            ExtraByteCodecs.RESOURCE_LOCATION.fieldOf(UpdateChronomarkPacket::access),
            ByteCodec.BOOLEAN.fieldOf(UpdateChronomarkPacket::locked),
            ByteCodec.FLOAT.fieldOf(UpdateChronomarkPacket::yOffset),
            ByteCodec.INT.fieldOf(UpdateChronomarkPacket::angle),
            ::UpdateChronomarkPacket
        )

        override fun onReceive(packet: UpdateChronomarkPacket, player: Player) {
            (player.level().getBlockEntity(packet.blockPos) as? ChronomarkBE)?.let {
                if(!player.mayInteract(player.level(), packet.blockPos) || (it.locked && it.owner?.id != player.gameProfile.id)) return

                it.color = packet.color
                it.posName = Component.literal(packet.name)
                it.accessId = packet.access
                it.locked = packet.locked && it.owner?.id == player.gameProfile.id
                it.yOffset = packet.yOffset.coerceIn(-5f, 5f)
                it.angle = packet.angle
                it.setChanged()
            }
        }
    }
}