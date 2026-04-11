package earth.terrarium.tempad.common.network.c2s

import com.teamresourceful.bytecodecs.base.ByteCodec
import com.teamresourceful.resourcefullib.common.bytecodecs.ExtraByteCodecs
import com.teamresourceful.resourcefullib.common.network.Packet
import com.teamresourceful.resourcefullib.common.network.base.PacketType
import earth.terrarium.tempad.api.app.AppRegistry
import earth.terrarium.tempad.api.context.ContextRegistry
import earth.terrarium.tempad.api.macro.MacroRegistry
import earth.terrarium.tempad.common.network.ServerPacketCompanion
import earth.terrarium.tempad.common.registries.ModApps
import earth.terrarium.tempad.common.registries.ModItems
import earth.terrarium.tempad.common.registries.defaultApp
import earth.terrarium.tempad.common.registries.defaultMacro
import earth.terrarium.tempad.tempadId
import net.minecraft.resources.Identifier
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.player.Player

data class OpenAppPacket(val id: Identifier): Packet<OpenAppPacket> {
    companion object: ServerPacketCompanion<OpenAppPacket> {
        override val id = "open_app".tempadId
        override val byteCodec: ByteCodec<OpenAppPacket> = ExtraByteCodecs.RESOURCE_LOCATION.map(::OpenAppPacket) {it.id}

        override fun onReceive(packet: OpenAppPacket, player: Player) {
            val ctx = ContextRegistry.locate(player) { it.`is`(ModItems.tempad) } ?: return
            AppRegistry[packet.id, ctx, false]?.openMenu(player as ServerPlayer)
        }
    }

    override fun type(): PacketType<OpenAppPacket> = Companion
}
