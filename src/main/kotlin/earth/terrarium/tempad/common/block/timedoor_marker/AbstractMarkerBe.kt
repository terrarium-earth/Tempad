package earth.terrarium.tempad.common.block.timedoor_marker

import com.mojang.authlib.GameProfile
import earth.terrarium.tempad.api.locations.NamedGlobalVec3
import earth.terrarium.tempad.common.registries.*
import earth.terrarium.tempad.common.utils.facingAngle
import earth.terrarium.tempad.common.utils.safeLet
import net.minecraft.core.BlockPos
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.Nameable
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.storage.ValueInput
import net.minecraft.world.level.storage.ValueOutput
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

    abstract fun canAccess(player: GameProfile): Boolean

    override fun preRemoveSideEffects(
        pos: BlockPos,
        state: BlockState,
    ) {
        super.preRemoveSideEffects(pos, state)
        if (level is ServerLevel) {
            safeLet(id, anchorPoints) { id, points ->
                points -= id
            }
        }
    }

    override fun loadAdditional(input: ValueInput) {
        super.loadAdditional(input)
        locked = input.getBooleanOr("Locked", true)
    }

    override fun saveAdditional(output: ValueOutput) {
        super.saveAdditional(output)
        output.putBoolean("Locked", locked)
    }

    override fun getUpdateTag(registries: HolderLookup.Provider): CompoundTag {
        return CompoundTag().apply { putBoolean("Locked", locked) }
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