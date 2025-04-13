package earth.terrarium.tempad.api.tva_device.impl

import earth.terrarium.tempad.api.ActionType
import earth.terrarium.tempad.api.tva_device.ChrononHandler
import earth.terrarium.tempad.common.registries.metronomeEnergy
import java.util.UUID

class MultiversalChrononHandler(val playerId: UUID): ChrononHandler {
    override val power: Int
        get() = metronomeEnergy?.getStored(playerId) ?: 0
    override val maxPower: Int
        get() = metronomeEnergy?.getCapacity(playerId) ?: 0

    override fun extract(amount: Int, action: ActionType): Int {
        val oldPower = power
        val newPower = (power - amount).coerceAtLeast(0)
        if (action == ActionType.Execute) {
            metronomeEnergy?.stored[playerId] = newPower
            metronomeEnergy?.sync()
        }
        return oldPower - newPower
    }

    override fun insert(amount: Int, action: ActionType): Int {
        val oldPower = power
        val newPower = (power + amount).coerceAtMost(maxPower)
        if (action == ActionType.Execute) {
            metronomeEnergy?.stored[playerId] = newPower
            metronomeEnergy?.sync()
        }
        return newPower - oldPower
    }
}