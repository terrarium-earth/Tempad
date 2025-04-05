package earth.terrarium.tempad.common.menu

import com.teamresourceful.bytecodecs.base.ByteCodec
import com.teamresourceful.resourcefullib.common.menu.MenuContent
import com.teamresourceful.resourcefullib.common.menu.MenuContentSerializer
import earth.terrarium.tempad.common.registries.ModMenus
import earth.terrarium.tempad.common.utils.RecordCodecMenuContentSerializer
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStack
import java.util.Optional
import java.util.UUID

class MetronomeMenu(id: Int, inv: Inventory, data: Optional<MetronomeMenuData>) : AbstractContainerMenu(ModMenus.METRONOME_MENU, id) {
    init {
        this.addPlayerInvSlots(inv)
    }

    override fun quickMoveStack(
        player: Player,
        index: Int,
    ): ItemStack {
        TODO("Not yet implemented")
    }

    override fun stillValid(player: Player): Boolean {
        TODO("Not yet implemented")
    }

    private fun addPlayerInvSlots(inventory: Inventory, x: Int = 48, y: Int = 157) {
        for (row in 0..2) {
            for (column in 0..8) {
                this.addSlot(Slot(inventory, column + row * 9 + 9 /* Hotbar is the first 9 */, x + column * 18, y + row * 18))
            }
        }

        for (k in 0..8) {
            this.addSlot(Slot(inventory, k, x + k * 18, y + 18 * 3 + 4))
        }
    }
}

data class MetronomeMenuData(val uuid: UUID): MenuContent<MetronomeMenuData> {
    override fun serializer(): MenuContentSerializer<MetronomeMenuData> = serializer

    companion object {
        val byteCodec = ByteCodec.UUID.map(::MetronomeMenuData) { it.uuid }
        val serializer = RecordCodecMenuContentSerializer(byteCodec)
    }
}