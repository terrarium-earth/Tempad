package earth.terrarium.tempad.common.block

import com.mojang.authlib.GameProfile
import com.teamresourceful.resourcefullib.common.menu.ContentMenuProvider
import earth.terrarium.tempad.api.tva_device.chronons
import earth.terrarium.tempad.api.tva_device.move
import earth.terrarium.tempad.common.config.CommonConfig
import earth.terrarium.tempad.common.menu.MetronomeMenu
import earth.terrarium.tempad.common.menu.MetronomeMenuData
import earth.terrarium.tempad.common.registries.ModBlocks
import earth.terrarium.tempad.common.utils.GAME_PROFILE_CODEC
import earth.terrarium.tempad.common.utils.get
import earth.terrarium.tempad.common.utils.load
import earth.terrarium.tempad.common.utils.save
import net.minecraft.core.BlockPos
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.neoforged.neoforge.items.ItemStackHandler
import java.util.Optional

class MetronomeBe(pos: BlockPos, state: BlockState) : BlockEntity(ModBlocks.metronomeBe, pos, state), ContentMenuProvider<MetronomeMenuData> {
    var owner: GameProfile? = null
    var initialChronons = 0
    val inventory = ItemStackHandler(8)

    fun tick() {
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

            level?.getBlockEntity(blockPos.above())?.chronons?.let { target ->
                move(metronome, target, CommonConfig.Metronome.transferRate)
            }

            level?.getBlockEntity(blockPos.below())?.chronons?.let { target ->
                move(metronome, target, CommonConfig.Metronome.transferRate)
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
    }

    override fun loadAdditional(
        tag: CompoundTag,
        registries: HolderLookup.Provider,
    ) {
        super.loadAdditional(tag, registries)
        owner = tag.load(GAME_PROFILE_CODEC.codec(), "Owner")
        inventory.deserializeNBT(registries, tag.getCompound("Inventory"))
    }

    override fun getUpdateTag(registries: HolderLookup.Provider): CompoundTag {
        return CompoundTag().apply { saveAdditional(this, registries) }
    }

    override fun getUpdatePacket(): ClientboundBlockEntityDataPacket {
        return ClientboundBlockEntityDataPacket.create(this)
    }

    override fun createContent(player: ServerPlayer?): MetronomeMenuData? {
        return owner?.let { MetronomeMenuData(it.id) }
    }

    override fun getDisplayName(): Component {
        return blockState.block.name
    }

    override fun createMenu(
        containerId: Int,
        playerInventory: Inventory,
        player: Player,
    ): AbstractContainerMenu? {
        return MetronomeMenu(containerId, playerInventory, inventory, owner?.let { MetronomeMenuData(it.id) })
    }
}