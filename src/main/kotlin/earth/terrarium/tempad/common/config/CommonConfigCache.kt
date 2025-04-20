package earth.terrarium.tempad.common.config

import earth.terrarium.tempad.common.registries.ModNetworking

object CommonConfigCache {
    val CACHE = ConfigCache("tempad", ModNetworking.channel)

    fun init() {
        ChrononCell
        Battery
        ChrononGenerator
        Chronometer
        Metronome
        RudimentaryTempad
        Tempad
        TimeTwister
        ModNetworking.channel.register(CACHE.syncType)
    }

    object ChrononCell {
        val capacity by CACHE.ofInt(CommonConfig.Cell::capacityCapacitor)
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

    object Chronomark {
        val maxOffset by CACHE.ofInt(CommonConfig.Chronomark::maxOffsetChronomark)
    }

    object Metronome {
        val capacity by CACHE.ofInt(CommonConfig.Metronome::capacityMetronome)
        val scaleGeneration by CACHE.ofBoolean(CommonConfig.Metronome::scaleGeneration)
    }

    object RudimentaryTempad {
        val capacity by CACHE.ofInt(CommonConfig.RudimentaryTempad::capacityRudi)
    }

    object Tempad {
        val capacity by CACHE.ofInt(CommonConfig.Tempad::capacityTempad)

        val maxOffset by CACHE.ofInt(CommonConfig.Tempad::maxOffsetTempad)
    }

    object TimeTwister {
        val capacity by CACHE.ofInt(CommonConfig.TimeTwister::capacityTimeTwister)
    }
}