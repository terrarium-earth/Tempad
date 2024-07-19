package earth.terrarium.tempad.common.registries

import earth.terrarium.tempad.api.test.ContextRegistry
import earth.terrarium.tempad.api.test.InventoryContext
import earth.terrarium.tempad.common.compat.curios.initCuriosCompat
import net.neoforged.fml.ModList

object ModContext {
    fun init() {
        ContextRegistry.register(InventoryContext.type, ::InventoryContext)
        if (ModList.get().isLoaded("curios")) {
            initCuriosCompat()
        }
    }
}