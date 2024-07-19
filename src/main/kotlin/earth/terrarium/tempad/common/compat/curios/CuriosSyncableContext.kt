package earth.terrarium.tempad.common.compat.curios

import com.teamresourceful.bytecodecs.base.ByteCodec
import com.teamresourceful.bytecodecs.base.`object`.ObjectByteCodec
import earth.terrarium.tempad.Tempad.Companion.tempadId
import earth.terrarium.tempad.api.test.ContextRegistry
import earth.terrarium.tempad.api.test.ContextType
import earth.terrarium.tempad.api.test.SyncableContext
import earth.terrarium.tempad.common.compat.curios.CuriosSyncableContext.CuriosSlotInfo
import earth.terrarium.tempad.common.items.TempadItem
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStack
import top.theillusivec4.curios.api.CuriosApi
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler
import kotlin.jvm.optionals.getOrNull

class CuriosSyncableContext(val player: Player, override val data: CuriosSlotInfo) : SyncableContext<CuriosSlotInfo> {
    val inventory: MutableMap<String, ICurioStacksHandler> = CuriosApi.getCuriosInventory(player).orElseThrow().curios

    override val type: ContextType<CuriosSlotInfo> = Companion.type

    override var stack: ItemStack
        get() = inventory[data.identifier]!!.stacks.getStackInSlot(data.index)
        set(value) { inventory[data.identifier]!!.stacks.setStackInSlot(data.index, value) }

    override fun addStack(stack: ItemStack) {
        player.inventory.placeItemBackInInventory(stack)
    }

    companion object {
        val type = ContextType("curios".tempadId, CuriosSlotInfo.codec)
    }

    class CuriosSlotInfo(val identifier: String, val index: Int) {
        companion object {
            val codec: ByteCodec<CuriosSlotInfo> = ObjectByteCodec.create(
                ByteCodec.STRING.fieldOf(CuriosSlotInfo::identifier),
                ByteCodec.INT.fieldOf(CuriosSlotInfo::index),
                ::CuriosSlotInfo
            )
        }
    }
}

fun initCuriosCompat() {
    ContextRegistry.register(CuriosSyncableContext.type, ::CuriosSyncableContext)
}