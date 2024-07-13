package earth.terrarium.tempad.common.apps

import com.teamresourceful.bytecodecs.base.ByteCodec
import com.teamresourceful.bytecodecs.base.`object`.ObjectByteCodec
import earth.terrarium.tempad.api.app.TempadApp
import earth.terrarium.tempad.api.context.ContextInstance
import earth.terrarium.tempad.api.context.ItemContext
import earth.terrarium.tempad.common.config.CommonConfig
import earth.terrarium.tempad.common.registries.ModMenus
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import java.util.*

data class NewLocationApp(val ctx: ContextInstance) : TempadApp<NewLocationData> {
    override fun createMenu(pContainerId: Int, pPlayerInventory: Inventory, pPlayer: Player): AbstractContainerMenu {
        return ModMenus.NewLocationMenu(pContainerId, pPlayerInventory, Optional.of(NewLocationData(CommonConfig.allowLocationSaving, ctx.ctx)))
    }

    override fun getDisplayName(): Component = Component.translatable("app.tempad.new_location")

    override fun createContent(player: ServerPlayer?) = NewLocationData(CommonConfig.allowLocationSaving, ctx.ctx)
}

class NewLocationData(val allowLocationSaving: Boolean, ctx: ItemContext) : AppContent<NewLocationData>(ctx, codec) {
    companion object {
        val codec: ByteCodec<NewLocationData> = ObjectByteCodec.create(
            ByteCodec.BOOLEAN.fieldOf { it.allowLocationSaving },
            ItemContext.codec.fieldOf { it.ctx },
            ::NewLocationData
        )
    }
}
