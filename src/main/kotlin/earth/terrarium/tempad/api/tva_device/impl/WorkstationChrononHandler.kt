package earth.terrarium.tempad.api.tva_device.impl

import earth.terrarium.tempad.api.ActionType
import earth.terrarium.tempad.api.tva_device.ChrononHandler
import earth.terrarium.tempad.api.tva_device.chronons
import earth.terrarium.tempad.common.block.WorkstationBE
import earth.terrarium.tempad.common.registries.chrononContent
import earth.terrarium.tempad.common.utils.get
import net.minecraft.world.level.block.Block

class WorkstationChrononHandler(val block: WorkstationBE): ChrononHandler {
    val handler: ChrononHandler? by block.inventory[0]::chronons

    override val power: Int by handler!!::power
    override val maxPower: Int by handler!!::maxPower

    override fun extract(amount: Int, action: ActionType): Int {
        val changed = handler!!.extract(amount, action)
        if (action == ActionType.Execute) {
            block.setChanged()
            block.level!!.sendBlockUpdated(block.blockPos, block.blockState, block.blockState, Block.UPDATE_ALL)
        }
        return changed
    }

    override fun insert(amount: Int, action: ActionType): Int {
        val changed = handler!!.insert(amount, action)
        if (action == ActionType.Execute) {
            block.setChanged()
            block.level!!.sendBlockUpdated(block.blockPos, block.blockState, block.blockState, Block.UPDATE_ALL)
        }
        return changed
    }
}