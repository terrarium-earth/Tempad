package earth.terrarium.tempad.api.tva_device.impl

import earth.terrarium.tempad.api.ActionType
import earth.terrarium.tempad.api.tva_device.ChrononHandler
import earth.terrarium.tempad.common.registries.chrononContent
import net.minecraft.util.Mth
import net.minecraft.world.level.block.entity.BlockEntity

class BlockChrononHandler(block: BlockEntity, override val maxPower: Int): ChrononHandler {
    override var power: Int by block::chrononContent

    override fun extract(amount: Int, action: ActionType): Int {
        val extracted = Mth.clamp(power, 0, amount)
        if(action == ActionType.Execute) power -= extracted
        return extracted
    }

    override fun insert(amount: Int, action: ActionType): Int {
        val inserted = Mth.clamp(amount, 0, maxPower - power)
        if(action == ActionType.Execute) power += inserted
        return inserted
    }
}