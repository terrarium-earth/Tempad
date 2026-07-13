package earth.terrarium.tempad.common.block

import earth.terrarium.tempad.api.capabilities.chronons
import net.neoforged.neoforge.transfer.IndexModifier
import net.neoforged.neoforge.transfer.access.ItemAccess
import net.neoforged.neoforge.transfer.item.ItemResource
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler

class MetronomeItemHandler: ItemStacksResourceHandler(8), IndexModifier<ItemResource> {
    override fun isValid(index: Int, resource: ItemResource): Boolean {
        val stackVer = ItemAccess.forStack(resource.toStack())
        return stackVer.chronons != null
    }
}