package earth.terrarium.tempad.common.registries

import com.teamresourceful.resourcefullib.common.network.Network
import earth.terrarium.tempad.Tempad.Companion.tempadId
import earth.terrarium.tempad.common.network.c2s.*

object ModNetworking {
    val channel: Network = Network("main".tempadId, 0, false)

    fun init() {
        channel.register(CreateLocationPacket.TYPE)
        channel.register(DeleteLocationPacket.TYPE)
        channel.register(OpenAppPacket.TYPE)
        channel.register(OpenFavoritePacket.TYPE)
        channel.register(OpenTimedoorPacket.TYPE)
        channel.register(SetFavoritePacket.TYPE)
        channel.register(AddFuelPacket.TYPE)
        channel.register(TransferFuelPacket.TYPE)
        channel.register(SaveSettingsPacket.TYPE)
        channel.register(BackTrackLocation.TYPE)
    }
}