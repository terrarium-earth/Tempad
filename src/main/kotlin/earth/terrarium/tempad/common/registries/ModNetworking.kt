package earth.terrarium.tempad.common.registries

import com.teamresourceful.bytecodecs.base.ByteCodec
import com.teamresourceful.resourcefullib.common.network.Network
import earth.terrarium.tempad.Tempad.Companion.tempadId
import earth.terrarium.tempad.common.config.CommonConfig
import earth.terrarium.tempad.common.config.CommonConfigCache
import earth.terrarium.tempad.common.network.c2s.*

object ModNetworking {
    val CHANNEL: Network = Network("main".tempadId, 0, false)

    fun init() {
        CHANNEL.register(CreateLocationPacket.TYPE)
        CHANNEL.register(DeleteLocationPacket.TYPE)
        CHANNEL.register(OpenAppPacket.TYPE)
        CHANNEL.register(OpenFavoritePacket.TYPE)
        CHANNEL.register(OpenTimedoorPacket.TYPE)
    }
}