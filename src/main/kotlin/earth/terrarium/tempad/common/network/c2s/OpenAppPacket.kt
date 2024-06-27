package earth.terrarium.tempad.common.network.c2s

import com.teamresourceful.bytecodecs.base.ByteCodec
import com.teamresourceful.bytecodecs.base.`object`.ObjectByteCodec
import com.teamresourceful.resourcefullib.common.bytecodecs.ExtraByteCodecs
import com.teamresourceful.resourcefullib.common.network.Packet
import com.teamresourceful.resourcefullib.common.network.base.NetworkHandle
import com.teamresourceful.resourcefullib.common.network.base.PacketType
import com.teamresourceful.resourcefullib.common.network.defaults.CodecPacketType
import earth.terrarium.tempad.Tempad
import earth.terrarium.tempad.Tempad.tempadId
import earth.terrarium.tempad.api.app.TempadApp
import earth.terrarium.tempad.common.registries.ModItems
import earth.terrarium.tempad.common.utils.get
import net.minecraft.server.level.ServerPlayer

data class OpenAppPacket(val app: TempadApp<*>, val slotId: Int) : Packet<OpenAppPacket> {
    companion object {
        val TYPE = CodecPacketType.Server.create(
            "open_app".tempadId,
            ObjectByteCodec.create(
                ExtraByteCodecs.registry(Tempad.APP_REGISTRY).fieldOf { it.app },
                ByteCodec.INT.fieldOf { it.slotId },
                ::OpenAppPacket
            ),
            NetworkHandle.handle { message, player ->
                val stack = player.inventory[message.slotId]
                if (!stack.`is`(ModItems.TEMPAD) || !message.app.isAppAvailable(player, stack)) return@handle
                message.app.openMenu(player as ServerPlayer)
            }
        )
    }

    override fun type(): PacketType<OpenAppPacket> = TYPE
}
