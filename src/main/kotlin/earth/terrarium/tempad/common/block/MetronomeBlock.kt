package earth.terrarium.tempad.common.block

import com.mojang.serialization.MapCodec
import com.teamresourceful.resourcefullib.common.menu.ContentMenuProvider
import earth.terrarium.tempad.common.menu.MetronomeMenuData
import earth.terrarium.tempad.common.registries.ModBlocks
import earth.terrarium.tempad.common.registries.metronomeEnergy
import earth.terrarium.tempad.common.utils.safeLet
import net.minecraft.core.BlockPos
import net.minecraft.core.GlobalPos
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.BaseEntityBlock
import net.minecraft.world.level.block.RenderShape
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.BlockHitResult

class MetronomeBlock() : BaseEntityBlock(Properties.of()) {
    override fun <T : BlockEntity?> getTicker(
        level: Level,
        state: BlockState,
        blockEntityType: BlockEntityType<T>,
    ): BlockEntityTicker<T>? {
        return createTickerHelper(blockEntityType, ModBlocks.metronomeBe) { _, _, _, block -> block.tick() }
    }

    override fun onRemove(
        state: BlockState,
        level: Level,
        pos: BlockPos,
        newState: BlockState,
        movedByPiston: Boolean,
    ) {
        val blockEntity = level.getBlockEntity(pos)
        if (blockEntity is MetronomeBe && !level.isClientSide) {
            safeLet(metronomeEnergy, blockEntity.owner?.id) { energy, owner ->
                energy.remove(owner, GlobalPos(level.dimension(), pos))
            }
        }
    }

    override fun onPlace(
        state: BlockState,
        level: Level,
        pos: BlockPos,
        oldState: BlockState,
        movedByPiston: Boolean,
    ) {
        val blockEntity = level.getBlockEntity(pos)
        if (blockEntity is MetronomeBe && blockEntity.bootTime == 0 && !level.isClientSide) {
            safeLet(metronomeEnergy, blockEntity.owner?.id) { energy, owner ->
                energy.add(owner, GlobalPos(level.dimension(), pos), blockEntity.initialChronons)
                blockEntity.initialChronons = 0
            }
        }
        super.onPlace(state, level, pos, oldState, movedByPiston)
    }

    override fun useWithoutItem(
        state: BlockState,
        level: Level,
        pos: BlockPos,
        player: Player,
        hitResult: BlockHitResult,
    ): InteractionResult {
        safeLet(level.getBlockEntity(pos) as? MetronomeBe, player as? ServerPlayer) { blockEntity, opener ->
            blockEntity.openMenu(opener)
        }
        return InteractionResult.SUCCESS
    }

    override fun codec(): MapCodec<out BaseEntityBlock?> = simpleCodec { MetronomeBlock() }

    override fun newBlockEntity(pos: BlockPos, state: BlockState, ): BlockEntity = MetronomeBe(pos, state)

    override fun getRenderShape(state: BlockState): RenderShape {
        return RenderShape.MODEL
    }
}