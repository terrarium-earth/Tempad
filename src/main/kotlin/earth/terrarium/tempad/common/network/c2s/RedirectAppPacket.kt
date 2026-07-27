package earth.terrarium.tempad.common.network.c2s

import com.teamresourceful.resourcefullib.common.network.Packet
import com.teamresourceful.resourcefullib.common.network.base.NetworkHandle
import com.teamresourceful.resourcefullib.common.network.base.PacketType
import com.teamresourceful.resourcefullib.common.network.defaults.CodecPacketType
import earth.terrarium.tempad.tempadId
import earth.terrarium.tempad.api.app.TempadAppHolder
import earth.terrarium.tempad.api.app.TempadAppRegistry
import earth.terrarium.tempad.api.access.ItemAccessAddress
import earth.terrarium.tempad.common.registries.*
import earth.terrarium.tempad.common.utils.stack
import net.minecraft.resources.Identifier
import net.minecraft.server.level.ServerPlayer

data class RedirectAppPacket(val appHolder: TempadAppHolder) : Packet<RedirectAppPacket> {
    constructor(appID: Identifier, data: ItemAccessAddress<*>) : this(TempadAppHolder(appID, data))

    companion object {
        val type = CodecPacketType.Server.create(
            "redirect_app".tempadId,
            TempadAppRegistry.BYTE_CODEC.map(::RedirectAppPacket, RedirectAppPacket::appHolder),
            NetworkHandle.handle { message, player ->
                val app = message.appHolder.getApp(player)
                val stack = message.appHolder.getCtx(player).stack
                if (!stack.`is`(ModItems.tempad) || (app != null && !app.isEnabled(player))) return@handle
                app?.openMenu(player as ServerPlayer)
            }
        )
    }

    override fun type(): PacketType<RedirectAppPacket> = type
}
