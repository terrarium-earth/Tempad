package earth.terrarium.tempad.common.data

import com.google.common.collect.HashMultimap
import com.google.common.collect.SetMultimap
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
                Codec.unboundedMap(UUIDUtil.STRING_CODEC, GlobalPos.CODEC.listOf()).fieldOf("positions").forGetter { it.storePositions }
            ).apply(it, ::MetronomeData)
        }

        val byteCodec: ByteCodec<MetronomeData> = ObjectByteCodec.create(
            ByteCodec.mapOf(ByteCodec.UUID, ByteCodec.INT).fieldOf { it.stored },
            ByteCodec.mapOf(ByteCodec.UUID, ExtraByteCodecs.GLOBAL_POS.listOf()).fieldOf { it.storePositions },
            ::MetronomeData
        )
    }

    val positions: SetMultimap<UUID, GlobalPos> = HashMultimap.create()
    val stored: Object2IntMap<UUID> = Object2IntOpenHashMap()

    private val storePositions: Map<UUID, List<GlobalPos>> get() = positions.asMap().mapValues { it.value.toMutableList() }

    init {
        for ((player, blocks) in positions) {
            this.positions.putAll(player, blocks)
        }

        this.stored.putAll(stored)
    }

    fun add(player: UUID, pos: GlobalPos, contents: Int) {
        if (pos in positions[player]) return
        positions.put(player, pos)
        stored[player] = contents + (stored[player] ?: 0)
        Tempad.server?.overworld()?.let { syncData(it, ModAttachments.syncedEnergy, this) }
    }

    fun getCapacity(player: UUID): Int {
        return positions[player].size * CommonConfigCache.Metronome.capacity
    }

    fun getStored(player: UUID): Int {
        return stored[player] ?: 0
    }

    fun tick() {
        for ((player, amount) in stored) {
            stored[player] = (CommonConfig.Metronome.generationAmount * (if(CommonConfig.Metronome.scaleGeneration) positions[player].size else 1) + amount).coerceAtMost(getCapacity(player))
        }
        sync()
    }

    fun sync() {
        Tempad.server?.overworld()?.let { syncData(it, ModAttachments.syncedEnergy, this) }
    }

    fun remove(player: UUID, pos: GlobalPos): Int {
        if (getCapacity(player) == 0) return 0
        return stored[player]?.let {
            val amount = it / positions[player].size
            stored[player] = it - amount
            positions[player] -= pos
            return amount
        } ?: 0
    }
}