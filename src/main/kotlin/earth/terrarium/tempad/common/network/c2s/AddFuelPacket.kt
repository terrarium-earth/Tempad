package earth.terrarium.tempad.common.network.c2s

import com.teamresourceful.bytecodecs.base.ByteCodec
import com.teamresourceful.bytecodecs.base.`object`.ObjectByteCodec
import com.teamresourceful.resourcefullib.common.network.Packet
import com.teamresourceful.resourcefullib.common.network.base.NetworkHandle
import com.teamresourceful.resourcefullib.common.network.base.PacketType
import com.teamresourceful.resourcefullib.common.network.defaults.CodecPacketType
import earth.terrarium.tempad.Tempad.Companion.tempadId
import earth.terrarium.tempad.api.context.ItemContext
import earth.terrarium.tempad.api.fuel.FuelHandler
import earth.terrarium.tempad.api.fuel.MutableFuelHandler
import earth.terrarium.tempad.common.menu.FuelMenu
import earth.terrarium.tempad.common.utils.get

data class AddFuelPacket(val ctx: ItemContext, val auto: Boolean): Packet<AddFuelPacket> {
    companion object {
        val TYPE = CodecPacketType.Server.create(
            "add_fuel".tempadId,
            ObjectByteCodec.create(
                ItemContext.codec.fieldOf { it.ctx },
                ByteCodec.BOOLEAN.fieldOf { it.auto },
                ::AddFuelPacket
            ),
            NetworkHandle.handle { packet, player ->
                val menu = player.containerMenu
                val tempadStack = packet.ctx.getStack(player)
                val fuelHandler = tempadStack[FuelHandler.CAPABILITY] ?: return@handle
                if (fuelHandler !is MutableFuelHandler) return@handle
                if (packet.auto || menu !is FuelMenu) {
                    if (fuelHandler.hasSpaceLeft) {
                        fuelHandler.addChargeFromPlayer(player)
                    }
                } else {
                    if(FuelHandler.moveCharge(menu.container[0], tempadStack)) return@handle
                    if (fuelHandler.hasSpaceLeft) {
                        fuelHandler.addChargeFromItem(packet.ctx.getInstance(player))
                    }
                }
            }
        )
    }

    override fun type(): PacketType<AddFuelPacket> = TYPE
}
