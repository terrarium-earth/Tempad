package earth.terrarium.tempad.common.apps

import com.teamresourceful.bytecodecs.base.ByteCodec
import com.teamresourceful.bytecodecs.base.`object`.ObjectByteCodec
import earth.terrarium.tempad.api.app.TempadApp
import earth.terrarium.tempad.common.config.CommonConfig
import earth.terrarium.tempad.common.registries.ModMenus
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import java.util.*

data class NewLocationApp(val slotId: Int) : TempadApp<NewLocationData> {
    override fun createMenu(pContainerId: Int, pPlayerInventory: Inventory, pPlayer: Player): AbstractContainerMenu {
        return ModMenus.NewLocationMenu(pContainerId, pPlayerInventory, Optional.of(NewLocationData(CommonConfig.allowLocationSaving, slotId)))
    }

    override fun getDisplayName(): Component = Component.translatable("app.tempad.new_location")

    override fun createContent(player: ServerPlayer?) = NewLocationData(CommonConfig.allowLocationSaving, slotId)
}

class NewLocationData(val allowLocationSaving: Boolean, slotId: Int) : AppContent<NewLocationData>(slotId, CODEC) {
    companion object {
        val CODEC: ByteCodec<NewLocationData> = ObjectByteCodec.create(
            ByteCodec.BOOLEAN.fieldOf { it.allowLocationSaving },
            ByteCodec.INT.fieldOf { it.slotId },
            ::NewLocationData
        )
    }
}
