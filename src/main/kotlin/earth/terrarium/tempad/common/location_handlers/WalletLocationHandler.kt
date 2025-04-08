package earth.terrarium.tempad.common.location_handlers

import com.mojang.authlib.GameProfile
import earth.terrarium.tempad.Tempad
import earth.terrarium.tempad.api.context.ContextLocator
import earth.terrarium.tempad.api.context.ContextRegistry
import earth.terrarium.tempad.api.locations.LocationGetter
import earth.terrarium.tempad.api.locations.LocationHandler
import earth.terrarium.tempad.api.locations.NamedGlobalVec3
import earth.terrarium.tempad.api.tva_device.ChrononHandler
import earth.terrarium.tempad.api.tva_device.UpgradeHandler
import earth.terrarium.tempad.common.registries.ModItems
import earth.terrarium.tempad.common.registries.portalTarget
import earth.terrarium.tempad.common.registries.walletContents
import net.minecraft.server.level.ServerPlayer
import java.util.UUID

class WalletLocationHandler(val playerProfile: GameProfile, val upgrades: UpgradeHandler, val chronons: ChrononHandler): LocationHandler {
    val player: ServerPlayer? get() = Tempad.server?.playerList?.getPlayer(playerProfile.id)

    override val locations: Map<UUID, NamedGlobalVec3>
        get() {
            val player = player ?: return emptyMap()
            val wallet = ContextRegistry.locate(player) {
                it.`is`(ModItems.cardWallet) && it.walletContents.nonEmptyItems().count() > 0
            } ?: return emptyMap()
            val cards = wallet.stack.walletContents.nonEmptyItems()
            for (stack in cards) {
                stack.portalTarget?.get(upgrades, chronons)?.let {

                }
            }
            return emptyMap()
        }


    override fun minusAssign(locationId: UUID) {
        TODO("Not yet implemented")
    }

    override fun getSerializable(locationId: UUID): LocationGetter? {
        TODO("Not yet implemented")
    }
}