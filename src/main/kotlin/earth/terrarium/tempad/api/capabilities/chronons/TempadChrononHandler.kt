package earth.terrarium.tempad.api.capabilities.chronons

import earth.terrarium.tempad.common.registries.ModComponents
import earth.terrarium.tempad.common.registries.chrononContentTempad
import earth.terrarium.tempad.common.registries.chrononContentTimeTwister
import earth.terrarium.tempad.common.registries.twisterEquipped
import net.minecraft.world.item.ItemStack
import net.neoforged.neoforge.transfer.access.ItemAccess
import net.neoforged.neoforge.transfer.energy.SimpleEnergyHandler
import net.neoforged.neoforge.transfer.transaction.Transaction

class TempadChrononHandler(val access: ItemAccess, val tempadLimit: Int, timeTwisterLimit: Int): SimpleEnergyHandler(tempadLimit + if(access.resource.twisterEquipped) timeTwisterLimit else 0) {
    companion object {
        fun create(stack: ItemAccess, tempadLimit: Int, timeTwisterLimit: Int): TempadChrononHandler? {
            if (tempadLimit + timeTwisterLimit <= 0) return null
            return TempadChrononHandler(stack, tempadLimit, timeTwisterLimit)
        }
    }

    init {
        this.energy = access.resource.chrononContentTempad + access.resource.chrononContentTimeTwister
    }

    override fun onEnergyChanged(previousAmount: Int) {
        // put in tempad first, then time twister
        val diff = previousAmount - energy
        if (diff == 0) return
        // get the difference, put it in tempad first
        val tempadDiff = diff.coerceAtMost(tempadLimit - access.resource.chrononContentTempad)
        Transaction.openRoot().use {
            access.exchange(
                access.resource
                    .with(ModComponents.chrononContentTempad, access.resource.chrononContentTempad + tempadDiff)
                    .with(ModComponents.chrononContentTimeTwister, access.resource.chrononContentTimeTwister + diff - tempadDiff),
                1,
                it
            )
            it.commit()
        }
    }
}