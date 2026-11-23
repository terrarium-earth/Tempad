package earth.terrarium.tempad.common.network.c2s

import earth.terrarium.tempad.api.context.WorkstationContext
import earth.terrarium.tempad.common.network.resolveTempad
import com.teamresourceful.resourcefullib.common.network.Packet
import com.teamresourceful.resourcefullib.common.network.base.NetworkHandle
import com.teamresourceful.resourcefullib.common.network.base.PacketType
import com.teamresourceful.resourcefullib.common.network.defaults.CodecPacketType
import earth.terrarium.tempad.tempadId
import earth.terrarium.tempad.api.app.AppHolder
import earth.terrarium.tempad.api.app.AppRegistry
import earth.terrarium.tempad.api.context.ContextHolder
import earth.terrarium.tempad.common.registries.*
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer

data class RedirectAppPacket(val appHolder: AppHolder) : Packet<RedirectAppPacket> {
    constructor(appID: ResourceLocation, data: ContextHolder<*>, isStationary: Boolean) : this(AppHolder(appID, data, isStationary))

    companion object {
        val type = CodecPacketType.Server.create(
            "redirect_app".tempadId,
            AppRegistry.BYTE_CODEC.map(::RedirectAppPacket, RedirectAppPacket::appHolder),
            NetworkHandle.handle { message, player ->
                val ctx = message.appHolder.ctx.resolveTempad(player) ?: return@handle
                // Derive "stationary" from the validated context instead of trusting the client flag.
                val app = AppRegistry[message.appHolder.id, ctx, ctx is WorkstationContext] ?: return@handle
                if (!app.isEnabled(player)) return@handle
                app.openMenu(player as ServerPlayer)
            }
        )
    }

    override fun type(): PacketType<RedirectAppPacket> = type
}
