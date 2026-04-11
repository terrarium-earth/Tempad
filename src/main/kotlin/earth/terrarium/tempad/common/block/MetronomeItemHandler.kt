package earth.terrarium.tempad.common.block

import earth.terrarium.tempad.api.tva_device.chronons
import net.neoforged.neoforge.transfer.item.ItemResource
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler

class MetronomeItemHandler: ItemStacksResourceHandler(8) {
    override fun isValid(index: Int, resource: ItemResource): Boolean {
        val stackVer = resource.toStack()
        return index < 4 && stackVer.chronons?.canExtract == true || index >= 4 && stackVer.chronons?.canInsert == true
    }
}