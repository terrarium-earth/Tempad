package earth.terrarium.tempad.common.network.c2s

import com.teamresourceful.bytecodecs.base.ByteCodec
import com.teamresourceful.resourcefullib.common.network.Network
import com.teamresourceful.resourcefullib.common.network.Packet
import com.teamresourceful.resourcefullib.common.network.base.ClientboundPacketType
import com.teamresourceful.resourcefullib.common.network.base.NetworkHandle
import com.teamresourceful.resourcefullib.common.network.base.PacketType
import com.teamresourceful.resourcefullib.common.network.defaults.CodecPacketType
import earth.terrarium.tempad.Tempad.tempadId
import earth.terrarium.tempad.common.registries.ModNetworking
import net.minecraft.world.entity.player.Player
import kotlin.reflect.KMutableProperty
import kotlin.reflect.KProperty

data class ConfigSync<T>(val getter: KProperty<T>, val setter: KMutableProperty<T>, val codec: ByteCodec<T>) {
    companion object {
        val SYNC_LIST = mutableListOf<ConfigSync<*>>()

        fun syncAll(player: Player) {
            SYNC_LIST.forEach {
                val packet = it.createPacket()
                ModNetworking.CHANNEL.sendToPlayer(packet, player)
            }
        }
    }

    val syncType: CodecPacketType.Client<ConfigSyncData> = CodecPacketType.Client.create(
        getter.name.tempadId,
        codec.map({ ConfigSyncData(it) }, { it.config }),
        NetworkHandle.handle { message ->
            setter.setter.call(message.config)
        }
    )

    fun createPacket() = ConfigSyncData(getter.call())

    fun init(network: Network) {
        network.register(syncType)
        SYNC_LIST.add(this)
    }

    inner class ConfigSyncData(val config: T): Packet<ConfigSyncData> {
        override fun type(): PacketType<ConfigSyncData> = syncType
    }
}
