package earth.terrarium.tempad.common.items

import com.mojang.authlib.GameProfile
import earth.terrarium.tempad.api.player_access.PlayerAccess
import earth.terrarium.tempad.api.player_access.PlayerAccessApi
import earth.terrarium.tempad.common.registries.accessId
import earth.terrarium.tempad.common.utils.toLanguageKey
import net.minecraft.network.chat.Component
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level

class ScreeningDeviceItem: Item(Properties().stacksTo(1)) {
    override fun use(level: Level, player: Player, hand: InteractionHand): InteractionResultHolder<ItemStack> {
        if (!level.isClientSide) {
            val stack = player.getItemInHand(hand)
            val ids = PlayerAccessApi.ids
            val currentIndex = ids.indexOf(stack.accessId)
            if (currentIndex > -1 && currentIndex != ids.size - 1) {
                stack.accessId = ids[currentIndex % ids.size]
                stack.accessId?.let {
                    player.displayClientMessage(Component.translatable(it.toLanguageKey("access")), true)
                }
            } else {
                stack.accessId = null
                player.displayClientMessage(Component.translatable(PlayerAccessApi.noAccess.toLanguageKey("access")), true)
            }
        }

        return InteractionResultHolder.success(player.getItemInHand(hand))
    }
}

class ScreeningDeviceAccess(val stack: ItemStack): PlayerAccess {
    companion object {
        fun create(stack: ItemStack): ScreeningDeviceAccess? {
            return if(stack.accessId != null) ScreeningDeviceAccess(stack) else null
        }
    }

    override fun canAccess(
        level: Level,
        owner: GameProfile,
        accessor: GameProfile,
    ): Boolean {
        return stack.accessId?.let { PlayerAccessApi[it] }?.canAccess(level, accessor, owner) == true
    }
}