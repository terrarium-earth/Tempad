package earth.terrarium.tempad.common.apps

import com.teamresourceful.bytecodecs.base.ByteCodec
import com.teamresourceful.bytecodecs.base.`object`.ObjectByteCodec
import earth.terrarium.tempad.api.locations.ProviderSettings
import earth.terrarium.tempad.api.locations.TempadLocations
import earth.terrarium.tempad.api.app.TempadApp
import earth.terrarium.tempad.api.context.ContextInstance
import earth.terrarium.tempad.api.context.ItemContext
import earth.terrarium.tempad.common.registries.ModMenus
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import java.util.*

data class SettingsApp(val ctx: ContextInstance): TempadApp<SettingsData> {
    override fun createMenu(pContainerId: Int, pPlayerInventory: Inventory, pPlayer: Player): AbstractContainerMenu {
        return ModMenus.SettingsMenu(
            pContainerId,
            pPlayerInventory,
            Optional.of(SettingsData(TempadLocations.providers, ctx.ctx))
        )
    }

    override fun getDisplayName(): Component = Component.translatable("app.tempad.settings")

    override fun createContent(player: ServerPlayer): SettingsData = SettingsData(TempadLocations.providers, ctx.ctx)

    override fun isEnabled(player: Player): Boolean = true
}

class SettingsData(val providers: Set<ProviderSettings>, ctx: ItemContext): AppContent<SettingsData>(ctx, codec) {
    companion object {
        val codec: ByteCodec<SettingsData> = ObjectByteCodec.create(
            ProviderSettings.BYTE_CODEC.setOf().fieldOf { it.providers },
            ItemContext.codec.fieldOf { it.ctx },
            ::SettingsData
        )
    }
}