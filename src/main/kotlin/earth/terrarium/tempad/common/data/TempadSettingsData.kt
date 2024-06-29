package earth.terrarium.tempad.common.data

import earth.terrarium.tempad.common.apps.TeleportApp
import earth.terrarium.tempad.common.registries.ModApps
import earth.terrarium.tempad.common.registries.ModComponents
import earth.terrarium.tempad.common.registries.ModMacros
import earth.terrarium.tempad.common.utils.ComponentDelegate
import net.neoforged.neoforge.common.MutableDataComponentHolder

var MutableDataComponentHolder.defaultApp by ComponentDelegate(ModComponents.DEFAULT_APP, ModApps.TELEPORT)
var MutableDataComponentHolder.defaultMacro by ComponentDelegate(ModComponents.DEFAULT_MACRO, ModMacros.DEFAULT_MACRO_ID)