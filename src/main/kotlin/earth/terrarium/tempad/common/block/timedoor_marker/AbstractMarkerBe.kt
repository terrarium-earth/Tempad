package earth.terrarium.tempad.common.block.timedoor_marker

import net.minecraft.server.level.ServerLevel
import net.minecraft.core.GlobalPos
import earth.terrarium.tempad.common.location_handlers.AnchorSnapshot
import earth.terrarium.tempad.common.location_handlers.AnchorAccessRule
import com.mojang.authlib.GameProfile
import earth.terrarium.tempad.api.locations.NamedGlobalVec3
import earth.terrarium.tempad.api.player_access.PlayerAccessApi
import earth.terrarium.tempad.common.registries.*
import earth.terrarium.tempad.common.utils.facingAngle
import net.minecraft.core.BlockPos
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket
import net.minecraft.world.Nameable
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.phys.Vec3
import kotlin.jvm.optionals.getOrNull

abstract class AbstractMarkerBe(block: BlockEntityType<*>, pos: BlockPos, state: BlockState): BlockEntity(block, pos, state), Nameable {
    var locked = true
    var posName: Component get() = getExistingData(ModAttachments.name).getOrNull() ?: blockState.block.name
        set(value) {
            setData(ModAttachments.name, value)
        }

    open val landingPosition: Vec3 get() {
        val relative = worldPosition.bottomCenter.relative(blockState.getValue(BlockStateProperties.HORIZONTAL_FACING), 1.0)
        return if(blockState.getValue(BlockStateProperties.UP)) relative.add(0.0, 0.5, 0.0) else relative.add(0.0, -2.0, 0.0)
    }

    open val landingAngle get() = facingAngle
    val namedGlobalVec3 get() = NamedGlobalVec3(posName, landingPosition, level!!.dimension(), landingAngle, color)

    abstract fun openScreen(player: Player)

    /** Access rule stored in the anchor snapshot; see [AnchorAccessRule]. */
    abstract fun accessRule(): AnchorAccessRule

    fun canAccess(player: GameProfile): Boolean = accessRule().canAccess(level!!, owner, player)

    fun createSnapshot(): AnchorSnapshot? {
        val level = level ?: return null
        return AnchorSnapshot(GlobalPos.of(level.dimension(), blockPos), namedGlobalVec3, owner, accessRule())
    }

    /** Pushes this marker's current state into the global anchor table (server only, registered markers only). */
    fun refreshAnchorSnapshot() {
        if (level !is ServerLevel || id == null || isRemoved) return
        anchorPoints?.update(this)
    }

    override fun setChanged() {
        super.setChanged()
        refreshAnchorSnapshot()
    }

    override fun onLoad() {
        super.onLoad()
        // Self-heals snapshots that drifted (external world edits, pre-snapshot saves). Never re-keys a foreign id.
        if (level is ServerLevel && id != null) anchorPoints?.updateIfOwned(this)
    }

    override fun loadAdditional(
        tag: CompoundTag,
        registries: HolderLookup.Provider,
    ) {
        super.loadAdditional(tag, registries)
        locked = tag.getBoolean("Locked")
    }

    override fun saveAdditional(
        tag: CompoundTag,
        registries: HolderLookup.Provider,
    ) {
        super.saveAdditional(tag, registries)
        tag.putBoolean("Locked", locked)
    }

    override fun getUpdateTag(registries: HolderLookup.Provider): CompoundTag {
        return CompoundTag().apply { saveAdditional(this, registries) }
    }

    override fun getUpdatePacket(): ClientboundBlockEntityDataPacket {
        return ClientboundBlockEntityDataPacket.create(this)
    }

    override fun getCustomName(): Component? {
        return getExistingData(ModAttachments.name).getOrNull()
    }

    override fun getName(): Component {
        return posName
    }
}