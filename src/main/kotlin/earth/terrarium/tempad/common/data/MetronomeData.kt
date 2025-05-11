package earth.terrarium.tempad.common.data

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import com.teamresourceful.bytecodecs.base.ByteCodec
import com.teamresourceful.bytecodecs.base.`object`.ObjectByteCodec
import com.teamresourceful.resourcefullib.common.bytecodecs.ExtraByteCodecs
import earth.terrarium.tempad.Tempad
import earth.terrarium.tempad.common.config.CommonConfig
import earth.terrarium.tempad.common.config.CommonConfigCache
import earth.terrarium.tempad.common.registries.ModAttachments
import earth.terrarium.tempad.common.utils.syncData
import it.unimi.dsi.fastutil.objects.Object2IntMap
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap
import net.minecraft.core.GlobalPos
import net.minecraft.core.UUIDUtil
import java.util.UUID

class MetronomeData(stored: Map<UUID, Int>, positions: Map<UUID, List<GlobalPos>>) {
    companion object {
        val codec: Codec<MetronomeData> = RecordCodecBuilder.create {
            it.group(
                Codec.unboundedMap(UUIDUtil.STRING_CODEC, Codec.INT).fieldOf("stored").forGetter { it.stored },
                Codec.unboundedMap(UUIDUtil.STRING_CODEC, GlobalPos.CODEC.listOf()).fieldOf("positions").forGetter { it.positions }
            ).apply(it, ::MetronomeData)
        }
    }

    val positions: MutableMap<UUID, MutableList<GlobalPos>> = mutableMapOf()
    val stored: Object2IntMap<UUID> = Object2IntOpenHashMap()

    init {
        for ((player, blocks) in positions) {
            this.positions.put(player, blocks.toMutableList())
        }

        this.stored.putAll(stored)
    }

    fun add(player: UUID, pos: GlobalPos, contents: Int) {
        if (positions[player] != null && pos in positions[player]!!) return
        positions.getOrPut(player) { mutableListOf() } += pos
        stored[player] = contents + (stored[player] ?: 0)
    }

    fun getCount(player: UUID): Int {
        return positions[player]?.size ?: 0
    }

    fun getCapacity(player: UUID): Int {
        return (positions[player]?.size ?: 0) * CommonConfigCache.Metronome.capacity
    }

    fun getStored(player: UUID): Int {
        return stored[player] ?: 0
    }

    fun tick() {
        val test = Object2IntOpenHashMap<String>();
        for ((player, amount) in stored) {
            stored[player] = (CommonConfig.Metronome.generationAmount * (if(CommonConfigCache.Metronome.scaleGeneration) getCount(player) else 1) + amount).coerceAtMost(getCapacity(player))
        }
    }

    fun remove(player: UUID, pos: GlobalPos): Int {
        if (getCapacity(player) == 0) return 0
        return stored[player]?.let {
            val amount = it / getCount(player)
            stored[player] = it - amount
            positions.getOrPut(player) { mutableListOf() } -= pos
            return amount
        } ?: 0
    }
}