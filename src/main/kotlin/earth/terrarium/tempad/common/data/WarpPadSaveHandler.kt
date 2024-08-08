package earth.terrarium.tempad.common.data

import com.teamresourceful.resourcefullib.common.utils.SaveHandler
import earth.terrarium.tempad.api.locations.StaticNamedGlobalPos
import net.minecraft.nbt.CompoundTag
import java.util.UUID

class WarpPadSaveHandler: SaveHandler() {
    val warps: MutableMap<UUID, StaticNamedGlobalPos> = mutableMapOf()

    override fun loadData(p0: CompoundTag) {
        TODO("Not yet implemented")
    }

    override fun saveData(p0: CompoundTag) {
        TODO("Not yet implemented")
    }
}