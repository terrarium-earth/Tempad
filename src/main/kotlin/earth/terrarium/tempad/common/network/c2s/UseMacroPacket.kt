package earth.terrarium.tempad.common.network.c2s

import com.teamresourceful.bytecodecs.base.ByteCodec
import com.teamresourceful.resourcefullib.common.network.Packet
import com.teamresourceful.resourcefullib.common.network.base.PacketType
import earth.terrarium.tempad.api.access.ItemAccessRegistry
import earth.terrarium.tempad.api.macro.MacroRegistry
import earth.terrarium.tempad.common.network.ServerPacketCompanion
import earth.terrarium.tempad.common.registries.ModItems
import earth.terrarium.tempad.common.registries.defaultMacro
import earth.terrarium.tempad.common.utils.stack
import earth.terrarium.tempad.tempadId
import net.minecraft.world.entity.player.Player

class UseMacroPacket(): Packet<UseMacroPacket> {
    companion object: ServerPacketCompanion<UseMacroPacket> {
        override val id = "use_macro".tempadId
        override val byteCodec: ByteCodec<UseMacroPacket> = ByteCodec.unit(::UseMacroPacket)

        override fun onReceive(packet: UseMacroPacket, player: Player) {
            val ctx = ItemAccessRegistry.locate(player) { it.`is`(ModItems.tempad) } ?: return
            val access = ctx.getAccess(player)
            if (player.cooldowns.isOnCooldown(access.stack)) return
            MacroRegistry[access.resource.defaultMacro]?.run(player, access)
        }
    }

    override fun type(): PacketType<UseMacroPacket> = Companion
}
