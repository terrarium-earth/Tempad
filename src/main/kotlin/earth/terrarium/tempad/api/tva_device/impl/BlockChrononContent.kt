package earth.terrarium.tempad.api.tva_device.impl

import earth.terrarium.tempad.api.ActionType
import earth.terrarium.tempad.api.tva_device.ChrononHandler
import earth.terrarium.tempad.common.block.MetronomeBe
import earth.terrarium.tempad.common.block.RudimentaryTempadBE
import net.minecraft.util.Mth
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntity

class BlockChrononContent(val block: BlockEntity, val setter: (Int) -> Unit, val getter: () -> Int, override val maxPower: Int): ChrononHandler {
    companion object {
        fun projector(block: RudimentaryTempadBE, maxPower: Int): BlockChrononContent? {
            if (maxPower <= 0) return null
            return BlockChrononContent(block, { block.chrononContent = it }, { block.chrononContent }, maxPower)
        }

        fun metronome(block: MetronomeBe, maxPower: Int): BlockChrononContent? {
            if (maxPower <= 0) return null
            return BlockChrononContent(block, { block.bootChronons = it }, { block.bootChronons }, maxPower)
        }

        fun metronomeClient(block: MetronomeBe): BlockChrononContent? {
            return BlockChrononContent(block, { block.localChronons = it }, { block.localChronons }, block.localCapacity)
        }
    }

    override var power: Int
        get() = getter()
        set(value) = setter(value)

    override val canExtract: Boolean = false

    override fun extract(amount: Int, action: ActionType): Int {
        val extracted = Mth.clamp(power, 0, amount)
        if(action == ActionType.Execute) {
            power -= extracted
            block.setChanged()
            block.level?.sendBlockUpdated(block.blockPos, block.blockState, block.blockState, Block.UPDATE_ALL)
        }
        return extracted
    }

    override fun insert(amount: Int, action: ActionType): Int {
        val inserted = Mth.clamp(amount, 0, maxPower - power)
        if(action == ActionType.Execute) {
            power += inserted
            block.setChanged()
            block.level?.sendBlockUpdated(block.blockPos, block.blockState, block.blockState, Block.UPDATE_ALL)
        }
        return inserted
    }
}