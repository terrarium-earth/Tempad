package earth.terrarium.tempad.common.network.c2s

import com.teamresourceful.bytecodecs.base.ByteCodec
import com.teamresourceful.bytecodecs.base.`object`.ObjectByteCodec
import com.teamresourceful.resourcefullib.common.color.Color
import com.teamresourceful.resourcefullib.common.network.Packet
import com.teamresourceful.resourcefullib.common.network.base.NetworkHandle
import com.teamresourceful.resourcefullib.common.network.defaults.CodecPacketType
import earth.terrarium.tempad.tempadId
import earth.terrarium.tempad.api.locations.NamedGlobalVec3
import earth.terrarium.tempad.api.access.ItemAccessAddress
import earth.terrarium.tempad.common.location_handlers.DefaultLocationHandler
import earth.terrarium.tempad.common.registries.ModItems
import net.minecraft.network.chat.Component

data class CreateLocationPacket(val name: String, val color: Color, val ctx: ItemAccessAddress<*>) : Packet<CreateLocationPacket> {
    companion object {
        val type = CodecPacketType.Server.create(
            "create_location".tempadId,
            ObjectByteCodec.create(
                ByteCodec.STRING.fieldOf { it.name },
                Color.BYTE_CODEC.fieldOf { it.color },
                ItemAccessAddress.codec.fieldOf { it.ctx },
                ::CreateLocationPacket
            ),
            NetworkHandle.handle { message, player ->
                if ({ message.ctx.getAccess(player).resource.`is`(ModItems.tempad) } !in player.inventory) return@handle
                DefaultLocationHandler(player.gameProfile) += NamedGlobalVec3(
                    Component.literal(message.name),
                    player.position(),
                    player.level().dimension(),
                    player.yRot,
                    message.color
                )
            }
        )
    }

    override fun type() = type
}
