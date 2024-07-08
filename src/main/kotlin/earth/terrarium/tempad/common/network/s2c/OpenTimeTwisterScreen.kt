package earth.terrarium.tempad.common.network.s2c

import com.teamresourceful.resourcefullib.common.network.Packet
import com.teamresourceful.resourcefullib.common.network.base.NetworkHandle
import com.teamresourceful.resourcefullib.common.network.base.PacketType
import com.teamresourceful.resourcefullib.common.network.defaults.CodecPacketType
import earth.terrarium.tempad.Tempad.Companion.tempadId
import earth.terrarium.tempad.client.screen.TimeTwisterScreen
import earth.terrarium.tempad.common.data.HistoricalLocation
import earth.terrarium.tempad.common.data.TravelHistoryAttachment
import net.minecraft.client.Minecraft
import java.util.UUID

class OpenTimeTwisterScreen(val values: TravelHistoryAttachment): Packet<OpenTimeTwisterScreen> {
    companion object {
        val TYPE = CodecPacketType.Client.create(
            "open_time_twister_screen".tempadId,
            TravelHistoryAttachment.BYTE_CODEC.map(::OpenTimeTwisterScreen, OpenTimeTwisterScreen::values),
            NetworkHandle.handle { packet ->
                Minecraft.getInstance().setScreen(TimeTwisterScreen(packet.values.history))
            }
        )
    }

    override fun type(): PacketType<OpenTimeTwisterScreen> = TYPE
}