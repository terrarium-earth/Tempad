package earth.terrarium.tempad.api.context

import com.teamresourceful.resourcefullib.common.bytecodecs.ExtraByteCodecs
import earth.terrarium.tempad.common.block.WorkstationBE
import earth.terrarium.tempad.common.utils.get
import earth.terrarium.tempad.common.utils.set
import earth.terrarium.tempad.tempadId
import net.minecraft.core.BlockPos
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.Block

class WorkstationContext(val player: Player, override val data: BlockPos): SyncableContext<BlockPos> {
    override val type: ContextType<BlockPos> = Companion.type
    val workstation: WorkstationBE? get() = player.level().getBlockEntity(data) as? WorkstationBE

    override var stack: ItemStack
        get() = workstation?.inventory?.get(0) ?: ItemStack.EMPTY
        set(value) {
            val station = workstation ?: return
            station.inventory.set(0,value)
            station.setChanged()
            station.level?.sendBlockUpdated(station.blockPos, station.blockState, station.blockState, Block.UPDATE_ALL)
        }

    override fun addStack(stack: ItemStack) {
        player.inventory.placeItemBackInInventory(stack)
    }

    companion object {
        val type = ContextType("workstation".tempadId, ExtraByteCodecs.BLOCK_POS)
    }
}