package earth.terrarium.tempad.common.apps

import earth.terrarium.tempad.api.app.TempadApp
import earth.terrarium.tempad.api.access.ItemAccessAddress
import earth.terrarium.tempad.api.capabilities.upgrades
import earth.terrarium.tempad.common.registries.ModApps
import earth.terrarium.tempad.common.registries.ModItems
import earth.terrarium.tempad.common.registries.ModMenus
import earth.terrarium.tempad.common.utils.translatable
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import java.util.Optional

data class KnowledgeRepositoryApp(val ctx: ItemAccessAddress<*>) : TempadApp<BasicAppContent> {
    override fun isEnabled(player: Player): Boolean {
        return ctx.getAccess(player).upgrades?.contains(ModItems.guideKey) == true
    }

    override fun createContent(player: ServerPlayer?): BasicAppContent = BasicAppContent(ctx)

    override fun getDisplayName(): Component = ModApps.guide.toLanguageKey("app").translatable

    override fun createMenu(
        containerId: Int,
        playerInventory: Inventory,
        player: Player,
    ): AbstractContainerMenu? {
        return ModMenus.KnowledgeMenu(containerId, playerInventory, Optional.of(BasicAppContent(ctx)))
    }
}
