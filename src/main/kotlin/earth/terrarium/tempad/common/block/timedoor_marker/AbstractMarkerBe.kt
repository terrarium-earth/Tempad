package earth.terrarium.tempad.common.block.timedoor_marker

import com.mojang.authlib.GameProfile
import earth.terrarium.tempad.api.locations.NamedGlobalVec3
import earth.terrarium.tempad.api.visibility.AnchorAccessApi
import earth.terrarium.tempad.common.registries.*
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
    var posName: Component get() = getExistingData(ModAttachments.name).getOrNull() ?: ModBlocks.timedoorMarker.name
        set(value) {
            setData(ModAttachments.name, value)
        }

    val landingPosition: Vec3 get() = if(blockState.getValue(BlockStateProperties.UP)) Vec3.atCenterOf(worldPosition) else Vec3.atBottomCenterOf(worldPosition.below(2))
    val namedGlobalVec3 get() = NamedGlobalVec3(posName, landingPosition, level!!.dimension(), blockState.getValue(BlockStateProperties.HORIZONTAL_FACING).toYRot(), color)

    abstract fun openScreen(player: Player)

    fun canAccess(player: GameProfile): Boolean {
        return owner ?.let { AnchorAccessApi[this.accessId].canAccess(level!!, it, player) } ?: true
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