package earth.terrarium.tempad.common.data

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import com.teamresourceful.bytecodecs.base.ByteCodec
import com.teamresourceful.bytecodecs.base.`object`.ObjectByteCodec
import com.teamresourceful.resourcefullib.common.bytecodecs.ExtraByteCodecs
import earth.terrarium.tempad.tempadId
import earth.terrarium.tempad.api.locations.StaticNamedGlobalPos
import earth.terrarium.tempad.common.config.CommonConfig
import earth.terrarium.tempad.common.utils.*
import net.minecraft.core.BlockPos
import net.minecraft.core.GlobalPos
import net.minecraft.core.UUIDUtil
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.level.Level
import net.minecraft.world.level.portal.DimensionTransition
import net.minecraft.world.phys.Vec3
import java.util.*
import kotlin.jvm.optionals.getOrNull

class HistoricalLocation(val marker: ResourceLocation?, val dimension: ResourceKey<Level>, val pos: Vec3) {
    constructor(marker: Optional<ResourceLocation>, dimension: ResourceKey<Level>, pos: Vec3) : this(marker.getOrNull(), dimension, pos)

    companion object {
        val CODEC: Codec<HistoricalLocation> = RecordCodecBuilder.create { instance ->
            instance.group(
                ResourceLocation.CODEC.optionalFieldOf("marker").nullableGetter(HistoricalLocation::marker),
                Level.RESOURCE_KEY_CODEC.fieldOf("dimension").forGetter { it.dimension },
                Vec3.CODEC.fieldOf("pos").forGetter { it.pos }
            ).apply(instance, ::HistoricalLocation)
        }

        val BYTE_CODEC: ByteCodec<HistoricalLocation> = ObjectByteCodec.create(
            ExtraByteCodecs.RESOURCE_LOCATION.nullableFieldOf { it.marker },
            ExtraByteCodecs.DIMENSION.fieldOf { it.dimension },
            VEC3_BYTE_CODEC.fieldOf { it.pos },
            ::HistoricalLocation
        )
    }

}

class TravelHistoryAttachment(val history: MutableMap<Date, HistoricalLocation>): Map<Date, HistoricalLocation> by history {
    companion object {
        val CODEC = (DATE_CODEC to HistoricalLocation.CODEC)
            .xmap({ TravelHistoryAttachment(it.toMutableMap()) }, TravelHistoryAttachment::history)

        val BYTE_CODEC = (DATE_BYTE_CODEC to HistoricalLocation.BYTE_CODEC)
            .map({ TravelHistoryAttachment(it.toMutableMap()) }, TravelHistoryAttachment::history)

        val DIM_EXIT_MARKER = "dimension_exit".tempadId
        val DIM_ENTER_MARKER = "dimension_enter".tempadId

        val DEATH_MARKER = "death".tempadId
        val RESPAWN_MARKER = "respawn".tempadId
    }

    constructor() : this(mutableMapOf())

    operator fun plusAssign(historicalLocation: HistoricalLocation) {
        history[Date()] = historicalLocation
    }

    fun logLocation(entity: LivingEntity, marker: ResourceLocation? = null) {
        if (history.isEmpty() || marker != null) {
            this += HistoricalLocation(marker, entity.level().dimension(), entity.pos)
            if (history.size > CommonConfig.maxHistorySize) history.remove(history.keys.first())
            return
        }

        val (_, lastPos) = history.entries.last()
        if (entity.level().dimension() == lastPos.dimension && entity.pos.distanceToSqr(lastPos.pos) < 64) return
        if (history.size > CommonConfig.maxHistorySize) history.remove(history.keys.first())
        this += HistoricalLocation(null, entity.level().dimension(), entity.pos)
    }

    /**
     * Backtrack to a specific location in the history, and removes all locations after it.
     */
    fun backtrackTo(entity: LivingEntity, time: Date) {
        val index = history.keys.indexOf(time)
        val historicalLocation = history[time]
        if (index == -1 || historicalLocation == null) return
        val ids = mutableListOf<Date>()
        for ((idIndex, entryId) in history.keys.withIndex()) {
            if (idIndex >= index) ids.add(entryId)
        }
        for (id in ids) history.remove(id)
        entity.changeDimension(DimensionTransition(entity.server.get(historicalLocation.dimension)!!, historicalLocation.pos, Vec3.ZERO, 0.0F, 0.0F, false, DimensionTransition.DO_NOTHING))
    }

    val relevantHistory get() = history.filter { it.value.marker != null }
}