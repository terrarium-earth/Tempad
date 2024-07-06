package earth.terrarium.tempad.common.network.c2s

import com.teamresourceful.bytecodecs.base.ByteCodec
import com.teamresourceful.resourcefullib.common.network.Packet
import com.teamresourceful.resourcefullib.common.network.base.NetworkHandle
import com.teamresourceful.resourcefullib.common.network.base.PacketType
import com.teamresourceful.resourcefullib.common.network.defaults.CodecPacketType
import earth.terrarium.tempad.Tempad.Companion.tempadId
import earth.terrarium.tempad.api.fuel.FuelHandler
import earth.terrarium.tempad.common.fuel.DefaultFuelType
import earth.terrarium.tempad.common.menu.FuelMenu
import earth.terrarium.tempad.common.utils.get

data class AddFuelPacket(val slot: Int): Packet<AddFuelPacket> {
    companion object {
        val TYPE = CodecPacketType.Server.create(
            "add_fuel".tempadId,
            ByteCodec.INT.map(::AddFuelPacket, AddFuelPacket::slot),
            NetworkHandle.handle { packet, player ->
                val menu = player.containerMenu
                if (menu is FuelMenu) {
                    val tempadStack = player.inventory[packet.slot]
                    if(FuelHandler.moveCharge(menu.container[0], tempadStack)) return@handle
                    val type = DefaultFuelType.parse(tempadStack)
                    if (type.acceptsFuel()) {
                        type.consume(menu.container, player, tempadStack)
                    }
                }
            }
        )
    }

    override fun type(): PacketType<AddFuelPacket> = TYPE
}
