package earth.terrarium.tempad.common.compat.curios

import earth.terrarium.tempad.api.context.ItemContext

fun initCuriosCompat() {
    ItemContext.register(CuriosItemContext.type)
}