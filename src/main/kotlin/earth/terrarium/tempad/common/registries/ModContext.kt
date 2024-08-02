package earth.terrarium.tempad.common.registries

import earth.terrarium.tempad.api.context.ContextRegistry
import earth.terrarium.tempad.api.context.InventoryContext
import earth.terrarium.tempad.common.compat.curios.initCuriosCompat
import earth.terrarium.tempad.common.menu.CarriedMenuCtx
import net.neoforged.fml.ModList

object ModContext {
    fun init() {
        ContextRegistry.register(InventoryContext.type, ::InventoryContext)
        ContextRegistry.register(CarriedMenuCtx.type) { player, _ -> CarriedMenuCtx(player) }
        if (ModList.get().isLoaded("curios")) {
            initCuriosCompat()
        }
    }
}