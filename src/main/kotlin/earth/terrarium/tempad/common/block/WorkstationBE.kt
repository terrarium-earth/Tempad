package earth.terrarium.tempad.common.block

import earth.terrarium.tempad.common.registries.ModBlocks
import net.minecraft.core.BlockPos
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.game.ClientGamePacketListener
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.neoforged.neoforge.items.ItemStackHandler

class WorkstationBE(pos: BlockPos, state: BlockState) : BlockEntity(ModBlocks.workstationBE, pos, state) {
    val inventory = ItemStackHandler(1)
    var cookingTime: Int = 0
    var recipe: ResourceLocation? = null

    companion object {
        val downloadKey = "DownloadTime"
        val recipeKey = "Recipe"
        val inventoryKey = "Items"
    }

    override fun saveAdditional(tag: CompoundTag, registries: HolderLookup.Provider) {
        super.saveAdditional(tag, registries)
        tag.put(inventoryKey, inventory.serializeNBT(registries))
        tag.putInt(downloadKey, cookingTime)
        tag.putString(recipeKey, recipe.toString())
    }

    override fun loadAdditional(tag: CompoundTag, registries: HolderLookup.Provider) {
        super.loadAdditional(tag, registries)
        inventory.deserializeNBT(registries, tag.getCompound(inventoryKey))
        cookingTime = tag.getInt(downloadKey)
        tag.putString(recipeKey, recipe.toString())
    }

    override fun getUpdateTag(registries: HolderLookup.Provider): CompoundTag {
        val tag = super.getUpdateTag(registries)
        tag.put(inventoryKey, inventory.serializeNBT(registries))
        tag.putInt(downloadKey, cookingTime)
        return tag
    }

    override fun getUpdatePacket(): Packet<ClientGamePacketListener?>? {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}