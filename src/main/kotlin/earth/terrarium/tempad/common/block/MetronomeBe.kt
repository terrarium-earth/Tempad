package earth.terrarium.tempad.common.block

import com.mojang.authlib.GameProfile
import com.teamresourceful.resourcefullib.common.menu.ContentMenuProvider
import earth.terrarium.tempad.api.tva_device.chronons
import earth.terrarium.tempad.api.tva_device.move
import earth.terrarium.tempad.common.config.CommonConfig
import earth.terrarium.tempad.common.menu.MetronomeMenu
import earth.terrarium.tempad.common.menu.MetronomeMenuData
import earth.terrarium.tempad.common.registries.ModBlocks
import earth.terrarium.tempad.common.registries.locked
import earth.terrarium.tempad.common.registries.metronomeEnergy
import earth.terrarium.tempad.common.utils.GAME_PROFILE_CODEC
import earth.terrarium.tempad.common.utils.get
import earth.terrarium.tempad.common.utils.load
import earth.terrarium.tempad.common.utils.safeLet
import earth.terrarium.tempad.common.utils.save
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
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
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.neoforged.neoforge.items.ItemStackHandler
import java.util.Optional

class MetronomeBe(pos: BlockPos, state: BlockState) : BlockEntity(ModBlocks.metronomeBe, pos, state), ContentMenuProvider<MetronomeMenuData> {
    var owner: GameProfile? = null
    var initialChronons = 0
    var bootChronons = 0
    var bootTime = 100
    val inventory = ItemStackHandler(8)

    fun tick() {
        if(level !is ServerLevel) return
        if(bootTime > 0) {
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

        chronons?.let { metronome ->
            for (i in 0 .. 7) {
                if (i < 4) {
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
    }

    override fun saveAdditional(
        tag: CompoundTag,
        registries: HolderLookup.Provider,
    ) {
        super.saveAdditional(tag, registries)
        owner?.let { tag.save(GAME_PROFILE_CODEC.codec(), "Owner", it) }
        tag.put("Inventory", inventory.serializeNBT(registries))
        tag.putInt("BootTime", bootTime)
        tag.putInt("BootChronons", bootChronons)
        tag.putInt("InitialChronons", initialChronons)
    }

    override fun loadAdditional(
        tag: CompoundTag,
        registries: HolderLookup.Provider,
    ) {
        super.loadAdditional(tag, registries)
        owner = tag.load(GAME_PROFILE_CODEC.codec(), "Owner")
        bootTime = if("BootTime" in tag) tag.getInt("BootTime") else 100
        initialChronons = tag.getInt("InitialChronons")
        bootChronons = tag.getInt("BootChronons")
        inventory.deserializeNBT(registries, tag.getCompound("Inventory"))
    }

    override fun getUpdateTag(registries: HolderLookup.Provider): CompoundTag {
        return CompoundTag().apply { saveAdditional(this, registries) }
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
        return MetronomeMenu(containerId, playerInventory, inventory, safeLet(level, owner) { lvl, own -> MetronomeMenuData(own.id, GlobalPos(lvl.dimension(), blockPos), locked) })
    }
}