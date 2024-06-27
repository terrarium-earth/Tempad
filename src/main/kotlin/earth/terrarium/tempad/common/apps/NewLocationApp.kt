package earth.terrarium.tempad.common.apps

import com.teamresourceful.bytecodecs.base.ByteCodec
import com.teamresourceful.resourcefullib.common.menu.MenuContent
import com.teamresourceful.resourcefullib.common.menu.MenuContentSerializer
import earth.terrarium.tempad.api.app.TempadApp
import earth.terrarium.tempad.common.config.CommonConfig
import earth.terrarium.tempad.common.menu.NewLocationMenu
import earth.terrarium.tempad.common.registries.ModTags
import earth.terrarium.tempad.common.utils.RecordCodecMenuContentSerializer
import earth.terrarium.tempad.common.utils.contains
import net.minecraft.core.registries.Registries
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.item.ItemStack

object NewLocationApp: TempadApp<AllowLocationSettingData> {
    override fun isAppAvailable(player: Player, stack: ItemStack): Boolean {
        if (!CommonConfig.allow_location_saving) return false
        val lookup = player.level().registryAccess().lookup(Registries.DIMENSION)
        return lookup
            .flatMap { it.get(player.level().dimension()) }
            .map { it !in ModTags.LOCATION_SAVING_NOT_SUPPORTED }
            .orElse(player.abilities.instabuild)
    }

    override fun createMenu(pContainerId: Int, pPlayerInventory: Inventory, pPlayer: Player): AbstractContainerMenu {
        return NewLocationMenu(pContainerId, pPlayerInventory, AllowLocationSettingData(CommonConfig.allow_location_saving))
    }

    override fun getDisplayName(): Component = Component.translatable("menu.tempad.new_location")

    override fun createContent(player: ServerPlayer?) = AllowLocationSettingData(CommonConfig.allow_location_saving)
}

data class AllowLocationSettingData(val allowLocationSaving: Boolean): MenuContent<AllowLocationSettingData> {
    companion object {
        val CODEC = ByteCodec.BOOLEAN.map({ AllowLocationSettingData(it) }, { it.allowLocationSaving })
        val SERIALIZER = RecordCodecMenuContentSerializer(CODEC)
    }

    override fun serializer(): MenuContentSerializer<AllowLocationSettingData> = SERIALIZER
}
