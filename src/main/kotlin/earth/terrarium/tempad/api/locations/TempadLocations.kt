package earth.terrarium.tempad.api.locations

import com.mojang.authlib.GameProfile
import earth.terrarium.tempad.api.capabilities.chronons
import earth.terrarium.tempad.api.capabilities.upgrades
import earth.terrarium.tempad.api.capabilities.upgrades.UpgradeHandler
import earth.terrarium.tempad.common.data.DimensionTpData
import earth.terrarium.tempad.common.registries.ModDataMaps
import net.minecraft.core.BlockPos
import net.minecraft.core.GlobalPos
import net.minecraft.resources.Identifier
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.player.Player
import net.neoforged.neoforge.transfer.access.ItemAccess
import net.neoforged.neoforge.transfer.energy.EnergyHandler
import java.util.*
import kotlin.math.ceil

interface LocationHandler {
    val locations: Map<UUID, NamedGlobalVec3>

    operator fun minusAssign(locationId: UUID)

    operator fun get(locationId: UUID): NamedGlobalVec3? = locations[locationId]

    fun calculateCost(fromLevel: ServerLevel, from: BlockPos, to: UUID): Int {
        val toPos = get(to) ?: return -1
        return calculateTimedoorCost(fromLevel, from, toPos)
    }

    fun getSerializable(locationId: UUID): LocationGetter?
}

fun calculateTimedoorCost(fromLevel: ServerLevel, from: BlockPos, toPos: NamedGlobalVec3): Int {
    val toType = fromLevel.server.getLevel(toPos.dimension)?.dimensionTypeRegistration() ?: return -1
    val fromType = fromLevel.dimensionTypeRegistration()
    val fromDistance = fromType.getData(ModDataMaps.dimensionDistance) ?: DimensionTpData(5000, 1000)
    if (fromLevel.dimension() != toPos.dimension) {
        val toDistance = toType.getData(ModDataMaps.dimensionDistance) ?: DimensionTpData(5000, 1000)
        return (toDistance.maxCost) + fromDistance.maxCost
    }
    return ceil(from.distSqr(BlockPos.containing(toPos.pos)) / fromDistance.distancePlateau).toInt().coerceIn(0, fromDistance.maxCost)
}

typealias LocationProvider = (GameProfile, UpgradeHandler?, EnergyHandler?) -> LocationHandler

object TempadLocations {
    val registry: Map<Identifier, LocationProvider>
        field = mutableMapOf()

    val deletable: Set<Identifier>
        field = mutableSetOf()

    @JvmStatic
    @JvmName("register")
    operator fun set(settings: Identifier, provider: LocationProvider) {
        registry[settings] = provider
    }

    fun setDeletable(settings: Identifier) {
        deletable += settings
    }

    @JvmStatic
    operator fun get(id: Identifier): LocationProvider? = registry[id]

    @JvmStatic
    operator fun get(
        player: GameProfile,
        upgrades: UpgradeHandler,
        chronons: EnergyHandler,
        id: Identifier,
    ): LocationHandler? =
        registry[id]?.let { it(player, upgrades, chronons) }

    operator fun get(player: Player, ctx: ItemAccess, id: Identifier): LocationHandler? =
        registry[id]?.let { it(player.gameProfile, ctx.upgrades, ctx.chronons) }

    @JvmStatic
    operator fun get(
        player: GameProfile,
        upgrades: UpgradeHandler,
        chronons: EnergyHandler,
    ): Map<Identifier, Map<UUID, NamedGlobalVec3>> =
        registry.mapValues { it.value(player, upgrades, chronons).locations }

    @JvmStatic
    operator fun get(player: Player, ctx: ItemAccess): Map<Identifier, Map<UUID, NamedGlobalVec3>> =
        registry.mapValues { it.value(player.gameProfile, ctx.upgrades, ctx.chronons).locations }
}