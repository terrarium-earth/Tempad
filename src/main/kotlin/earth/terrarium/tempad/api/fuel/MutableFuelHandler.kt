package earth.terrarium.tempad.api.fuel

import net.minecraft.world.entity.player.Player

interface MutableFuelHandler: FuelHandler {
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("addCharges")
    operator fun plusAssign(amount: Int)

    fun addCharge() {
        this += 1
    }

    fun addChargeFromPlayer(player: Player): Boolean {
        if (player.abilities.instabuild) {
            this += 1
            return true
        }
        for (i in 0 until player.inventory.containerSize) {
            if(addChargeFromItem(PlayerContainerContext(player, i))) {
                return true
            }
        }
        return false
    }

    fun addChargeFromItem(context: ItemContext): Boolean
}