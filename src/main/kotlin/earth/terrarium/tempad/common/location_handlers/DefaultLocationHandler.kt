package earth.terrarium.tempad.common.location_handlers

import com.mojang.authlib.GameProfile
import earth.terrarium.tempad.Tempad
import earth.terrarium.tempad.api.context.SyncableContext
import earth.terrarium.tempad.api.locations.LocationHandler
import earth.terrarium.tempad.api.locations.NamedGlobalVec3
import earth.terrarium.tempad.common.registries.savedPositions
import earth.terrarium.tempad.tempadId
import net.minecraft.world.entity.player.Player
import java.util.*

class DefaultLocationHandler(val gameProfile: GameProfile) : LocationHandler {
    companion object {
        val ID = "default".tempadId
    }

    val player: Player
        get() = Tempad.server?.playerList?.getPlayer(gameProfile.id) ?: throw IllegalStateException("Player not found")

    override val locations: Map<UUID, NamedGlobalVec3> by player.savedPositions::locations

    override fun minusAssign(locationId: UUID) {
        player.savedPositions -= locationId
    }
}