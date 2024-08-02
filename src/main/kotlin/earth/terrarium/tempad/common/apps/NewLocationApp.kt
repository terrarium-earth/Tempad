package earth.terrarium.tempad.common.apps

import com.teamresourceful.bytecodecs.base.ByteCodec
import com.teamresourceful.bytecodecs.base.`object`.ObjectByteCodec
import earth.terrarium.tempad.api.app.TempadApp
import earth.terrarium.tempad.api.context.ContextHolder
import earth.terrarium.tempad.api.context.SyncableContext
import earth.terrarium.tempad.common.config.CommonConfig
import earth.terrarium.tempad.common.registries.ModMenus
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import java.util.*

data class NewLocationApp(val ctx: SyncableContext<*>) : TempadApp<NewLocationData> {
    override fun createMenu(pContainerId: Int, pPlayerInventory: Inventory, pPlayer: Player): AbstractContainerMenu {
        return ModMenus.NewLocationMenu(pContainerId, pPlayerInventory, Optional.of(NewLocationData(CommonConfig.allowLocationSaving, ctx.holder)))
    }

    override fun getDisplayName(): Component = Component.translatable("app.tempad.new_location")

    override fun createContent(player: ServerPlayer?) = NewLocationData(CommonConfig.allowLocationSaving, ctx.holder)
}

class NewLocationData(val allowLocationSaving: Boolean, ctx: ContextHolder<*>) : AppContent<NewLocationData>(ctx, codec) {
    companion object {
        val codec: ByteCodec<NewLocationData> = ObjectByteCodec.create(
            ByteCodec.BOOLEAN.fieldOf { it.allowLocationSaving },
            ContextHolder.codec.fieldOf { it.ctx },
            ::NewLocationData
        )
    }
}
