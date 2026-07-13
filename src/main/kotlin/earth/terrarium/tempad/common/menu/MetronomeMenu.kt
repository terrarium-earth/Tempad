package earth.terrarium.tempad.common.menu

import com.teamresourceful.bytecodecs.base.ByteCodec
import com.teamresourceful.bytecodecs.base.`object`.ObjectByteCodec
import com.teamresourceful.resourcefullib.common.bytecodecs.ExtraByteCodecs
import com.teamresourceful.resourcefullib.common.menu.MenuContent
import com.teamresourceful.resourcefullib.common.menu.MenuContentSerializer
import earth.terrarium.tempad.common.block.MetronomeItemHandler
import earth.terrarium.tempad.common.registries.ModMenus
import earth.terrarium.tempad.common.utils.RecordCodecMenuContentSerializer
import net.minecraft.core.GlobalPos
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.ContainerData
import net.minecraft.world.inventory.SimpleContainerData
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStack
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler
import net.neoforged.neoforge.transfer.item.ResourceHandlerSlot
import java.util.*
import kotlin.jvm.optionals.getOrNull

class MetronomeMenu(
    id: Int,
    inv: Inventory,
    items: MetronomeItemHandler,
    val energy: ContainerData,
    val data: MetronomeMenuData?,
) : AbstractContainerMenu(ModMenus.metronome, id) {
    constructor(id: Int, inv: Inventory, data: Optional<MetronomeMenuData>) : this(
        id, inv,
        MetronomeItemHandler(), SimpleContainerData(2), data.getOrNull()
    )

    init {
        this.addMenuSlots(items)
        this.addPlayerInvSlots(inv)
        this.addDataSlots(energy)
    }

    override fun quickMoveStack(
        player: Player,
        index: Int,
    ): ItemStack {
        var newStack = ItemStack.EMPTY
        val slot = this.slots[index]
        if (slot.hasItem()) {
            val originalStack = slot.item
            newStack = originalStack.copy()
            if (index < 8) {
                if (!this.moveItemStackTo(originalStack, 8, this.slots.size, true)) {
                    return ItemStack.EMPTY
                }
            } else if (!this.moveItemStackTo(originalStack, 0, 8, false)) {
                return ItemStack.EMPTY
            }

            if (originalStack.isEmpty) {
                slot.set(ItemStack.EMPTY)
            } else {
                slot.setChanged()
            }
        }
        return newStack
    }

    override fun stillValid(player: Player): Boolean = true

    private fun addPlayerInvSlots(inventory: Inventory, x: Int = 25, y: Int = 85) {
        for (row in 0..2) {
            for (column in 0..8) {
                this.addSlot(
                    Slot(
                        inventory,
                        column + row * 9 + 9 /* Hotbar is the first 9 */,
                        x + column * 18,
                        y + row * 18
                    )
                )
            }
        }

        for (k in 0..8) {
            this.addSlot(Slot(inventory, k, x + k * 18, y + 18 * 3 + 4))
        }
    }

    private fun addMenuSlots(items: MetronomeItemHandler, x: Int = 25, y: Int = 10) {
        for (slot in 0..7) {
            this.addSlot(ResourceHandlerSlot(items, items, slot, x + (slot + if (slot > 3) 1 else 0) * 18, y + 30))
        }
    }
}

data class MetronomeMenuData(val uuid: UUID, val pos: GlobalPos, val locked: Boolean) : MenuContent<MetronomeMenuData> {
    override fun serializer(): MenuContentSerializer<MetronomeMenuData> = serializer

    companion object {
        val byteCodec = ObjectByteCodec.create(
            ByteCodec.UUID.fieldOf(MetronomeMenuData::uuid),
            ExtraByteCodecs.GLOBAL_POS.fieldOf(MetronomeMenuData::pos),
            ByteCodec.BOOLEAN.fieldOf(MetronomeMenuData::locked),
            ::MetronomeMenuData
        )
        val serializer = RecordCodecMenuContentSerializer(byteCodec)
    }
}