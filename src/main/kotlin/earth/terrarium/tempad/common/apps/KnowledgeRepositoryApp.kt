package earth.terrarium.tempad.common.apps

import earth.terrarium.tempad.api.app.TempadApp
import earth.terrarium.tempad.api.context.SyncableContext
import earth.terrarium.tempad.common.registries.ModApps
import earth.terrarium.tempad.common.registries.ModMenus
import earth.terrarium.tempad.common.utils.translatable
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import java.util.Optional

data class KnowledgeRepositoryApp(val ctx: SyncableContext<*>, val isStationary: Boolean) : TempadApp<BasicAppContent> {
    override fun createContent(player: ServerPlayer?): BasicAppContent = BasicAppContent(ctx.holder, isStationary)

    override fun getDisplayName(): Component = ModApps.guide.toLanguageKey("app").translatable

    override fun createMenu(
        containerId: Int,
        playerInventory: Inventory,
        player: Player,
    ): AbstractContainerMenu? {
        return ModMenus.KnowledgeMenu(containerId, playerInventory, Optional.of(BasicAppContent(ctx.holder, isStationary)))
    }
}
