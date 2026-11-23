package earth.terrarium.tempad.common.network.c2s

import earth.terrarium.tempad.common.network.PacketThrottle
import earth.terrarium.tempad.common.network.clampName
import earth.terrarium.tempad.common.network.resolveTempad
import earth.terrarium.tempad.common.config.CommonConfig
import com.teamresourceful.bytecodecs.base.ByteCodec
import com.teamresourceful.bytecodecs.base.`object`.ObjectByteCodec
import com.teamresourceful.resourcefullib.common.color.Color
import com.teamresourceful.resourcefullib.common.network.Packet
import com.teamresourceful.resourcefullib.common.network.base.NetworkHandle
import com.teamresourceful.resourcefullib.common.network.defaults.CodecPacketType
import earth.terrarium.tempad.tempadId
import earth.terrarium.tempad.api.locations.NamedGlobalVec3
import earth.terrarium.tempad.api.context.ContextHolder
import earth.terrarium.tempad.common.location_handlers.DefaultLocationHandler
import earth.terrarium.tempad.common.registries.ModItems
import earth.terrarium.tempad.common.utils.COLOR_BYTE_CODEC
import net.minecraft.network.chat.Component

data class CreateLocationPacket(val name: String, val color: Color, val ctx: ContextHolder<*>) : Packet<CreateLocationPacket> {
    companion object {
        val type = CodecPacketType.Server.create(
            "create_location".tempadId,
            ObjectByteCodec.create(
                ByteCodec.STRING.fieldOf { it.name },
                COLOR_BYTE_CODEC.fieldOf { it.color },
                ContextHolder.codec.fieldOf { it.ctx },
                ::CreateLocationPacket
            ),
            NetworkHandle.handle { message, player ->
                if (PacketThrottle.EDIT_LOCATION.isThrottled(player)) return@handle
                if (!CommonConfig.allowLocationSaving) return@handle
                message.ctx.resolveTempad(player) ?: return@handle
                DefaultLocationHandler(player.gameProfile) += NamedGlobalVec3(
                    Component.literal(message.name.clampName()),
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
