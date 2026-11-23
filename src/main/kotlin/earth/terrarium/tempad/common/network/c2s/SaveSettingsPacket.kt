package earth.terrarium.tempad.common.network.c2s

import earth.terrarium.tempad.api.context.modify
import earth.terrarium.tempad.api.macro.MacroRegistry
import earth.terrarium.tempad.api.app.AppRegistry
import earth.terrarium.tempad.common.network.resolveTempad
import com.teamresourceful.bytecodecs.base.`object`.ObjectByteCodec
import com.teamresourceful.resourcefullib.common.bytecodecs.ExtraByteCodecs
import com.teamresourceful.resourcefullib.common.network.Packet
import com.teamresourceful.resourcefullib.common.network.base.NetworkHandle
import com.teamresourceful.resourcefullib.common.network.base.PacketType
import com.teamresourceful.resourcefullib.common.network.defaults.CodecPacketType
import earth.terrarium.tempad.tempadId
import earth.terrarium.tempad.api.context.ContextHolder
import earth.terrarium.tempad.common.data.OrganizationMethod
import earth.terrarium.tempad.common.registries.defaultApp
import earth.terrarium.tempad.common.registries.defaultMacro
import net.minecraft.resources.ResourceLocation

class SaveSettingsPacket(
    val ctxData: ContextHolder<*>,
    val defaultApp: ResourceLocation,
    val defaultMacro: ResourceLocation
) :
    Packet<SaveSettingsPacket> {
    companion object {
        val type = CodecPacketType.Server.create(
            "save_settings".tempadId,
            ObjectByteCodec.create(
                ContextHolder.codec.fieldOf(SaveSettingsPacket::ctxData),
                ExtraByteCodecs.RESOURCE_LOCATION.fieldOf(SaveSettingsPacket::defaultApp),
                ExtraByteCodecs.RESOURCE_LOCATION.fieldOf(SaveSettingsPacket::defaultMacro),
                ::SaveSettingsPacket
            ),
            NetworkHandle.handle { packet, player ->
                if (packet.defaultApp !in AppRegistry.getIds() || packet.defaultMacro !in MacroRegistry.getIds()) return@handle
                val ctx = packet.ctxData.resolveTempad(player) ?: return@handle
                // modify() writes the stack back through the context, so workstations get marked dirty / synced.
                ctx.modify {
                    it.defaultApp = packet.defaultApp
                    it.defaultMacro = packet.defaultMacro
                }
            }
        )
    }

    override fun type(): PacketType<SaveSettingsPacket> = type
}