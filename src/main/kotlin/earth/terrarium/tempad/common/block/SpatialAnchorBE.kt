package earth.terrarium.tempad.common.block

import com.mojang.authlib.GameProfile
import earth.terrarium.tempad.api.locations.NamedGlobalVec3
import earth.terrarium.tempad.api.visibility.AnchorAccessApi
import earth.terrarium.tempad.common.registries.ModAttachments
import earth.terrarium.tempad.common.registries.ModBlocks
import earth.terrarium.tempad.common.registries.accessId
import earth.terrarium.tempad.common.registries.color
import earth.terrarium.tempad.common.registries.owner
import net.minecraft.core.BlockPos
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.world.Nameable
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.phys.Vec3
import kotlin.jvm.optionals.getOrNull

class SpatialAnchorBE(pos: BlockPos, state: BlockState): BlockEntity(ModBlocks.spatialAnchorBE, pos, state), Nameable {
    var posName: Component get() = getExistingData(ModAttachments.name).getOrNull() ?: ModBlocks.spatialAnchor.name
        set(value) {
            setData(ModAttachments.name, value)
        }

    val landingPosition: Vec3 get() = if(blockState.getValue(BlockStateProperties.UP)) Vec3.atCenterOf(worldPosition) else Vec3.atBottomCenterOf(worldPosition.below(2))
    val namedGlobalVec3 get() = NamedGlobalVec3(posName, landingPosition, level!!.dimension(), blockState.getValue(BlockStateProperties.HORIZONTAL_FACING).toYRot(), color)

    fun canAccess(player: GameProfile): Boolean {
        return AnchorAccessApi[this.accessId].canAccess(level!!, owner!!, player)
    }

    override fun getUpdateTag(registries: HolderLookup.Provider): CompoundTag {
        return CompoundTag().apply { saveAdditional(this, registries) }
    }

    override fun getCustomName(): Component? {
        return getExistingData(ModAttachments.name).getOrNull()
    }

    override fun getName(): Component {
        return posName
    }
}