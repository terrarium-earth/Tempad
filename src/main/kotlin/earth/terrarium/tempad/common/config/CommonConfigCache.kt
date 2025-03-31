package earth.terrarium.tempad.common.config

import earth.terrarium.tempad.common.registries.ModNetworking

object CommonConfigCache {
    val CACHE = ConfigCache("tempad", ModNetworking.channel)

    object Capacitor {
        val capacity by CACHE.ofInt(CommonConfig.Capacitor::capacity)
    }

    object Chronometer {
        val capacity by CACHE.ofInt(CommonConfig.Chronometer::capacity)
    }

    object RudimentaryTempad {
        val capacity by CACHE.ofInt(CommonConfig.RudimentaryTempad::capacity)
    }

    object Tempad {
        val capacity by CACHE.ofInt(CommonConfig.Tempad::capacity)
    }

    object TimeTwister {
        val capacity by CACHE.ofInt(CommonConfig.TimeTwister::capacity)
    }
}