package earth.terrarium.tempad.common.block

import earth.terrarium.tempad.common.registries.ModBlocks
import net.minecraft.core.BlockPos
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.game.ClientGamePacketListener
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.storage.ValueInput
import net.minecraft.world.level.storage.ValueOutput

class LiftwayBe(worldPosition: BlockPos, blockState: BlockState) : BlockEntity(ModBlocks.liftwayBE, worldPosition, blockState) {
    var usageCount = 0
    var lastUsageCount = 0 // only to be updated on the client by the renderer
    var tickCount = 0

    fun triggerMovement() {
        usageCount++
        setChanged()
        (level as? ServerLevel)?.chunkSource?.blockChanged(worldPosition)
    }

    fun clientTick() {
        tickCount++
    }

    override fun loadAdditional(input: ValueInput) {
        super.loadAdditional(input)
        usageCount = input.getIntOr("usageCount", 0)
        if (usageCount > lastUsageCount) {
            lastUsageCount = usageCount
            tickCount = 0
        }
    }

    override fun saveAdditional(output: ValueOutput) {
        super.saveAdditional(output)
        output.putInt("usageCount", usageCount)
    }

    override fun getUpdateTag(registries: HolderLookup.Provider): CompoundTag {
        return saveCustomOnly(registries)
    }

    override fun getUpdatePacket(): Packet<ClientGamePacketListener> {
        return ClientboundBlockEntityDataPacket.create(this)
    }
}