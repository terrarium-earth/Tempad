package earth.terrarium.tempad.common.items

import com.mojang.authlib.GameProfile
import earth.terrarium.tempad.api.capabilities.player_access.PlayerLocationAccess
import earth.terrarium.tempad.api.capabilities.player_access.PlayerAccessApi
import earth.terrarium.tempad.common.registries.accessId
import net.minecraft.ChatFormatting
import net.minecraft.network.chat.Component
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.item.component.TooltipDisplay
import net.minecraft.world.level.Level
import java.util.function.Consumer

class ScreeningDeviceItem(props: Properties) : Item(props) {
    override fun use(level: Level, player: Player, hand: InteractionHand): InteractionResult {
        if (!level.isClientSide) {
            val stack = player.getItemInHand(hand)
            val ids = PlayerAccessApi.ids
            val currentIndex = ids.indexOf(stack.accessId)
            if (currentIndex == -1 || currentIndex != ids.size - 1) {
                stack.accessId = if(currentIndex == -1) ids[0] else ids[currentIndex + 1 % ids.size]
            } else {
                stack.accessId = null
            }

            val display = stack.accessId?.toLanguageKey("access")?.let { Component.translatable(it).withStyle(ChatFormatting.GREEN) }
                ?: Component.translatable("item.tempad.screening_device.off").withStyle(ChatFormatting.RED)

            player.sendSystemMessage(Component.translatable(
                "item.tempad.screening_device.screening",
                display
            ))
        }

        return InteractionResult.SUCCESS
    }

    override fun appendHoverText(
        stack: ItemStack,
        context: TooltipContext,
        display: TooltipDisplay,
        builder: Consumer<Component>,
        tooltipFlag: TooltipFlag
    ) {
        super.appendHoverText(stack, context, display, builder, tooltipFlag)
        val display =
            stack.accessId?.toLanguageKey("access")?.let { Component.translatable(it).withStyle(ChatFormatting.GREEN) }
                ?: Component.translatable("item.tempad.screening_device.off").withStyle(ChatFormatting.RED)
        builder.accept(
            Component.translatable(
                "item.tempad.screening_device.screening",
                display
            ).withStyle(ChatFormatting.GRAY)
        )
    }
}

class ScreeningDeviceLocationAccess(val stack: ItemStack) : PlayerLocationAccess {
    companion object {
        fun create(stack: ItemStack): ScreeningDeviceLocationAccess? {
            return if (stack.accessId != null) ScreeningDeviceLocationAccess(stack) else null
        }
    }

    override fun canLocate(
        level: Level,
        owner: GameProfile,
        accessor: GameProfile,
    ): Boolean {
        return stack.accessId?.let { PlayerAccessApi[it] }?.canLocate(level, accessor, owner) == true
    }
}