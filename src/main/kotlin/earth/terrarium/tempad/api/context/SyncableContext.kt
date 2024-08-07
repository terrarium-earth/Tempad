package earth.terrarium.tempad.api.context

import com.teamresourceful.bytecodecs.base.ByteCodec
import io.netty.buffer.ByteBuf
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.player.Player

typealias ContextProvider<T> = (Player, T) -> SyncableContext<T>

data class ContextType<T: Any>(val id: ResourceLocation, val codec: ByteCodec<T>) {
    fun getCtx(player: Player, context: T): SyncableContext<T> {
        return ContextRegistry.get(this, player, context)
    }

    fun decode(buf: ByteBuf): ContextHolder<T> {
        val data = codec.decode(buf)
        return ContextHolder(this, data)
    }

    companion object {
        val codec: ByteCodec<ContextType<*>> = ByteCodec.passthrough(
            { buf, provider ->
                ResourceLocation.STREAM_CODEC.encode(buf, provider.id)
            },
            { buf ->
                val id = ResourceLocation.STREAM_CODEC.decode(buf)
                ContextRegistry.registry.keys.first { it.id == id }
            }
        )
    }
}

interface SyncableContext<T: Any>: ItemContext {
    val type: ContextType<T>
    val data: T
    val holder: ContextHolder<T> get() = ContextHolder(type, data)
}

data class ContextHolder<T: Any>(val type: ContextType<T>, val data: T) {
    fun getCtx(player: Player): SyncableContext<T> {
        return type.getCtx(player, data)
    }

    fun encode(buf: ByteBuf) {
        ContextType.codec.encode(type, buf)
        type.codec.encode(data, buf)
    }

    companion object {
        val codec: ByteCodec<ContextHolder<*>> = ByteCodec.passthrough(
            { buf, provider ->
                provider.encode(buf)
            },
            { buf ->
                val type = ContextType.codec.decode(buf)
                return@passthrough type.decode(buf)
            }
        )
    }
}

object ContextRegistry {
    val registry: Map<ContextType<*>, ContextProvider<*>>
        field = mutableMapOf<ContextType<*>, ContextProvider<*>>()

    fun <T: Any> register(type: ContextType<T>, provider: ContextProvider<T>) {
        registry[type] = provider
    }

    fun <T : Any> get(type: ContextType<T>, player: Player, context: T): SyncableContext<T> {
        val thing = registry[type] ?: error("No provider for $type")
        val finalThing = thing as ContextProvider<T>
        return finalThing(player, context)
    }
}