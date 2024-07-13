package earth.terrarium.tempad.common.registries

import earth.terrarium.tempad.api.context.ItemContext
import earth.terrarium.tempad.api.context.impl.InventoryItemContext
import earth.terrarium.tempad.api.context.impl.StaticItemContext

object ModContext {
    fun init() {
        ItemContext.register(InventoryItemContext.type)
        ItemContext.register(StaticItemContext.type)
    }
}