package earth.terrarium.tempad.common.block

import com.mojang.serialization.MapCodec
import com.teamresourceful.resourcefullib.common.menu.ContentMenuProvider
import earth.terrarium.tempad.Tempad
import earth.terrarium.tempad.common.menu.MetronomeMenuData
import earth.terrarium.tempad.common.registries.ModBlocks
import earth.terrarium.tempad.common.registries.ModItems
import earth.terrarium.tempad.common.registries.chrononContent
import earth.terrarium.tempad.common.registries.locked
import earth.terrarium.tempad.common.registries.metronomeEnergy
import earth.terrarium.tempad.common.registries.owner
import earth.terrarium.tempad.common.registries.portalTarget
import earth.terrarium.tempad.common.utils.safeLet
import earth.terrarium.tempad.common.utils.stack
import net.minecraft.core.BlockPos
import net.minecraft.core.GlobalPos
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.BaseEntityBlock
import net.minecraft.world.level.block.RenderShape
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.storage.loot.LootParams
import net.minecraft.world.level.storage.loot.parameters.LootContextParams
import net.minecraft.world.phys.BlockHitResult

class MetronomeBlock() : BaseEntityBlock(Properties.of().strength(3.0f, 1200f)) {
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
        if (blockEntity is MetronomeBe && !level.isClientSide && blockEntity.bootTime == 0) {
            safeLet(metronomeEnergy, blockEntity.owner?.id) { energy, owner ->
                blockEntity.initialChronons = energy.remove(owner, GlobalPos(level.dimension(), pos))
            }
        }
        super.onRemove(state, level, pos, newState, movedByPiston)
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
            if (blockEntity.bootTime == 0) {
                if (blockEntity.owner == null) {
                    blockEntity.owner = player.gameProfile
                } else if (blockEntity.locked && blockEntity.owner?.id != player.gameProfile.id) {
                    player.displayClientMessage(Component.translatable("error.tempad.block_locked", name).withColor(Tempad.ORANGE.value), true)
                    return InteractionResult.FAIL
                }
                blockEntity.openMenu(opener)
            }
        }
        return InteractionResult.SUCCESS
    }

    override fun getDrops(state: BlockState, params: LootParams.Builder): MutableList<ItemStack> {
        return mutableListOf(
            ModItems.metronome.stack {
                (params.getParameter(LootContextParams.BLOCK_ENTITY) as? MetronomeBe)?.let {
                    chrononContent = it.initialChronons
                }
            }
        )
    }

    override fun codec(): MapCodec<out BaseEntityBlock?> = simpleCodec { MetronomeBlock() }

    override fun newBlockEntity(pos: BlockPos, state: BlockState, ): BlockEntity = MetronomeBe(pos, state)

    override fun getRenderShape(state: BlockState): RenderShape {
        return RenderShape.MODEL
    }
}