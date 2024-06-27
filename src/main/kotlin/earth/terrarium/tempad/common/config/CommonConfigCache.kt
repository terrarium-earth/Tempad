package earth.terrarium.tempad.common.config

import earth.terrarium.tempad.common.registries.ModNetworking

object CommonConfigCache {
    val CACHE = ConfigCache("tempad", ModNetworking.CHANNEL)

    val allow_interdimensional_travel by CACHE.ofBoolean(CommonConfig::allow_interdimensional_travel)
    val allow_exporting = CACHE.ofBoolean(CommonConfig::allow_exporting)

    object Tempad {
        val fuel_type by CACHE.ofString(CommonConfig.Tempad::fuel_type)
        val consume_amount by CACHE.ofInt(CommonConfig.Tempad::consume_amount)
        val capacity by CACHE.ofInt(CommonConfig.Tempad::capacity)
    }

    object AdvancedTempad {
        val fuel_type by CACHE.ofString(CommonConfig.AdvancedTempad::fuel_type)
        val consume_amount by CACHE.ofInt(CommonConfig.AdvancedTempad::consume_amount)
        val capacity by CACHE.ofInt(CommonConfig.AdvancedTempad::capacity)
    }
}