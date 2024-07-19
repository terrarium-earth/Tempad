package earth.terrarium.tempad.common.network.c2s

import com.teamresourceful.resourcefullib.common.network.Packet
import com.teamresourceful.resourcefullib.common.network.base.NetworkHandle
import com.teamresourceful.resourcefullib.common.network.base.PacketType
import com.teamresourceful.resourcefullib.common.network.defaults.CodecPacketType
import earth.terrarium.tempad.Tempad.Companion.tempadId
import earth.terrarium.tempad.api.app.AppHolder
import earth.terrarium.tempad.api.app.AppRegistry
import earth.terrarium.tempad.api.test.ContextHolder
import earth.terrarium.tempad.common.registries.*
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer

data class OpenAppPacket(val appHolder: AppHolder) : Packet<OpenAppPacket> {
    constructor(appID: ResourceLocation, data: ContextHolder<*>) : this(AppHolder(appID, data))

    companion object {
        val TYPE = CodecPacketType.Server.create(
            "open_app".tempadId,
            AppRegistry.BYTE_CODEC.map(::OpenAppPacket, OpenAppPacket::appHolder),
            NetworkHandle.handle { message, player ->
                val app = message.appHolder.getApp(player)
                val stack = message.appHolder.getCtx(player).stack
                if (!stack.`is`(ModItems.tempad) || (app != null && !app.isEnabled(player))) return@handle
                app?.openMenu(player as ServerPlayer)
            }
        )
    }

    override fun type(): PacketType<OpenAppPacket> = TYPE
}
