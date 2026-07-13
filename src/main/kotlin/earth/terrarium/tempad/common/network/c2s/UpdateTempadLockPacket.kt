package earth.terrarium.tempad.common.network.c2s

import com.teamresourceful.bytecodecs.base.ByteCodec
import com.teamresourceful.bytecodecs.base.`object`.ObjectByteCodec
import com.teamresourceful.resourcefullib.common.network.Packet
import com.teamresourceful.resourcefullib.common.network.base.PacketType
import earth.terrarium.tempad.api.access.ItemAccessAddress
import earth.terrarium.tempad.common.network.ServerPacketCompanion
import earth.terrarium.tempad.common.registries.ModComponents
import earth.terrarium.tempad.common.registries.locked
import earth.terrarium.tempad.common.registries.owner
import earth.terrarium.tempad.common.utils.exchange
import earth.terrarium.tempad.common.utils.transfer
import earth.terrarium.tempad.tempadId
import net.minecraft.world.entity.player.Player

data class UpdateTempadLockPacket(val locked: Boolean, val ctx: ItemAccessAddress<*>) : Packet<UpdateTempadLockPacket> {
    companion object : ServerPacketCompanion<UpdateTempadLockPacket> {
        override val id = "update_tempad_lock".tempadId
        override val byteCodec: ByteCodec<UpdateTempadLockPacket> = ObjectByteCodec.create(
            ByteCodec.BOOLEAN.fieldOf { it.locked },
            ItemAccessAddress.codec.fieldOf { it.ctx },
            ::UpdateTempadLockPacket,
        )

        override fun onReceive(message: UpdateTempadLockPacket, player: Player) {
            val access = message.ctx.getAccess(player)
            transfer {
                access.exchange(
                    access.resource.with(ModComponents.locked, message.locked && player.gameProfile.id == access.resource.owner?.id),
                    access.amount
                )
            }
        }
    }

    override fun type(): PacketType<UpdateTempadLockPacket> = Companion
}
