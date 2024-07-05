package earth.terrarium.tempad.common.config

import com.google.common.base.CaseFormat
import com.teamresourceful.bytecodecs.base.ByteCodec
import com.teamresourceful.resourcefulconfig.api.types.entries.Observable
import com.teamresourceful.resourcefullib.common.network.Network
import com.teamresourceful.resourcefullib.common.network.Packet
import com.teamresourceful.resourcefullib.common.network.base.ClientboundPacketType
import com.teamresourceful.resourcefullib.common.network.base.NetworkHandle
import com.teamresourceful.resourcefullib.common.network.base.PacketType
import com.teamresourceful.resourcefullib.common.network.defaults.CodecPacketType
import earth.terrarium.tempad.Tempad
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.player.Player
import net.neoforged.neoforge.server.ServerLifecycleHooks
import kotlin.reflect.KProperty

typealias Data<T> = ConfigCache.ConfigEntry<T>.ConfigSyncData

class ConfigCache(val modId: String, val network: Network) {
    val entries = mutableListOf<ConfigEntry<*>>()
    private val syncId = ResourceLocation.fromNamespaceAndPath(modId, "config_sync")
    private val syncType = ConfigSyncType()

    fun syncAll(player: Player) {
        val syncData = entries.map { it.createPacket() }
        network.sendToPlayer(ConfigSync(syncData), player)
    }

    fun ofInt(observable: KProperty<Observable<Int>>) = ConfigEntry(observable, ByteCodec.INT)

    fun ofBoolean(observable: KProperty<Observable<Boolean>>) = ConfigEntry(observable, ByteCodec.BOOLEAN)

    fun ofString(observable: KProperty<Observable<String>>) = ConfigEntry(observable, ByteCodec.STRING)

    inner class ConfigSyncType: ClientboundPacketType<ConfigSync> {
        override fun id(): ResourceLocation = syncId

        override fun decode(buffer: RegistryFriendlyByteBuf?): ConfigSync {
            val decodedEntries = mutableListOf<Data<*>>()
            entries.forEach { entry ->
                val data = entry.syncType.decode(buffer)
                decodedEntries.add(data)
            }
            return ConfigSync(decodedEntries)
        }

        override fun handle(message: ConfigSync): Runnable = Runnable {
            message.entries.forEach { it.set() }
        }

        override fun encode(message: ConfigSync?, buffer: RegistryFriendlyByteBuf?) {
            message?.entries?.forEach { entry ->
                entry.encode(buffer)
            }
        }
    }

    inner class ConfigSync(val entries: List<Data<*>>): Packet<ConfigSync> {
        override fun type(): PacketType<ConfigSync> = syncType
    }

    inner class ConfigEntry<T>(private val observable: KProperty<Observable<T>>, val codec: ByteCodec<T>) {
        var value: T = observable.getter.call().get()
        val syncType: CodecPacketType.Client<ConfigSyncData> = CodecPacketType.Client.create(
            ResourceLocation.fromNamespaceAndPath(modId, CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, observable.name)),
            codec.map({ ConfigSyncData(it) }, { it.config }),
            NetworkHandle.handle { message ->
                value = message.config
            }
        )

        init {
            network.register(syncType)
            observable.getter.call().addListener { _, newValue ->
                this.value = newValue
                network.sendToAllPlayers(ConfigSyncData(newValue), Tempad.server!!)
            }
        }

        fun createPacket() = ConfigSyncData(observable.getter.call().get())

        operator fun getValue(test: Any, property: KProperty<*>): T = value

        inner class ConfigSyncData(val config: T): Packet<ConfigSyncData> {
            override fun type(): PacketType<ConfigSyncData> = syncType

            fun encode(buffer: RegistryFriendlyByteBuf?) {
                syncType.encode(this, buffer)
            }

            fun set() {
                value = config
            }
        }
    }
}
