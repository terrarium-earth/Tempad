package earth.terrarium.tempad.common.registries

import earth.terrarium.tempad.api.locations.*
import earth.terrarium.tempad.api.capabilities.player_access.DefaultLocationAccess
import earth.terrarium.tempad.api.capabilities.player_access.PlayerAccessApi
import earth.terrarium.tempad.common.compat.initFTBTeamsAccess
import earth.terrarium.tempad.common.location_handlers.DefaultLocationHandler
import earth.terrarium.tempad.common.location_handlers.PlayerHandler
import earth.terrarium.tempad.common.location_handlers.DoorPointsHandler
import earth.terrarium.tempad.common.location_handlers.WalletLocationHandler
import earth.terrarium.tempad.tempadId
import net.neoforged.fml.ModList

object ModLocations {
    fun init() {
        TempadLocations[DefaultLocationHandler.ID] = { player, _, _ -> DefaultLocationHandler(player) }
        TempadLocations[DoorPointsHandler.ID] = { player, _, _ -> DoorPointsHandler(player) }
        TempadLocations[PlayerHandler.ID] = { player, upgrades, _ -> PlayerHandler(player, upgrades) }
        TempadLocations[WalletLocationHandler.id] = ::WalletLocationHandler

        TempadLocations.setDeletable(DefaultLocationHandler.ID)

        PlayerAccessApi["public".tempadId] = DefaultLocationAccess.Public
        // if(ModList.get().isLoaded("odyssey_claims")) initAlliesAccess()
        if(ModList.get().isLoaded("ftbteams")) initFTBTeamsAccess()
    }
}