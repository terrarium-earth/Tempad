package earth.terrarium.tempad.api.fuel

import earth.terrarium.tempad.common.utils.get
import earth.terrarium.tempad.common.utils.set
import net.minecraft.world.Container
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack

class PlayerContainerContext(override val player: Player, private val container: Container, val slot: Int): ItemContext {
    constructor(player: Player, slot: Int) : this(player, player.inventory, slot)

    override val level = player.level()

    override var item: ItemStack
        get() = container[slot]
        set(value) {
            container[slot] = value
        }

    override fun insertOverflow(overflow: ItemStack) {
        player.inventory.placeItemBackInInventory(overflow)
    }
}