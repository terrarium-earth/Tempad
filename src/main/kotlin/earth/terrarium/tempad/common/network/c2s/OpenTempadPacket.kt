package earth.terrarium.tempad.common.network.c2s

import com.teamresourceful.bytecodecs.base.ByteCodec
import com.teamresourceful.resourcefullib.common.network.Packet
import com.teamresourceful.resourcefullib.common.network.base.PacketType
import earth.terrarium.tempad.api.app.AppRegistry
import earth.terrarium.tempad.api.access.ItemAccessRegistry
import earth.terrarium.tempad.common.network.ServerPacketCompanion
import earth.terrarium.tempad.common.registries.ModApps
import earth.terrarium.tempad.common.registries.ModItems
import earth.terrarium.tempad.common.registries.defaultApp
import earth.terrarium.tempad.tempadId
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.player.Player

class OpenTempadPacket(): Packet<OpenTempadPacket> {
    companion object: ServerPacketCompanion<OpenTempadPacket> {
        override val id = "open_tempad".tempadId
        override val byteCodec: ByteCodec<OpenTempadPacket> = ByteCodec.unit(::OpenTempadPacket)

        override fun onReceive(packet: OpenTempadPacket, player: Player) {
            val ctx = ItemAccessRegistry.locate(player) { it.`is`(ModItems.tempad) } ?: return
            val access = ctx.getAccess(player)
            (AppRegistry[access.resource.defaultApp, player, ctx, false]?: AppRegistry[ModApps.teleport, player, ctx, false])!!.openMenu(player as ServerPlayer)
        }
    }

    override fun type(): PacketType<OpenTempadPacket> = Companion
}
