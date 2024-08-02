package earth.terrarium.tempad.common.network.c2s

import com.teamresourceful.bytecodecs.base.`object`.ObjectByteCodec
import com.teamresourceful.resourcefullib.common.bytecodecs.ExtraByteCodecs
import com.teamresourceful.resourcefullib.common.network.Packet
import com.teamresourceful.resourcefullib.common.network.base.NetworkHandle
import com.teamresourceful.resourcefullib.common.network.base.PacketType
import com.teamresourceful.resourcefullib.common.network.defaults.CodecPacketType
import earth.terrarium.tempad.Tempad.Companion.tempadId
import earth.terrarium.tempad.api.context.ContextHolder
import earth.terrarium.tempad.common.data.OrganizationMethod
import earth.terrarium.tempad.common.registries.defaultApp
import earth.terrarium.tempad.common.registries.defaultMacro
import net.minecraft.resources.ResourceLocation

class SaveSettingsPacket(
    val ctxData: ContextHolder<*>,
    val defaultApp: ResourceLocation,
    val defaultMacro: ResourceLocation,
    val organizationMethod: OrganizationMethod,
) :
    Packet<SaveSettingsPacket> {
    companion object {
        val type = CodecPacketType.Server.create(
            "save_settings".tempadId,
            ObjectByteCodec.create(
                ContextHolder.codec.fieldOf(SaveSettingsPacket::ctxData),
                ExtraByteCodecs.RESOURCE_LOCATION.fieldOf(SaveSettingsPacket::defaultApp),
                ExtraByteCodecs.RESOURCE_LOCATION.fieldOf(SaveSettingsPacket::defaultMacro),
                OrganizationMethod.BYTE_CODEC.fieldOf(SaveSettingsPacket::organizationMethod),
                ::SaveSettingsPacket
            ),
            NetworkHandle.handle { packet, player ->
                val ctx = packet.ctxData.getCtx(player)
                val tempad = ctx.stack
                tempad.defaultApp = packet.defaultApp
                tempad.defaultMacro = packet.defaultMacro
            }
        )
    }

    override fun type(): PacketType<SaveSettingsPacket> = type
}