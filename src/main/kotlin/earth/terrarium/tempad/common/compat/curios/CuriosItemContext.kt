package earth.terrarium.tempad.common.compat.curios

import com.teamresourceful.bytecodecs.base.ByteCodec
import com.teamresourceful.bytecodecs.base.`object`.ObjectByteCodec
import earth.terrarium.tempad.Tempad.Companion.tempadId
import earth.terrarium.tempad.api.context.ItemContext
import earth.terrarium.tempad.api.context.ContextType
import earth.terrarium.tempad.common.items.TempadItem
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStack
import top.theillusivec4.curios.api.CuriosApi
import top.theillusivec4.curios.api.SlotContext
import kotlin.jvm.optionals.getOrNull

class CuriosItemContext(val slotType: String, val index: Int) : ItemContext {
    override val type: ContextType<*> = Companion.type

    override fun getStack(player: Player): ItemStack {
        return CuriosApi.getCuriosInventory(player).orElseThrow().curios[slotType]!!.stacks.getStackInSlot(index)
    }

    override fun setStack(player: Player, stack: ItemStack) {
        CuriosApi.getCuriosInventory(player).orElseThrow().curios[slotType]!!.stacks.setStackInSlot(index, stack)
    }

    override fun isLocked(slot: Slot, player: Player): Boolean = false

    companion object {
        val codec: ByteCodec<CuriosItemContext> = ObjectByteCodec.create(
            ByteCodec.STRING.fieldOf(CuriosItemContext::slotType),
            ByteCodec.INT.fieldOf(CuriosItemContext::index),
            ::CuriosItemContext
        )
        val id: ResourceLocation = "curios".tempadId

        val type = ContextType(id, 10, codec) ctx@ { player ->
            val result = CuriosApi.getCuriosInventory(player).orElseThrow().findFirstCurio { it.item is TempadItem }
            val slotContext = result.getOrNull()?.slotContext ?: return@ctx null
            return@ctx CuriosItemContext(slotContext.identifier, slotContext.index)
        }
    }
}
