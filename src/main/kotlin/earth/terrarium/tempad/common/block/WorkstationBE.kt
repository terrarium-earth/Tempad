package earth.terrarium.tempad.common.block

import earth.terrarium.tempad.api.sizing.FloorPlacementSettings
import earth.terrarium.tempad.api.sizing.TimedoorPlacementSettings
import earth.terrarium.tempad.api.sizing.VerticalPlacementSettings
import earth.terrarium.tempad.api.tva_device.chronons
import earth.terrarium.tempad.api.tva_device.upgrades
import earth.terrarium.tempad.common.entity.TimedoorEntity
import earth.terrarium.tempad.common.registries.ModBlocks
import earth.terrarium.tempad.common.registries.owner
import earth.terrarium.tempad.common.registries.portalOffset
import earth.terrarium.tempad.common.registries.selectedPos
import earth.terrarium.tempad.common.utils.get
import earth.terrarium.tempad.common.utils.safeLet
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.HolderLookup
import net.minecraft.core.UUIDUtil
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.game.ClientGamePacketListener
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.phys.AABB
import net.neoforged.neoforge.items.ItemStackHandler
import thedarkcolour.kotlinforforge.neoforge.forge.vectorutil.v3d.component1
import thedarkcolour.kotlinforforge.neoforge.forge.vectorutil.v3d.component2
import thedarkcolour.kotlinforforge.neoforge.forge.vectorutil.v3d.component3
import java.util.UUID

class WorkstationBE(pos: BlockPos, state: BlockState) : BlockEntity(ModBlocks.workstationBE, pos, state) {
    val inventory = ItemStackHandler(1)
    var cookingTime: Int = 0
    var recipe: ResourceLocation? = null
    var timedoorId: UUID? = null

    val direction: Direction get() = blockState.getValue(BlockStateProperties.HORIZONTAL_FACING)

    companion object {
        val downloadKey = "DownloadTime"
        val recipeKey = "Recipe"
        val inventoryKey = "Items"
        val timedoorKey = "TimedoorId"
    }

    override fun saveAdditional(tag: CompoundTag, registries: HolderLookup.Provider) {
        super.saveAdditional(tag, registries)
        tag.put(inventoryKey, inventory.serializeNBT(registries))
        tag.putInt(downloadKey, cookingTime)
        recipe?.let { tag.putString(recipeKey, it.toString()) } ?: tag.remove(recipeKey)
        timedoorId?.let { tag.putString(timedoorKey, it.toString()) } ?: tag.remove(timedoorKey)
    }

    override fun loadAdditional(tag: CompoundTag, registries: HolderLookup.Provider) {
        super.loadAdditional(tag, registries)
        inventory.deserializeNBT(registries, tag.getCompound(inventoryKey))
        cookingTime = tag.getInt(downloadKey)
        timedoorId = UUID.fromString(tag.getString(downloadKey))
        recipe = ResourceLocation.tryParse(tag.getString("recipe"))
    }

    override fun getUpdateTag(registries: HolderLookup.Provider): CompoundTag {
        val tag = super.getUpdateTag(registries)
        tag.put(inventoryKey, inventory.serializeNBT(registries))
        tag.putInt(downloadKey, cookingTime)
        return tag
    }

    fun openTimedoor() {
        val level = level
        if(level == null || level !is ServerLevel) return
        val nearby = level.getEntitiesOfClass(ServerPlayer::class.java, AABB(blockPos).inflate(5.0))
        val potentialDoor = timedoorId?.let { id -> level.entities.get(id) }
        if(potentialDoor as? TimedoorEntity != null) {
            potentialDoor.closingTime += 20;
            potentialDoor.linkedPortalEntity?.let { it.closingTime += 20 }
            return
        }
        safeLet(inventory[0].selectedPos, upgrades, chronons, owner) { pos, upgrades, chronons, player ->
            pos.get(upgrades, chronons)?.let {
                TimedoorEntity.openTimedoor(player, this, it, getSizing()) {
                    it.yRot = inventory[0].portalOffset.angle.toFloat() + direction.toYRot()
                }?.let { msg ->
                    nearby.error(msg)
                }
            }
        }
    }

    fun getSizing(): TimedoorPlacementSettings {
        val offset = inventory[0].portalOffset
        val (x, y, z) = offset.calcOffset(direction)
        return if (offset.isUpright) {
            VerticalPlacementSettings(x, y, z)
        } else {
            FloorPlacementSettings(x, y, z)
        }
    }

    override fun getUpdatePacket(): ClientboundBlockEntityDataPacket {
        return ClientboundBlockEntityDataPacket.create(this)
    }

    fun List<ServerPlayer>.error(msg: Component) {
        forEach { it.displayClientMessage(msg, true) }
    }
}