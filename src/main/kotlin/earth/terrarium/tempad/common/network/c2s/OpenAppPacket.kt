package earth.terrarium.tempad.common.network.c2s

import com.teamresourceful.bytecodecs.base.ByteCodec
import com.teamresourceful.resourcefullib.common.bytecodecs.ExtraByteCodecs
import com.teamresourceful.resourcefullib.common.network.Packet
import com.teamresourceful.resourcefullib.common.network.base.PacketType
import earth.terrarium.tempad.api.app.TempadAppRegistry
import earth.terrarium.tempad.api.access.ItemAccessRegistry
import earth.terrarium.tempad.common.network.ServerPacketCompanion
import earth.terrarium.tempad.common.registries.ModItems
import earth.terrarium.tempad.tempadId
import net.minecraft.resources.Identifier
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.player.Player

data class OpenAppPacket(val id: Identifier): Packet<OpenAppPacket> {
    companion object: ServerPacketCompanion<OpenAppPacket> {
        override val id = "open_app".tempadId
        override val byteCodec: ByteCodec<OpenAppPacket> = ExtraByteCodecs.IDENTIFIER.map(::OpenAppPacket) {it.id}

        override fun onReceive(packet: OpenAppPacket, player: Player) {
            val ctx = ItemAccessRegistry.locate(player) { it.`is`(ModItems.tempad) } ?: return
            TempadAppRegistry[packet.id, player, ctx]?.openMenu(player as ServerPlayer)
        }
    }

    override fun type(): PacketType<OpenAppPacket> = Companion
}
