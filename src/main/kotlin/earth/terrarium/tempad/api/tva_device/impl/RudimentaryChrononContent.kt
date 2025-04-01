package earth.terrarium.tempad.api.tva_device.impl

import earth.terrarium.tempad.api.ActionType
import earth.terrarium.tempad.api.tva_device.ChrononHandler
import earth.terrarium.tempad.common.block.RudimentaryTempadBE
import net.minecraft.util.Mth
import net.minecraft.world.level.block.Block

class RudimentaryChrononContent(val block: RudimentaryTempadBE, override val maxPower: Int): ChrononHandler {
    override var power: Int by block::chrononContent

    override fun extract(amount: Int, action: ActionType): Int {
        val extracted = Mth.clamp(power, 0, amount)
        if(action == ActionType.Execute) {
            power -= extracted
            block.setChanged()
            block.level!!.sendBlockUpdated(block.blockPos, block.blockState, block.blockState, Block.UPDATE_ALL)
        }
        return extracted
    }

    override fun insert(amount: Int, action: ActionType): Int {
        val inserted = Mth.clamp(amount, 0, maxPower - power)
        if(action == ActionType.Execute) {
            power += inserted
            block.setChanged()
            block.level!!.sendBlockUpdated(block.blockPos, block.blockState, block.blockState, Block.UPDATE_ALL)
        }
        return inserted
    }
}