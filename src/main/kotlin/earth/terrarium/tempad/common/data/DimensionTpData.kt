package earth.terrarium.tempad.common.data

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder

data class DimensionTpData(val distancePlateau: Int, val maxCost: Int) {
    companion object {
        val codec = RecordCodecBuilder.create<DimensionTpData> { instance ->
            instance.group(
                Codec.intRange(0, Int.MAX_VALUE).fieldOf("distancePlateau").forGetter { it.distancePlateau },
                Codec.intRange(0, Int.MAX_VALUE).fieldOf("maxCost").forGetter { it.maxCost }
            ).apply(instance, ::DimensionTpData)
        }
    }
}
