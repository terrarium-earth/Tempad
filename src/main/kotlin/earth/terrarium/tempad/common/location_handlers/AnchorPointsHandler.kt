package earth.terrarium.tempad.common.location_handlers

import com.mojang.authlib.GameProfile
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import earth.terrarium.tempad.Tempad
import earth.terrarium.tempad.api.locations.IndirectLocation
import earth.terrarium.tempad.api.locations.LocationGetter
import earth.terrarium.tempad.api.locations.LocationHandler
import earth.terrarium.tempad.api.locations.NamedGlobalVec3
import earth.terrarium.tempad.api.player_access.PlayerAccessApi
import earth.terrarium.tempad.common.block.timedoor_marker.AbstractMarkerBe
import earth.terrarium.tempad.common.registries.anchorPoints
import earth.terrarium.tempad.common.registries.id
import earth.terrarium.tempad.common.utils.GAME_PROFILE_CODEC
import earth.terrarium.tempad.tempadId
import net.minecraft.ChatFormatting
import net.minecraft.core.GlobalPos
import net.minecraft.core.UUIDUtil
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.MinecraftServer
import net.minecraft.world.level.Level
import java.util.*
import kotlin.jvm.optionals.getOrNull

/**
 * Who may travel to a marker. Lives in the [AnchorSnapshot] so access can be decided without the block entity.
 *
 * @param accessId a [PlayerAccessApi] id (chronomarks); takes precedence over [public] when set.
 * @param public the on/off toggle used by plain timedoor markers.
 */
data class AnchorAccessRule(val accessId: ResourceLocation?, val public: Boolean) {
    fun canAccess(level: Level, owner: GameProfile?, accessor: GameProfile): Boolean {
        if (owner == null) return false
        if (accessId != null) {
            return PlayerAccessApi[accessId]?.canAccess(level, owner, accessor) ?: (accessor.id == owner.id)
        }
        return public || accessor.id == owner.id
    }
}

/**
 * Everything needed to list / travel to a marker, cached so that queries never touch (and therefore never load) the
 * marker's chunk. Kept up to date by [AbstractMarkerBe] whenever it changes or loads.
 *
 * [location] is null only for entries migrated from the old position-only format that have not been refreshed yet.
 */
class AnchorSnapshot(
    val pos: GlobalPos,
    val location: NamedGlobalVec3?,
    val owner: GameProfile?,
    val access: AnchorAccessRule,
) {
    fun canAccess(server: MinecraftServer, accessor: GameProfile): Boolean {
        val level = server.getLevel(pos.dimension()) ?: server.overworld()
        return access.canAccess(level, owner, accessor)
    }

    companion object {
        private val CURRENT_CODEC: Codec<AnchorSnapshot> = RecordCodecBuilder.create { instance ->
            instance.group(
                GlobalPos.CODEC.fieldOf("anchor").forGetter { it.pos },
                NamedGlobalVec3.CODEC.codec().optionalFieldOf("location").forGetter { Optional.ofNullable(it.location) },
                GAME_PROFILE_CODEC.codec().optionalFieldOf("owner").forGetter { Optional.ofNullable(it.owner) },
                ResourceLocation.CODEC.optionalFieldOf("access_id").forGetter { Optional.ofNullable(it.access.accessId) },
                Codec.BOOL.optionalFieldOf("public", false).forGetter { it.access.public },
            ).apply(instance) { pos, location, owner, accessId, public ->
                AnchorSnapshot(pos, location.getOrNull(), owner.getOrNull(), AnchorAccessRule(accessId.getOrNull(), public))
            }
        }

        /** Pre-snapshot saves stored only the position; those entries get refreshed by [migrateLegacyAnchors]. */
        private val LEGACY_CODEC: Codec<AnchorSnapshot> = GlobalPos.CODEC.xmap(::legacy) { it.pos }

        val CODEC: Codec<AnchorSnapshot> = Codec.withAlternative(CURRENT_CODEC, LEGACY_CODEC)

        fun legacy(pos: GlobalPos) = AnchorSnapshot(pos, null, null, AnchorAccessRule(null, false))
    }
}

class AnchorPointsData(anchors: Map<UUID, AnchorSnapshot>) {
    companion object {
        val codec: Codec<AnchorPointsData> = Codec.unboundedMap(UUIDUtil.STRING_CODEC, AnchorSnapshot.CODEC)
            .xmap(::AnchorPointsData, AnchorPointsData::anchors)
    }

    val anchors = mutableMapOf<UUID, AnchorSnapshot>()

    init {
        this.anchors.putAll(anchors)
    }

    val needsMigration: Boolean get() = anchors.values.any { it.location == null }

