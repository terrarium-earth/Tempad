package earth.terrarium.tempad.api.context

import com.teamresourceful.bytecodecs.base.ByteCodec
import earth.terrarium.tempad.api.PriorityId
import io.netty.buffer.ByteBuf
import io.netty.handler.codec.DecoderException
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack

typealias ContextProvider<T> = (Player, T) -> SyncableContext<T>

/**
 * Server-side check that [T] (usually received from a client) refers to something the player may legitimately use.
 * Must not load chunks or otherwise do expensive work: it runs for every C2S packet carrying a context.
 */
typealias ContextValidator<T> = (Player, T) -> Boolean

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
                ContextRegistry.registry.keys.firstOrNull { it.id == id }
                    ?: throw DecoderException("Unknown tempad context type: $id")
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
    /**
     * Resolves the context WITHOUT validation. Only use for contexts the server built itself
     * (or on the client). For anything decoded from a C2S packet use [resolve].
     */
    fun getCtx(player: Player): SyncableContext<T> {
        return type.getCtx(player, data)
    }

    /**
     * Validates a client-supplied context and resolves it. Returns null if the context data is invalid for this
     * player (out of range slot, unloaded / out of reach / locked block, ...) or if the item it points at does not
     * match [expected].
     */
    fun resolve(player: Player, expected: (ItemStack) -> Boolean): SyncableContext<T>? {
        if (!ContextRegistry.isValid(type, player, data)) return null
        val ctx = type.getCtx(player, data)
        return if (expected(ctx.stack)) ctx else null
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

typealias ContextLocator = (Player, (ItemStack) -> Boolean) -> SyncableContext<*>?

object ContextRegistry {
    val registry: Map<ContextType<*>, ContextProvider<*>>
        field = mutableMapOf()

    val locatorRegistry: Map<PriorityId, ContextLocator>
        field = sortedMapOf()

    private val validators = mutableMapOf<ContextType<*>, ContextValidator<*>>()

    /**
     * @param validator see [ContextValidator]. Defaults to accepting everything for backwards compatibility with
     * addons; built-in context types always register a real validator.
     */
    @JvmOverloads
    fun <T: Any> register(type: ContextType<T>, provider: ContextProvider<T>, validator: ContextValidator<T> = { _, _ -> true }) {
        registry[type] = provider
        validators[type] = validator
    }

    @Suppress("UNCHECKED_CAST")
    fun <T: Any> isValid(type: ContextType<T>, player: Player, data: T): Boolean {
        val validator = validators[type] as? ContextValidator<T> ?: return false
        return try {
            validator(player, data)
        } catch (e: RuntimeException) {
            false
        }
    }

    fun registerLocator(id: PriorityId, locator: ContextLocator) {
        locatorRegistry[id] = locator
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : Any> get(type: ContextType<T>, player: Player, context: T): SyncableContext<T> {
        val thing = registry[type] ?: error("No provider for $type")
        val finalThing = thing as ContextProvider<T>
        return finalThing(player, context)
    }

    fun locate(player: Player, predicate: (ItemStack) -> Boolean): SyncableContext<*>? {
        for ((_, locator) in locatorRegistry) {
            val context = locator(player, predicate)
            if(context != null) {
                return context
            }
        }
        return null
    }
}