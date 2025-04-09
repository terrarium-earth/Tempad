package earth.terrarium.tempad.common.items

import com.mojang.authlib.GameProfile
import earth.terrarium.tempad.api.player_access.PlayerAccess
import earth.terrarium.tempad.api.player_access.PlayerAccessApi
import earth.terrarium.tempad.common.registries.accessId
import net.minecraft.ChatFormatting
import net.minecraft.network.chat.Component
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.level.Level

class ScreeningDeviceItem : Item(Properties().stacksTo(1)) {
    override fun use(level: Level, player: Player, hand: InteractionHand): InteractionResultHolder<ItemStack> {
        if (!level.isClientSide) {
            val stack = player.getItemInHand(hand)
            val ids = PlayerAccessApi.ids
            val currentIndex = ids.indexOf(stack.accessId)
            if (currentIndex == -1 || currentIndex != ids.size - 1) {
                stack.accessId = ids[currentIndex + 1 % ids.size]
            } else {
                stack.accessId = null
            }

            val display = stack.accessId?.toLanguageKey("access")?.let { Component.translatable(it).withStyle(ChatFormatting.GREEN) }
                ?: Component.translatable("item.tempad.screening_device.off").withStyle(ChatFormatting.RED)

            player.displayClientMessage(Component.translatable(
                "item.tempad.screening_device.screening",
                display
            ), true)
        }

        return InteractionResultHolder.success(player.getItemInHand(hand))
    }

    override fun appendHoverText(
        stack: ItemStack,
        context: TooltipContext,
        tooltipComponents: MutableList<Component?>,
        tooltipFlag: TooltipFlag,
    ) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag)
        val display =
            stack.accessId?.toLanguageKey("access")?.let { Component.translatable(it).withStyle(ChatFormatting.GREEN) }
                ?: Component.translatable("item.tempad.screening_device.off").withStyle(ChatFormatting.RED)
        tooltipComponents.add(
            Component.translatable(
                "item.tempad.screening_device.screening",
                display
            ).withStyle(ChatFormatting.GRAY)
        )
    }
}

class ScreeningDeviceAccess(val stack: ItemStack) : PlayerAccess {
    companion object {
        fun create(stack: ItemStack): ScreeningDeviceAccess? {
            return if (stack.accessId != null) ScreeningDeviceAccess(stack) else null
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