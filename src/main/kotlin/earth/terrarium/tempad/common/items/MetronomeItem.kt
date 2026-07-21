package earth.terrarium.tempad.common.items

import earth.terrarium.tempad.api.capabilities.chronons
import earth.terrarium.tempad.client.tooltip.tooltip
import earth.terrarium.tempad.common.block.MetronomeBe
import earth.terrarium.tempad.common.registries.ModBlocks
import earth.terrarium.tempad.common.registries.chrononContent
import earth.terrarium.tempad.common.utils.access
import net.minecraft.core.BlockPos
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.tooltip.TooltipComponent
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import java.util.Optional

class MetronomeItem(props: Properties) : BlockItem(ModBlocks.metronome, props) {
    override fun updateCustomBlockEntityTag(
        pos: BlockPos,
        level: Level,
        player: Player?,
        stack: ItemStack,
        state: BlockState,
    ): Boolean {
        level.getBlockEntity(pos)?.let { blockEntity ->
            if (blockEntity !is MetronomeBe) return@let
            player?.let {
                blockEntity.owner = it.gameProfile
                blockEntity.initialChronons = stack.chrononContent
            }
        }
        return super.updateCustomBlockEntityTag(pos, level, player, stack, state)
    }

    override fun getTooltipImage(stack: ItemStack): Optional<TooltipComponent> {
        return Optional.ofNullable(stack.access.chronons?.tooltip)
    }
}