package earth.terrarium.tempad.common.data

import earth.terrarium.tempad.common.registries.ModComponents
import earth.terrarium.tempad.common.utils.ComponentDelegate
import net.neoforged.neoforge.common.MutableDataComponentHolder

var MutableDataComponentHolder.fuelCharges by ComponentDelegate(ModComponents.FUEL_CHARGES, 0)