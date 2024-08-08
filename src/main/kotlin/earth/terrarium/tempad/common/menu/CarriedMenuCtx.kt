package earth.terrarium.tempad.common.menu

import com.teamresourceful.bytecodecs.base.ByteCodec
import earth.terrarium.tempad.tempadId
import earth.terrarium.tempad.api.context.ContextType
import earth.terrarium.tempad.api.context.SyncableContext
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack

class CarriedMenuCtx(val player: Player) : SyncableContext<Unit> {
    override val type: ContextType<Unit> = Companion.type
    override val data: Unit = Unit
    override var stack: ItemStack
        get() = player.containerMenu?.carried ?: ItemStack.EMPTY
        set(value) {
            player.containerMenu?.carried = value
        }

    override fun addStack(stack: ItemStack) {
        player.inventory.placeItemBackInInventory(stack)
    }

    companion object {
        val type = ContextType<Unit>("menu_carried_item".tempadId, ByteCodec.unit(Unit))
    }
}

val Player.menuCtx: CarriedMenuCtx get() = CarriedMenuCtx(this)