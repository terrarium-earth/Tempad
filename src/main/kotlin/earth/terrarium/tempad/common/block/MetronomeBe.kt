package earth.terrarium.tempad.common.block

import com.mojang.authlib.GameProfile
import com.teamresourceful.resourcefullib.common.menu.ContentMenuProvider
import earth.terrarium.tempad.common.config.CommonConfig
import earth.terrarium.tempad.common.menu.MetronomeMenu
import earth.terrarium.tempad.common.menu.MetronomeMenuData
import earth.terrarium.tempad.common.registries.ModBlocks
import earth.terrarium.tempad.common.registries.metronomeEnergy
import earth.terrarium.tempad.common.utils.GAME_PROFILE_CODEC
import earth.terrarium.tempad.common.utils.safeLet
import net.minecraft.core.BlockPos
import net.minecraft.core.GlobalPos
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.SimpleContainerData
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.storage.ValueInput
import net.minecraft.world.level.storage.ValueOutput
import kotlin.jvm.optionals.getOrNull

class MetronomeBe(pos: BlockPos, state: BlockState) : BlockEntity(ModBlocks.metronomeBe, pos, state),
    ContentMenuProvider<MetronomeMenuData> {
    var owner: GameProfile? = null
    var locked = true
    var initialChronons = 0
    var bootChronons = 0
    var localChronons = 0
    var localCapacity = 0
    var bootTime = 100
    val inventory = MetronomeItemHandler()

    fun tick() {
        if (level !is ServerLevel) return
        if (bootTime > 0) {
            if (bootChronons >= CommonConfig.Metronome.jumpStartAmount) {
                bootTime--

                if (bootTime <= 0) {
                    bootChronons = 0
                    safeLet(metronomeEnergy, owner?.id, level) { energy, owner, lvl ->
                        energy.add(owner, GlobalPos(lvl.dimension(), blockPos), initialChronons)
                        this.initialChronons = 0
                    }
                    level?.sendBlockUpdated(blockPos, blockState, blockState, Block.UPDATE_ALL)
                    this.setChanged()
                } else {
                    level?.sendBlockUpdated(blockPos, blockState, blockState, Block.UPDATE_ALL)
                    this.setChanged()
                }
            } else {
                bootTime = 100
                level?.sendBlockUpdated(blockPos, blockState, blockState, Block.UPDATE_ALL)
                this.setChanged()
            }
            return
        }

        level?.server?.tickCount?.let {
            if ((it + 1) % CommonConfig.Metronome.generationRate == 0) {
                level?.sendBlockUpdated(blockPos, blockState, blockState, Block.UPDATE_ALL)
                this.setChanged()
            }
        }

        /*
        chronons?.let { metronome ->
            for (i in 0 .. 7) {
                if (i < 4) {
                    HandlerItemAccess(inventory, i).getCapability(Tempad)?.let {

                    }

                    inventory[i].chronons?.let { target ->
                        move(target, metronome, CommonConfig.Metronome.transferRate)
                    }

                } else {
                    inventory[i].chronons?.let { target ->
                        move(metronome, target, CommonConfig.Metronome.transferRate)
                    }
                }
            }

            for (direction in Direction.entries) {
                level?.getBlockEntity(blockPos.relative(direction))?.chronons?.let { target ->
                    move(metronome, target, CommonConfig.Metronome.transferRate)
                }
            }
        }
         */
    }

    override fun saveAdditional(tag: ValueOutput) {
        super.saveAdditional(tag)
        owner?.let { tag.store("Owner", GAME_PROFILE_CODEC.codec(), it) }
        tag.putChild("Inventory", inventory)
        tag.putInt("BootTime", bootTime)
        tag.putInt("BootChronons", bootChronons)
        tag.putInt("InitialChronons", initialChronons)
        tag.putBoolean("Locked", locked)
    }

    override fun loadAdditional(tag: ValueInput) {
        super.loadAdditional(tag)
        owner = tag.read("Owner", GAME_PROFILE_CODEC.codec()).getOrNull()
        bootTime = tag.getIntOr("BootTime", 100)
        locked = tag.getBooleanOr("Locked", true)
        initialChronons = tag.getIntOr("InitialChronons", 0)
        bootChronons = tag.getIntOr("BootChronons", 0)
        localChronons = tag.getIntOr("LocalChronons", 0)
        localCapacity = tag.getIntOr("LocalCapacity", 0)
        tag.readChild("Inventory", inventory)
    }

    override fun preRemoveSideEffects(
        pos: BlockPos,
        state: BlockState,
    ) {
        if (level?.isClientSide?.not() ?: false && bootTime == 0) {
            safeLet(metronomeEnergy, owner?.id) { energy, owner ->
                initialChronons = energy.remove(owner, GlobalPos(level!!.dimension(), pos))
            }
        }
        super.preRemoveSideEffects(pos, state)
    }

    override fun getUpdateTag(registries: HolderLookup.Provider): CompoundTag {
        return saveCustomOnly(registries)
    }

    override fun getUpdatePacket(): ClientboundBlockEntityDataPacket {
        return ClientboundBlockEntityDataPacket.create(this)
    }

    override fun createContent(player: ServerPlayer?): MetronomeMenuData? {
        return owner?.let { MetronomeMenuData(it.id, GlobalPos(level?.dimension()!!, blockPos), locked) }
    }

    override fun getDisplayName(): Component {
        return blockState.block.name
    }

    override fun createMenu(
        containerId: Int,
        playerInventory: Inventory,
        player: Player,
    ): AbstractContainerMenu? {
        return MetronomeMenu(
            containerId,
            playerInventory,
            inventory,
            owner?.id?.let { MetronomeDataContainer(it) } ?: SimpleContainerData(2),
            safeLet(level, owner) { lvl, own ->
                MetronomeMenuData(
                    own.id,
                    GlobalPos(lvl.dimension(), blockPos),
                    locked
                )
            })
    }
}