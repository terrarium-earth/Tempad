package earth.terrarium.tempad.common.apps

import com.teamresourceful.bytecodecs.base.ByteCodec
import com.teamresourceful.bytecodecs.base.`object`.ObjectByteCodec
import com.teamresourceful.resourcefullib.common.bytecodecs.ExtraByteCodecs
import earth.terrarium.tempad.api.app.TempadApp
import earth.terrarium.tempad.api.context.ContextHolder
import earth.terrarium.tempad.api.context.SyncableContext
import earth.terrarium.tempad.api.locations.TempadLocations
import earth.terrarium.tempad.common.registries.ModMenus
import net.minecraft.network.chat.Component
import net.minecraft.resources.Identifier
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import java.util.*

data class SettingsApp(val ctx: SyncableContext<*>, val isStationary: Boolean) : TempadApp<SettingsData> {
    override fun createMenu(pContainerId: Int, pPlayerInventory: Inventory, pPlayer: Player): AbstractContainerMenu {
        return ModMenus.SettingsMenu(
            pContainerId,
            pPlayerInventory,
            Optional.of(SettingsData(TempadLocations.registry.keys, isStationary, ctx.holder))
        )
    }

    override fun getDisplayName(): Component = Component.translatable("app.tempad.settings")

    override fun createContent(player: ServerPlayer): SettingsData =
        SettingsData(TempadLocations.registry.keys, isStationary, ctx.holder)

    override fun isEnabled(player: Player): Boolean = true
}

class SettingsData(val providers: Set<Identifier>, isStationary: Boolean, ctx: ContextHolder<*>) : AppContent<SettingsData>(ctx, isStationary, codec) {
    companion object {
        val codec: ByteCodec<SettingsData> = ObjectByteCodec.create(
            ExtraByteCodecs.RESOURCE_LOCATION.setOf().fieldOf { it.providers },
            ByteCodec.BOOLEAN.fieldOf { it.isStationary },
            ContextHolder.codec.fieldOf { it.ctx },
            ::SettingsData
        )
    }
}