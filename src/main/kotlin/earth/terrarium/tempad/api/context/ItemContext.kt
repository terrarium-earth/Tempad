package earth.terrarium.tempad.api.context

import com.teamresourceful.bytecodecs.base.ByteCodec
import earth.terrarium.tempad.api.PriorityId
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStack
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class ContextType<T: ItemContext>(val id: ResourceLocation, val priority: Int, val codec: ByteCodec<T>, val tempadCtxGetter: (Player) -> T? = { null }) {
    val priorityId = PriorityId(id, priority)
}

interface ItemContext {
    val type: ContextType<*>

    fun stackDelegate(player: Player) = object: ReadWriteProperty<Any?, ItemStack> {
        override fun getValue(thisRef: Any?, property: KProperty<*>): ItemStack = getStack(player)
        override fun setValue(thisRef: Any?, property: KProperty<*>, value: ItemStack) = setStack(player, value)
    }

    fun getStack(player: Player): ItemStack
    fun setStack(player: Player, stack: ItemStack)

    fun isLocked(slot: Slot, player: Player): Boolean

    fun getInstance(player: Player) = ContextInstance(this, player)

    companion object {
        val registry = sortedMapOf<PriorityId, ContextType<*>>()

        val typeCodec: ByteCodec<ContextType<*>> = PriorityId.byteCodec.map({ registry[it]!! }) { it.priorityId }
        val codec: ByteCodec<ItemContext> = ByteCodec.passthrough(
            { buf, provider ->
                typeCodec.encode(provider.type, buf)
                (provider.type.codec as ByteCodec<ItemContext>).encode(provider, buf)
            },
            { buf ->
                val id = typeCodec.decode(buf)
                return@passthrough id.codec.decode(buf)
            }
        )

        fun register(provider: ContextType<*>) {
            registry[provider.priorityId] = provider
        }
    }
}

val Player.ctx: ItemContext?
    get() {
        for ((_, type) in ItemContext.registry) {
            val ctx = type.tempadCtxGetter(this)
            if (ctx != null) return ctx
        }
        return null
    }