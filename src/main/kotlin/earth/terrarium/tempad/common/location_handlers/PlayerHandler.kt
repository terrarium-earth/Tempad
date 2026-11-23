package earth.terrarium.tempad.common.location_handlers

import net.minecraft.server.level.ServerPlayer
import net.minecraft.server.MinecraftServer
import earth.terrarium.tempad.api.player_access.PlayerAccess
import com.mojang.authlib.GameProfile
import com.mojang.serialization.Codec
import earth.terrarium.tempad.Tempad
import earth.terrarium.tempad.api.context.ContextRegistry
import earth.terrarium.tempad.api.locations.IndirectLocation
import earth.terrarium.tempad.api.locations.LocationGetter
import earth.terrarium.tempad.api.locations.LocationHandler
import earth.terrarium.tempad.api.locations.NamedGlobalVec3
import earth.terrarium.tempad.api.locations.namedGlobalVec3
import earth.terrarium.tempad.api.player_access.playerAccess
import earth.terrarium.tempad.api.tva_device.UpgradeHandler
import earth.terrarium.tempad.common.registries.ModItems
import earth.terrarium.tempad.tempadId
import net.minecraft.ChatFormatting
import net.minecraft.core.UUIDUtil
import net.minecraft.network.chat.Component
import java.util.UUID

class PlayerPointsData(data: Map<UUID, Map<UUID, NamedGlobalVec3>>) {
    val data = mutableMapOf<UUID, MutableMap<UUID, NamedGlobalVec3>>()

    init {
        for ((id, values) in data) {
            this.data[id] = values.toMutableMap()
        }
    }

    companion object {
        val codec: Codec<PlayerPointsData> = Codec.unboundedMap<UUID, MutableMap<UUID, NamedGlobalVec3>>(UUIDUtil.STRING_CODEC, Codec.unboundedMap<UUID, NamedGlobalVec3>(UUIDUtil.STRING_CODEC, NamedGlobalVec3.CODEC.codec())).xmap(::PlayerPointsData, PlayerPointsData::data)
    }

    operator fun get(playerId: UUID): MutableMap<UUID, NamedGlobalVec3> {
        return data.getOrPut(playerId) { mutableMapOf() }
    }
}

/**
 * Caches which [PlayerAccess] devices (location broadcaster, screening device, ...) each online player carries.
 *
 * Finding them means walking the whole inventory and curios slots. Without a cache every Tempad open by every player
 * did that for every other online player (O(players^2) server-wide). Entries live for [TTL_TICKS]; positions are
 * always read live, only "which devices does this player carry" can be up to a second stale.
 */
object BroadcastAccessCache {
    private const val TTL_TICKS = 20

    private class Entry(val tick: Int, val accesses: List<PlayerAccess>)

    private val cache = HashMap<UUID, Entry>()

    fun accessesOf(target: ServerPlayer): List<PlayerAccess> {
        val now = target.server.tickCount
        cache[target.uuid]?.let { if (now - it.tick < TTL_TICKS) return it.accesses }
        val found = ArrayList<PlayerAccess>(1)
        // The predicate always returns false so every registered locator visits every slot; we only collect.
        ContextRegistry.locate(target) { stack ->
            stack.playerAccess?.let(found::add)
            false
        }
        cache[target.uuid] = Entry(now, found)
        return found
    }

    fun forget(player: UUID) {
        cache.remove(player)
    }
}

class PlayerHandler(val player: GameProfile, val upgrades: UpgradeHandler) : LocationHandler {
    override val locations: Map<UUID, NamedGlobalVec3>
        get() {
            if (ModItems.playerKey !in upgrades) return emptyMap()
            val server = Tempad.server ?: return emptyMap()
            val result = LinkedHashMap<UUID, NamedGlobalVec3>()
            for (target in server.playerList.players) {
                if (isVisible(server, target)) result[target.uuid] = target.namedGlobalVec3
            }
            return result
        }

    /** Single-player lookup: one target instead of scanning everyone online. */
    override fun get(locationId: UUID): NamedGlobalVec3? {
        if (ModItems.playerKey !in upgrades) return null
        val server = Tempad.server ?: return null
        val target = server.playerList.getPlayer(locationId) ?: return null
        return if (isVisible(server, target)) target.namedGlobalVec3 else null
    }

    private fun isVisible(server: MinecraftServer, target: ServerPlayer): Boolean {
        if (target.uuid == player.id || target.isSpectator) return false
        return BroadcastAccessCache.accessesOf(target).any { it.canAccess(server.overworld(), target.gameProfile, player) }
    }

    override fun minusAssign(locationId: UUID) {
        // NO-OP
    }

    override fun getSerializable(locationId: UUID): LocationGetter? {
        // Only allow writing cards for players this accessor can actually see (the id comes from the client).
        if (get(locationId) == null) return null
        val pos = Tempad.server?.playerList?.getPlayer(locationId)?.gameProfile ?: return null
        return IndirectLocation(player, Component.translatable("locations.tempad.player", Component.literal(pos.name).withStyle(
            ChatFormatting.AQUA)).withStyle(ChatFormatting.GRAY), ID, locationId)
    }

    companion object {
        val ID = "player".tempadId
    }
}