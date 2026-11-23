package earth.terrarium.tempad.common.config

import io.netty.buffer.Unpooled
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
import kotlin.reflect.KProperty

typealias Data<T> = ConfigSyncedCache.ConfigEntry<T>.ConfigSyncData

class ConfigSyncedCache(val modId: String, val network: Network) {
    val syncType = ConfigSyncType()
    val entries = mutableMapOf<ResourceLocation, ConfigEntry<*>>()
    private val syncId = ResourceLocation.fromNamespaceAndPath(modId, "config_sync")

    fun syncAll(player: Player) {
        val syncData = entries.mapValues { it.value.createPacket() }
        network.sendToPlayer(ConfigSync(syncData), player)
    }

    fun ofInt(observable: KProperty<Observable<Int>>) = ConfigEntry(observable, ByteCodec.INT).apply { entries.put(syncType.id(), this) }

    fun ofBoolean(observable: KProperty<Observable<Boolean>>) = ConfigEntry(observable, ByteCodec.BOOLEAN).apply { entries.put(syncType.id(), this) }

    fun ofString(observable: KProperty<Observable<String>>) = ConfigEntry(observable, ByteCodec.STRING).apply { entries.put(syncType.id(), this) }

    inner class ConfigSyncType: ClientboundPacketType<ConfigSync> {
        override fun id(): ResourceLocation = syncId

        override fun decode(buffer: RegistryFriendlyByteBuf): ConfigSync {
            val decodedEntries = mutableMapOf<ResourceLocation, Data<*>>()
            val size = buffer.readVarInt()
            repeat(size) {
                val id = buffer.readResourceLocation()
                // Each entry is length-prefixed so an id this side doesn't know can be skipped without
                // misreading every entry after it.
                val payload = buffer.readByteArray()
                val entry = entries[id] ?: return@repeat
                val entryBuf = RegistryFriendlyByteBuf(Unpooled.wrappedBuffer(payload), buffer.registryAccess(), buffer.connectionType)
                try {
                    decodedEntries[id] = entry.syncType.decode(entryBuf)
                } finally {
                    entryBuf.release()
                }
            }
            return ConfigSync(decodedEntries)
        }

        override fun handle(message: ConfigSync): Runnable = Runnable {
            message.entries.values.forEach { it.set() }
        }

        override fun encode(message: ConfigSync, buffer: RegistryFriendlyByteBuf) {
            buffer.writeVarInt(message.entries.size)
            message.entries.forEach { id, entry ->
                buffer.writeResourceLocation(id)
                val entryBuf = RegistryFriendlyByteBuf(Unpooled.buffer(), buffer.registryAccess(), buffer.connectionType)
                try {
                    entry.encode(entryBuf)
                    val payload = ByteArray(entryBuf.readableBytes())
                    entryBuf.readBytes(payload)
                    buffer.writeByteArray(payload)
                } finally {
                    entryBuf.release()
                }
            }
        }
    }

    inner class ConfigSync(val entries: Map<ResourceLocation, Data<*>>): Packet<ConfigSync> {
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
                // Listeners also fire on the client and before a server exists (initial config load).
                Tempad.server?.let { network.sendToAllPlayers(ConfigSyncData(newValue), it) }
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
