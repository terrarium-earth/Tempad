package earth.terrarium.tempad.common.registries

import com.teamresourceful.resourcefullib.common.network.Network
import earth.terrarium.tempad.Tempad.Companion.tempadId
import earth.terrarium.tempad.common.network.c2s.*
import earth.terrarium.tempad.common.network.s2c.OpenTimeTwisterScreen

object ModNetworking {
    val CHANNEL: Network = Network("main".tempadId, 0, false)

    fun init() {
        CHANNEL.register(CreateLocationPacket.TYPE)
        CHANNEL.register(DeleteLocationPacket.TYPE)
        CHANNEL.register(OpenAppPacket.TYPE)
        CHANNEL.register(OpenFavoritePacket.TYPE)
        CHANNEL.register(OpenTimedoorPacket.TYPE)
        CHANNEL.register(SetFavoritePacket.TYPE)
        CHANNEL.register(AddFuelPacket.TYPE)
        CHANNEL.register(TransferFuelPacket.TYPE)
        CHANNEL.register(SaveSettingsPacket.TYPE)
        CHANNEL.register(OpenTimeTwisterScreen.TYPE)
        CHANNEL.register(BackTrackLocation.TYPE)
    }
}