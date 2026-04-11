package earth.terrarium.tempad.api.locations

import com.mojang.authlib.GameProfile
import earth.terrarium.tempad.api.context.ItemContext
import earth.terrarium.tempad.api.tva_device.ChrononHandler
import earth.terrarium.tempad.api.tva_device.UpgradeHandler
import earth.terrarium.tempad.api.tva_device.chronons
import earth.terrarium.tempad.api.tva_device.upgrades
import net.minecraft.resources.Identifier
import net.minecraft.world.entity.player.Player
import java.util.*

interface LocationHandler {
    val locations: Map<UUID, NamedGlobalVec3>

    operator fun minusAssign(locationId: UUID)

    operator fun get(locationId: UUID): NamedGlobalVec3? = locations[locationId]

    fun getSerializable(locationId: UUID): LocationGetter?
}

typealias LocationProvider = (GameProfile, UpgradeHandler, ChrononHandler) -> LocationHandler

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
    operator fun get(player: GameProfile, upgrades: UpgradeHandler, chronons: ChrononHandler, id: Identifier): LocationHandler? =
        registry[id]?.let { it(player, upgrades, chronons) }

    operator fun get(player: Player, ctx: ItemContext, id: Identifier): LocationHandler? =
        registry[id]?.let { it(player.gameProfile, ctx.stack.upgrades!!, ctx.stack.chronons!!) }

    @JvmStatic
    operator fun get(player: GameProfile, upgrades: UpgradeHandler, chronons: ChrononHandler): Map<Identifier, Map<UUID, NamedGlobalVec3>> =
        registry.mapValues { it.value(player, upgrades, chronons).locations }

    @JvmStatic
    operator fun get(player: Player, ctx: ItemContext): Map<Identifier, Map<UUID, NamedGlobalVec3>> =
        registry.mapValues { it.value(player.gameProfile, ctx.stack.upgrades!!, ctx.stack.chronons!!).locations }
}