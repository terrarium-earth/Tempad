package earth.terrarium.tempad.common.block

import earth.terrarium.tempad.api.tva_device.impl.MultiversalChrononHandler
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
import net.minecraft.world.inventory.SimpleContainerData
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.neoforged.neoforge.items.ItemStackHandler
import java.util.Optional

class MetronomeBe(pos: BlockPos, state: BlockState) : BlockEntity(ModBlocks.metronomeBe, pos, state), ContentMenuProvider<MetronomeMenuData> {
    var owner: GameProfile? = null
    var locked = true
    var initialChronons = 0
    var bootChronons = 0
    var localChronons = 0
    var localCapacity = 0
    var bootTime = BOOT_TIME
    val inventory = MetronomeItemHandler()

    companion object {
        const val BOOT_TIME = 100

        /** While booting, sync progress (only shown in Jade) every N ticks instead of every tick. */
        private const val BOOT_SYNC_INTERVAL = 10

        /** How often to check whether the owner's shared pool changed and needs to be shown to clients. */
        private const val POOL_SYNC_INTERVAL = 20

        /**
         * Energy is pushed/pulled every N ticks with N times the per-tick rate: same throughput, ~10x fewer
         * capability lookups and block entity syncs.
         */
        private const val TRANSFER_INTERVAL = 10
    }

    /** Spreads metronomes over different ticks so they don't all transfer on the same one. */
    private val tickPhase: Int by lazy { Math.floorMod(blockPos.asLong().hashCode(), TRANSFER_INTERVAL * POOL_SYNC_INTERVAL) }
    private var lastSentStored = -1
    private var lastSentCapacity = -1

    private fun sync() {
        level?.sendBlockUpdated(blockPos, blockState, blockState, Block.UPDATE_CLIENTS)
    }

    fun tick() {
        val level = level as? ServerLevel ?: return
        if (bootTime > 0) {
            tickBoot(level)
            return
        }

        val time = level.gameTime + tickPhase
        if (time % POOL_SYNC_INTERVAL == 0L) syncPoolIfChanged()
        if (time % TRANSFER_INTERVAL == 0L) transfer(level)
    }

    private fun tickBoot(level: ServerLevel) {
        if (bootChronons < CommonConfig.Metronome.jumpStartAmount) {
            // Not jump-started: used to re-sync every tick forever. Only reset (and sync) if boot progress was lost.
            if (bootTime != BOOT_TIME) {
                bootTime = BOOT_TIME
                sync()
                setChanged()
            }
            return
        }

        bootTime--
        if (bootTime <= 0) {
            bootChronons = 0
            safeLet(metronomeEnergy, owner?.id) { energy, owner ->
                energy.add(owner, GlobalPos(level.dimension(), blockPos), initialChronons)
                this.initialChronons = 0
            }
            // The chronon capability switches implementation once booted.
            level.invalidateCapabilities(blockPos)
            sync()
            setChanged()
        } else if (bootTime % BOOT_SYNC_INTERVAL == 0) {
            sync()
            setChanged()
        }
    }

    private fun syncPoolIfChanged() {
        val energy = metronomeEnergy ?: return
        val owner = owner?.id ?: return
        val stored = energy.getStored(owner)
        val capacity = energy.getCapacity(owner)
        if (stored == lastSentStored && capacity == lastSentCapacity) return
        lastSentStored = stored
        lastSentCapacity = capacity
        sync()
    }

    private fun transfer(level: ServerLevel) {
        val metronome = chronons ?: return
        val amount = CommonConfig.Metronome.transferRate * TRANSFER_INTERVAL
        var itemsChanged = false
        for (i in 0..7) {
            val target = inventory[i].chronons ?: continue
            val moved = if (i < 4) move(target, metronome, amount) else move(metronome, target, amount)
            if (moved > 0) itemsChanged = true
        }

        val ownerId = owner?.id
        for (direction in Direction.entries) {
            val neighbor = blockPos.relative(direction)
            // Never load a chunk just to look for a neighbour.
            if (!level.isLoaded(neighbor)) continue
            val target = level.getBlockEntity(neighbor)?.chronons ?: continue
            // A booted neighbour metronome of the same owner is the same shared pool: moving would be a no-op.
            if (target is MultiversalChrononHandler && target.playerId == ownerId) continue
            move(metronome, target, amount)
        }

        // Item charge lives in stack components; ItemStackHandler isn't notified of those edits, so mark dirty here.
        if (itemsChanged) setChanged()
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
        tag.putBoolean("Locked", locked)
    }

    override fun loadAdditional(
        tag: CompoundTag,
        registries: HolderLookup.Provider,
    ) {
        super.loadAdditional(tag, registries)
        owner = tag.load(GAME_PROFILE_CODEC.codec(), "Owner")
        bootTime = if("BootTime" in tag) tag.getInt("BootTime") else BOOT_TIME
        locked = tag.getBoolean("Locked")
        initialChronons = tag.getInt("InitialChronons")
        bootChronons = tag.getInt("BootChronons")
        localChronons = tag.getInt("LocalChronons")
        localCapacity = tag.getInt("LocalCapacity")
        // Update tags sent to clients carry no inventory; keep whatever the client has.
        if ("Inventory" in tag) inventory.deserializeNBT(registries, tag.getCompound("Inventory"))
    }

    /** Only what the client uses (capability + Jade): no inventory, which the menu syncs on its own. */
    override fun getUpdateTag(registries: HolderLookup.Provider): CompoundTag {
        return CompoundTag().apply {
            owner?.let { save(GAME_PROFILE_CODEC.codec(), "Owner", it) }
            putInt("BootTime", bootTime)
            putInt("BootChronons", bootChronons)
            safeLet(metronomeEnergy, this@MetronomeBe.owner) { energy, owner ->
                this.putInt("LocalChronons", energy.getStored(owner.id))
                this.putInt("LocalCapacity", energy.getCapacity(owner.id))
            }
        }
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
        return MetronomeMenu(containerId, playerInventory, inventory, owner?.id?.let { MetronomeDataContainer(it) } ?: SimpleContainerData(2), safeLet(level, owner) { lvl, own -> MetronomeMenuData(own.id, GlobalPos(lvl.dimension(), blockPos), locked) })
    }
}