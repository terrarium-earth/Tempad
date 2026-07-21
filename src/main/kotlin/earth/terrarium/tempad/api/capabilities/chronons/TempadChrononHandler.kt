package earth.terrarium.tempad.api.capabilities.chronons

import earth.terrarium.tempad.common.registries.ModComponents
import earth.terrarium.tempad.common.registries.chrononContentTempad
import earth.terrarium.tempad.common.registries.chrononContentTimeTwister
import earth.terrarium.tempad.common.registries.twisterEquipped
import net.minecraft.world.item.ItemStack
import net.neoforged.neoforge.transfer.access.ItemAccess
import net.neoforged.neoforge.transfer.energy.ItemAccessEnergyHandler
import net.neoforged.neoforge.transfer.energy.SimpleEnergyHandler
import net.neoforged.neoforge.transfer.item.ItemResource
import net.neoforged.neoforge.transfer.transaction.Transaction

class TempadChrononHandler(val access: ItemAccess, val tempadLimit: Int, timeTwisterLimit: Int): ItemAccessEnergyHandler(access, ModComponents.chrononContentTempad, tempadLimit + if(access.resource.twisterEquipped) timeTwisterLimit else 0) {
    companion object {
        fun create(stack: ItemAccess, tempadLimit: Int, timeTwisterLimit: Int): TempadChrononHandler? {
            if (tempadLimit + timeTwisterLimit <= 0) return null
            return TempadChrononHandler(stack, tempadLimit, timeTwisterLimit)
        }
    }

    override fun getAmountFrom(accessResource: ItemResource): Int {
        return (access.resource.chrononContentTempad + access.resource.chrononContentTimeTwister).coerceIn(0, Int.MAX_VALUE)
    }

    override fun update(
        accessResource: ItemResource,
        newAmount: Int,
    ): ItemResource {
        // put in tempad first, then time twister
        val diff = newAmount - amountAsInt
        // get the difference, put it in tempad first
        val tempadDiff = diff.coerceAtMost(tempadLimit - access.resource.chrononContentTempad)
        return access.resource
            .with(ModComponents.chrononContentTempad, access.resource.chrononContentTempad + tempadDiff)
            .with(ModComponents.chrononContentTimeTwister, access.resource.chrononContentTimeTwister + diff - tempadDiff)
    }
}