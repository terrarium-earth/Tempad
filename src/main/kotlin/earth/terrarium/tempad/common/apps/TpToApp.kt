package earth.terrarium.tempad.common.apps

import earth.terrarium.tempad.api.app.TempadApp
import earth.terrarium.tempad.api.context.SyncableContext
import earth.terrarium.tempad.common.registries.ModMenus
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import java.util.*

class TpToApp(val ctx: SyncableContext<*>): TempadApp<BasicAppContent> {
    override fun createMenu(pContainerId: Int, pPlayerInventory: Inventory, pPlayer: Player): AbstractContainerMenu {
        return ModMenus.TpToMenu(pContainerId, pPlayerInventory, Optional.of(createContent(pPlayer as ServerPlayer)))
    }

    override fun getDisplayName(): Component = Component.translatable("app.tempad.tp_to")
    override fun createContent(player: ServerPlayer?): BasicAppContent = BasicAppContent(ctx.holder)
}