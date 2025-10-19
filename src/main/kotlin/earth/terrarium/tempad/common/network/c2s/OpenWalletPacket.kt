package earth.terrarium.tempad.common.network.c2s

import com.teamresourceful.bytecodecs.base.ByteCodec
import com.teamresourceful.resourcefullib.common.bytecodecs.ExtraByteCodecs
import com.teamresourceful.resourcefullib.common.network.Packet
import com.teamresourceful.resourcefullib.common.network.base.PacketType
import earth.terrarium.tempad.api.app.AppRegistry
import earth.terrarium.tempad.api.context.ContextRegistry
import earth.terrarium.tempad.api.macro.MacroRegistry
import earth.terrarium.tempad.common.items.items
import earth.terrarium.tempad.common.network.ServerPacketCompanion
import earth.terrarium.tempad.common.registries.ModApps
import earth.terrarium.tempad.common.registries.ModItems
import earth.terrarium.tempad.common.registries.defaultApp
import earth.terrarium.tempad.common.registries.defaultMacro
import earth.terrarium.tempad.common.registries.walletContents
import earth.terrarium.tempad.tempadId
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.player.Player

class OpenWalletPacket: Packet<OpenWalletPacket> {


    companion object: ServerPacketCompanion<OpenWalletPacket> {
        override val id = "open_wallet".tempadId
        override val byteCodec: ByteCodec<OpenWalletPacket> = ByteCodec.unit (::OpenWalletPacket)
        override fun onReceive(
            packet: OpenWalletPacket,
            player: Player
        ) {
            val ctx = ContextRegistry.locate(player) { it.`is`(ModItems.cardWallet) } ?: return
            player.openMenu(ctx.stack.items)

        }
    }

    override fun type(): PacketType<OpenWalletPacket> = Companion
}
