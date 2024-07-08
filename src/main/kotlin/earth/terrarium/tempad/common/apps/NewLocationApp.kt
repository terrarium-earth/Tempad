package earth.terrarium.tempad.common.apps

import com.teamresourceful.bytecodecs.base.ByteCodec
import com.teamresourceful.bytecodecs.base.`object`.ObjectByteCodec
import earth.terrarium.tempad.api.app.TempadApp
import earth.terrarium.tempad.common.config.CommonConfig
import earth.terrarium.tempad.common.registries.ModMenus
import earth.terrarium.tempad.common.registries.ModTags
import earth.terrarium.tempad.common.utils.contains
import net.minecraft.core.registries.Registries
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import java.util.*

data class NewLocationApp(val slotId: Int) : TempadApp<NewLocationData> {
    override fun isEnabled(player: Player): Boolean {
        if (!CommonConfig.allowLocationSaving) return false
        val lookup = player.level().registryAccess().lookup(Registries.DIMENSION)
        return lookup
            .flatMap { it.get(player.level().dimension()) }
            .map { it !in ModTags.LOCATION_SAVING_NOT_SUPPORTED }
            .orElse(player.abilities.instabuild)
    }

    override fun createMenu(pContainerId: Int, pPlayerInventory: Inventory, pPlayer: Player): AbstractContainerMenu {
        return ModMenus.NewLocationMenu(pContainerId, pPlayerInventory, Optional.of(NewLocationData(CommonConfig.allowLocationSaving, slotId)))
    }

    override fun getDisplayName(): Component = Component.translatable("menu.tempad.new_location")

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
