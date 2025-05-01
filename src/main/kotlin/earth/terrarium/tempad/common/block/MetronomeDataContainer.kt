package earth.terrarium.tempad.common.block

import earth.terrarium.tempad.common.registries.metronomeEnergy
import net.minecraft.world.inventory.ContainerData
import java.util.UUID

class MetronomeDataContainer(val owner: UUID): ContainerData {
    override fun get(index: Int): Int {
        return when (index) {
            0 -> metronomeEnergy?.getStored(owner) ?: 0
            1 -> metronomeEnergy?.getCapacity(owner) ?: 0
            else -> return 0
        }
    }

    override fun set(index: Int, value: Int) {
    }

    override fun getCount(): Int = 2
}