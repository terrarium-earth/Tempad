package earth.terrarium.tempad.api.tva_device.impl

import earth.terrarium.tempad.api.ActionType
import earth.terrarium.tempad.api.tva_device.ChrononHandler

object InfiniteChrononHandler: ChrononHandler {
    override val power: Int = -1
    override val maxPower: Int = -1

    override fun extract(amount: Int, action: ActionType): Int {
        return amount
    }

    override fun insert(amount: Int, action: ActionType): Int {
        return 0
    }
}