    /** All anchors [accessor] may travel to. Reads snapshots only; never loads chunks. */
    fun getPositions(accessor: GameProfile): Map<UUID, NamedGlobalVec3> {
        val server = Tempad.server ?: return emptyMap()
        pruneMissing(server)
        val result = LinkedHashMap<UUID, NamedGlobalVec3>()
        for ((id, snapshot) in anchors) {
            val location = snapshot.location ?: continue
            if (snapshot.canAccess(server, accessor)) result[id] = location
        }
        return result
    }

    /** Single-anchor lookup, O(1) instead of building the whole visible list. */
    fun getPosition(id: UUID, accessor: GameProfile): NamedGlobalVec3? {
        val server = Tempad.server ?: return null
        val snapshot = anchors[id] ?: return null
        if (isMissing(server, snapshot)) {
            anchors.remove(id)
            return null
        }
        val location = snapshot.location ?: return null
        return if (snapshot.canAccess(server, accessor)) location else null
    }

    /** Registers [block] (assigning an id if it has none) and stores a fresh snapshot of it. */
    operator fun plusAssign(block: AbstractMarkerBe) {
        if (block.id == null) block.id = UUID.randomUUID()
        update(block)
    }

    /**
     * Refreshes the snapshot for [block]. Also drops any other entry pointing at the same position: placing a marker
     * item that carries an id used to leave behind the random id assigned in onPlace.
     */
    fun update(block: AbstractMarkerBe) {
        val id = block.id ?: return
        val snapshot = block.createSnapshot() ?: return
        anchors.entries.removeIf { (otherId, other) -> otherId != id && other.pos == snapshot.pos }
        anchors[id] = snapshot
    }

    /**
     * Like [update], but never steals an id that is registered at another position (e.g. a block entity copied by a
     * structure / schematic tool keeps the original's id). Used when a marker's chunk loads.
     */
    fun updateIfOwned(block: AbstractMarkerBe) {
        val id = block.id ?: return
        val existing = anchors[id]
        val level = block.level ?: return
        if (existing != null && existing.pos != GlobalPos.of(level.dimension(), block.blockPos)) return
        update(block)
    }

    operator fun minusAssign(locationId: UUID) {
        anchors.remove(locationId)
    }

    /** A snapshot is stale if its chunk is loaded (cheap to check) but the marker is gone, e.g. after external edits. */
    private fun isMissing(server: MinecraftServer, snapshot: AnchorSnapshot): Boolean {
        val level = server.getLevel(snapshot.pos.dimension()) ?: return true
        if (!level.isLoaded(snapshot.pos.pos())) return false
        return level.getBlockEntity(snapshot.pos.pos()) !is AbstractMarkerBe
    }

    private fun pruneMissing(server: MinecraftServer) {
        anchors.values.removeIf { isMissing(server, it) }
    }
}

/**
 * One-time conversion of position-only entries from older saves. This is the only place that loads marker chunks,
 * and it runs once at server start; afterwards entries stay current through block entity hooks.
 */
fun migrateLegacyAnchors(server: MinecraftServer) {
    val data = anchorPoints ?: return
    if (!data.needsMigration) return
    val legacy = data.anchors.filterValues { it.location == null }
    val start = System.nanoTime()
    var migrated = 0
    for ((id, snapshot) in legacy) {
        val level = server.getLevel(snapshot.pos.dimension())
        val block = level?.getBlockEntity(snapshot.pos.pos()) as? AbstractMarkerBe
        if (block == null) {
            data.anchors.remove(id)
            continue
        }
        if (block.id == null) block.id = id
        // If the block carries a different id (old onPlace bug when placing a marker item), this re-keys the entry to
        // the block's own id, which is the one location cards reference.
        data.update(block)
        migrated++
    }
    Tempad.logger.info(
        "Migrated {} of {} legacy Tempad anchor entries to snapshots in {} ms",
        migrated, legacy.size, (System.nanoTime() - start) / 1_000_000,
    )
}

class AnchorPointsHandler(val gameProfile: GameProfile): LocationHandler {
    override val locations: Map<UUID, NamedGlobalVec3> get() = anchorPoints?.getPositions(gameProfile) ?: emptyMap()

    override fun get(locationId: UUID): NamedGlobalVec3? = anchorPoints?.getPosition(locationId, gameProfile)

    override fun minusAssign(locationId: UUID) {}

    override fun getSerializable(locationId: UUID): LocationGetter? {
        val pos = get(locationId) ?: return null
        return IndirectLocation(gameProfile, Component.translatable("locations.tempad.anchor_point", MutableComponent.create(pos.name.contents).withStyle(
            ChatFormatting.AQUA)).withStyle(ChatFormatting.GRAY), ID, locationId)
    }

    companion object {
        val ID = "markers".tempadId
    }
}
