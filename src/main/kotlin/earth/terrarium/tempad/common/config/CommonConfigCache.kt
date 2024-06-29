package earth.terrarium.tempad.common.config

import earth.terrarium.tempad.common.registries.ModNetworking

object CommonConfigCache {
    val CACHE = ConfigCache("tempad", ModNetworking.CHANNEL)

    object Tempad {
        val fuelType by CACHE.ofString(CommonConfig.Tempad::fuelType)
        val consumeAmount by CACHE.ofInt(CommonConfig.Tempad::consumeAmount)
        val capacity by CACHE.ofInt(CommonConfig.Tempad::capacity)
        val cooldownTime by CACHE.ofInt(CommonConfig.Tempad::cooldownTime)
    }

    object AdvancedTempad {
        val fuelType by CACHE.ofString(CommonConfig.AdvancedTempad::fuelType)
        val consumeAmount by CACHE.ofInt(CommonConfig.AdvancedTempad::consumeAmount)
        val capacity by CACHE.ofInt(CommonConfig.AdvancedTempad::capacity)
        val cooldownTime by CACHE.ofInt(CommonConfig.AdvancedTempad::cooldownTime)
    }
}