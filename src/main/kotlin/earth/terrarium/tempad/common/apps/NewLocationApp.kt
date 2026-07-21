package earth.terrarium.tempad.common.apps

import com.teamresourceful.bytecodecs.base.ByteCodec
import com.teamresourceful.bytecodecs.base.`object`.ObjectByteCodec
import earth.terrarium.tempad.Tempad
import earth.terrarium.tempad.api.app.TempadApp
import earth.terrarium.tempad.api.access.ItemAccessAddress
import earth.terrarium.tempad.api.capabilities.upgrades
import earth.terrarium.tempad.common.config.CommonConfig
import earth.terrarium.tempad.common.registries.ModItems
import earth.terrarium.tempad.common.registries.ModMenus
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import java.util.*

data class NewLocationApp(val ctx: ItemAccessAddress<*>, val isStationary: Boolean): TempadApp<NewLocationAppData> {
    override fun isEnabled(player: Player): Boolean {
        val access = ctx.getAccess(player)
        return CommonConfig.allowLocationSaving && !isStationary && (CommonConfig.requireLocationUpgrade|| access.upgrades?.contains(ModItems.newLocationKey) == true)
    }

    override fun createMenu(pContainerId: Int, pPlayerInventory: Inventory, pPlayer: Player): AbstractContainerMenu {
        return ModMenus.NewLocationMenu(pContainerId, pPlayerInventory, Optional.of(NewLocationAppData(CommonConfig.allowLocationSaving, ctx)))
    }

    override fun getDisplayName(): Component = Component.translatable("app.tempad.new_location")

    override fun createContent(player: ServerPlayer?) = NewLocationAppData(CommonConfig.allowLocationSaving, ctx)
}

class NewLocationAppData(val allowLocationSaving: Boolean, ctx: ItemAccessAddress<*>) : AppContent<NewLocationAppData>(ctx, false, codec) {
    companion object {
        val codec: ByteCodec<NewLocationAppData> = ObjectByteCodec.create(
            ByteCodec.BOOLEAN.fieldOf { it.allowLocationSaving },
            ItemAccessAddress.codec.fieldOf { it.ctx },
            ::NewLocationAppData
        )
    }
}
