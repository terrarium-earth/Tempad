package earth.terrarium.tempad.common.registries

import com.teamresourceful.resourcefullibkt.common.createRegistry
import com.teamresourceful.resourcefullibkt.common.getValue
import earth.terrarium.tempad.Tempad
import earth.terrarium.tempad.common.items.TempadItem
import net.minecraft.core.registries.BuiltInRegistries

object ModItems {
    val REGISTRY = BuiltInRegistries.ITEM.createRegistry(Tempad.MOD_ID)

    val TEMPAD by REGISTRY.register("tempad") {
        TempadItem()
    }
}