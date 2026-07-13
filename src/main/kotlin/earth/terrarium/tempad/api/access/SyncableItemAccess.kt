package earth.terrarium.tempad.api.access

import com.teamresourceful.bytecodecs.base.ByteCodec
import earth.terrarium.tempad.api.PriorityId
import io.netty.buffer.ByteBuf
import net.minecraft.resources.Identifier
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.neoforged.neoforge.transfer.access.ItemAccess

typealias ItemAccessProvider<T> = (Player, T) -> ItemAccess

data class ItemAccessAddressType<T: Any>(val id: Identifier, val codec: ByteCodec<T>) {
    fun getAccess(player: Player, context: T): ItemAccess {
        return ItemAccessRegistry.get(this, player, context)
    }

    fun decode(buf: ByteBuf): ItemAccessAddress<T> {
        val data = codec.decode(buf)
        return ItemAccessAddress(this, data)
    }

    companion object {
        val codec: ByteCodec<ItemAccessAddressType<*>> = ByteCodec.passthrough(
            { buf, provider ->
                Identifier.STREAM_CODEC.encode(buf, provider.id)
            },
            { buf ->
                val id = Identifier.STREAM_CODEC.decode(buf)
                ItemAccessRegistry.registry.keys.first { it.id == id }
            }
        )
    }
}

fun <T: Any> ItemAccessAddressType<T>.holder(data: T): ItemAccessAddress<T> = ItemAccessAddress(this, data)

data class ItemAccessAddress<T: Any>(val type: ItemAccessAddressType<T>, val data: T) {
    fun getAccess(player: Player): ItemAccess {
        return type.getAccess(player, data)
    }

    fun encode(buf: ByteBuf) {
        ItemAccessAddressType.codec.encode(type, buf)
        type.codec.encode(data, buf)
    }

    companion object {
        val codec: ByteCodec<ItemAccessAddress<*>> = ByteCodec.passthrough(
            { buf, provider ->
                provider.encode(buf)
            },
            { buf ->
                val type = ItemAccessAddressType.codec.decode(buf)
                return@passthrough type.decode(buf)
            }
        )
    }
}

typealias ItemAccessAddressLocator = (Player, (ItemStack) -> Boolean) -> ItemAccessAddress<*>?

object ItemAccessRegistry {
    val registry: Map<ItemAccessAddressType<*>, ItemAccessProvider<*>>
        field = mutableMapOf()

    val locatorRegistry: Map<PriorityId, ItemAccessAddressLocator>
        field = sortedMapOf()

    fun <T: Any> register(type: ItemAccessAddressType<T>, provider: ItemAccessProvider<T>) {
        registry[type] = provider
    }

    fun registerLocator(id: PriorityId, locator: ItemAccessAddressLocator) {
        locatorRegistry[id] = locator
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : Any> get(type: ItemAccessAddressType<T>, player: Player, context: T): ItemAccess {
        val thing = registry[type] ?: error("No provider for $type")
        val finalThing = thing as ItemAccessProvider<T>
        return finalThing(player, context)
    }

    fun locate(player: Player, predicate: (ItemStack) -> Boolean): ItemAccessAddress<*>? {
        for ((_, locator) in locatorRegistry) {
            val context = locator(player, predicate)
            if(context != null) {
                return context
            }
        }
        return null
    }
}