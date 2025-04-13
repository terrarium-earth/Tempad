package earth.terrarium.tempad.common.network.c2s

import com.teamresourceful.bytecodecs.base.ByteCodec
import com.teamresourceful.bytecodecs.base.`object`.ObjectByteCodec
import com.teamresourceful.resourcefullib.common.network.Packet
import com.teamresourceful.resourcefullib.common.network.base.PacketType
import earth.terrarium.tempad.api.context.ContextHolder
import earth.terrarium.tempad.api.context.modify
import earth.terrarium.tempad.common.network.ServerPacketCompanion
import earth.terrarium.tempad.common.registries.locked
import earth.terrarium.tempad.common.registries.owner
import earth.terrarium.tempad.tempadId
import net.minecraft.world.entity.player.Player

data class UpdateTempadLockPacket(val locked: Boolean, val ctx: ContextHolder<*>) : Packet<UpdateTempadLockPacket> {
    companion object : ServerPacketCompanion<UpdateTempadLockPacket> {
        override val id = "update_tempad_lock".tempadId
        override val byteCodec: ByteCodec<UpdateTempadLockPacket> = ObjectByteCodec.create(
            ByteCodec.BOOLEAN.fieldOf { it.locked },
            ContextHolder.codec.fieldOf { it.ctx },
            ::UpdateTempadLockPacket,
        )

        override fun onReceive(message: UpdateTempadLockPacket, player: Player) {
            message.ctx.getCtx(player).modify {
                it.locked = message.locked && player.gameProfile.id == it.owner?.id
            }
        }
    }

    override fun type(): PacketType<UpdateTempadLockPacket> = Companion
}
