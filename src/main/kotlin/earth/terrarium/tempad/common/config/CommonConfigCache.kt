package earth.terrarium.tempad.common.config

import earth.terrarium.tempad.common.registries.ModNetworking

object CommonConfigCache {
    val CACHE = ConfigCache("tempad", ModNetworking.channel)

    object Capacitor {
        val capacity by CACHE.ofInt(CommonConfig.Capacitor::capacityCapacitor)
    }

    object Battery {
        val capacity by CACHE.ofInt(CommonConfig.Battery::capacityBattery)
    }

    object ChrononGenerator {
        val capacity by CACHE.ofInt(CommonConfig.ChrononGenerator::capacitorGenerator)
    }

    object Chronometer {
        val capacity by CACHE.ofInt(CommonConfig.Chronometer::capacityChronometer)
    }

    object Metronome {
        val capacity by CACHE.ofInt(CommonConfig.Metronome::capacityMetronome)
    }

    object RudimentaryTempad {
        val capacity by CACHE.ofInt(CommonConfig.RudimentaryTempad::capacityRudi)
    }

    object Tempad {
        val capacity by CACHE.ofInt(CommonConfig.Tempad::capacityTempad)
    }

    object TimeTwister {
        val capacity by CACHE.ofInt(CommonConfig.TimeTwister::capacityTimeTwister)
    }
}