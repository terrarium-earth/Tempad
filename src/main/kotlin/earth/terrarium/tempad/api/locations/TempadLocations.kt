package earth.terrarium.tempad.api.locations

import com.mojang.authlib.GameProfile
import earth.terrarium.tempad.api.context.ItemContext
import earth.terrarium.tempad.api.tva_device.ChrononHandler
import earth.terrarium.tempad.api.tva_device.UpgradeHandler
import earth.terrarium.tempad.api.tva_device.chronons
import earth.terrarium.tempad.api.tva_device.upgrades
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.player.Player
import java.util.*

interface LocationHandler {
    val locations: Map<UUID, NamedGlobalVec3>

    operator fun minusAssign(locationId: UUID)

    operator fun get(locationId: UUID): NamedGlobalVec3? = locations[locationId]
}

typealias LocationProvider = (GameProfile, UpgradeHandler, ChrononHandler) -> LocationHandler

object TempadLocations {
    val registry: Map<ResourceLocation, LocationProvider>
        field = mutableMapOf()

    @JvmStatic
    @JvmName("register")
    operator fun set(settings: ResourceLocation, provider: LocationProvider) {
        registry[settings] = provider
    }

    @JvmStatic
    operator fun get(id: ResourceLocation): LocationProvider? = registry[id]

    @JvmStatic
    operator fun get(player: GameProfile, upgrades: UpgradeHandler, chronons: ChrononHandler, id: ResourceLocation): LocationHandler? =
        registry[id]?.let { it(player, upgrades, chronons) }

    operator fun get(player: Player, ctx: ItemContext, id: ResourceLocation): LocationHandler? =
        registry[id]?.let { it(player.gameProfile, ctx.stack.upgrades!!, ctx.stack.chronons!!) }

    @JvmStatic
    operator fun get(player: GameProfile, upgrades: UpgradeHandler, chronons: ChrononHandler): Map<ResourceLocation, Map<UUID, NamedGlobalVec3>> =
        registry.mapValues { it.value(player, upgrades, chronons).locations }

    @JvmStatic
    operator fun get(player: Player, ctx: ItemContext): Map<ResourceLocation, Map<UUID, NamedGlobalVec3>> =
        registry.mapValues { it.value(player.gameProfile, ctx.stack.upgrades!!, ctx.stack.chronons!!).locations }
}