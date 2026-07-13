package earth.terrarium.tempad.common.network.c2s

import com.teamresourceful.bytecodecs.base.`object`.ObjectByteCodec
import com.teamresourceful.resourcefullib.common.bytecodecs.ExtraByteCodecs
import com.teamresourceful.resourcefullib.common.network.Packet
import com.teamresourceful.resourcefullib.common.network.base.NetworkHandle
import com.teamresourceful.resourcefullib.common.network.base.PacketType
import com.teamresourceful.resourcefullib.common.network.defaults.CodecPacketType
import earth.terrarium.tempad.tempadId
import earth.terrarium.tempad.api.access.ItemAccessAddress
import earth.terrarium.tempad.common.registries.ModComponents
import earth.terrarium.tempad.common.registries.defaultApp
import earth.terrarium.tempad.common.registries.defaultMacro
import earth.terrarium.tempad.common.utils.commit
import earth.terrarium.tempad.common.utils.exchange
import earth.terrarium.tempad.common.utils.transfer
import net.minecraft.resources.Identifier

class SaveSettingsPacket(
    val ctxData: ItemAccessAddress<*>,
    val defaultApp: Identifier,
    val defaultMacro: Identifier
) :
    Packet<SaveSettingsPacket> {
    companion object {
        val type = CodecPacketType.Server.create(
            "save_settings".tempadId,
            ObjectByteCodec.create(
                ItemAccessAddress.codec.fieldOf(SaveSettingsPacket::ctxData),
                ExtraByteCodecs.IDENTIFIER.fieldOf(SaveSettingsPacket::defaultApp),
                ExtraByteCodecs.IDENTIFIER.fieldOf(SaveSettingsPacket::defaultMacro),
                ::SaveSettingsPacket
            ),
            NetworkHandle.handle { packet, player ->
                val access = packet.ctxData.getAccess(player)
                transfer {
                    access.exchange(
                        access.resource.with(ModComponents.defaultApp, packet.defaultApp).with(ModComponents.defaultMacro, packet.defaultMacro),
                        access.amount
                    )
                    commit()
                }
            }
        )
    }

    override fun type(): PacketType<SaveSettingsPacket> = type
}