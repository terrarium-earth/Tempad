package earth.terrarium.tempad.common.block

import earth.terrarium.tempad.api.locations.IndirectLocation
import earth.terrarium.tempad.api.sizing.FloorPlacementSettings
import earth.terrarium.tempad.api.sizing.TimedoorPlacementSettings
import earth.terrarium.tempad.api.sizing.VerticalPlacementSettings
import earth.terrarium.tempad.api.tva_device.chronons
import earth.terrarium.tempad.api.tva_device.drainAndExecute
import earth.terrarium.tempad.api.tva_device.upgrades
import earth.terrarium.tempad.common.block.WorkstationBlock.Companion.HAS_TAPE
import earth.terrarium.tempad.common.config.CommonConfig
import earth.terrarium.tempad.common.entity.TimedoorEntity
import earth.terrarium.tempad.common.registries.ModBlocks
import earth.terrarium.tempad.common.registries.owner
import earth.terrarium.tempad.common.registries.portalOffset
import earth.terrarium.tempad.common.registries.selectedPos
import earth.terrarium.tempad.common.utils.get
import earth.terrarium.tempad.common.utils.safeLet
import earth.terrarium.tempad.common.utils.stack
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.HolderLookup
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.item.Items
import net.minecraft.world.level.block.Block.UPDATE_ALL
import net.minecraft.world.level.block.Block.popResource
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
    var downloadTime: Int = 0
    var maxDownloadTime: Int = 0
    var recipe: ResourceLocation? = null
    var timedoorId: UUID? = null
    val active get() = activeLeft || activeRight
    var activeLeft = false
    var activeRight = false
    var age = 0

    val direction: Direction get() = blockState.getValue(BlockStateProperties.HORIZONTAL_FACING)

    companion object {
        const val DOWNLOAD_TIME = "DownloadTime"
        const val MAX_DOWNLOAD_TIME = "MaxDownloadTime"
        const val RECIPE = "Recipe"
        const val INVENTORY = "Items"
        const val TIMEDOOR = "TimedoorId"
        const val LEFT = "Left"
        const val RIGHT = "Right"
    }

    override fun saveAdditional(tag: CompoundTag, registries: HolderLookup.Provider) {
        super.saveAdditional(tag, registries)
        tag.put(INVENTORY, inventory.serializeNBT(registries))
        tag.putInt(DOWNLOAD_TIME, downloadTime)
        tag.putInt(MAX_DOWNLOAD_TIME, maxDownloadTime)
        tag.putBoolean(LEFT, activeLeft)
        tag.putBoolean(RIGHT, activeRight)
        recipe?.let { tag.putString(RECIPE, it.toString()) }
        timedoorId?.let { tag.putString(TIMEDOOR, it.toString()) }
    }

    override fun loadAdditional(tag: CompoundTag, registries: HolderLookup.Provider) {
        super.loadAdditional(tag, registries)
        inventory.deserializeNBT(registries, tag.getCompound(INVENTORY))
        downloadTime = tag.getInt(DOWNLOAD_TIME)
        maxDownloadTime = tag.getInt(MAX_DOWNLOAD_TIME)
        activeLeft = tag.getBoolean(LEFT)
        activeRight = tag.getBoolean(RIGHT)
        recipe = ResourceLocation.tryParse(tag.getString(RECIPE))
        timedoorId = tag.getString(TIMEDOOR).takeUnless { it.isEmpty() }?.let { UUID.fromString(it) }
    }

    override fun getUpdateTag(registries: HolderLookup.Provider): CompoundTag {
        val tag = super.getUpdateTag(registries)
        tag.put(INVENTORY, inventory.serializeNBT(registries))
        tag.putInt(DOWNLOAD_TIME, downloadTime)
        tag.putInt(MAX_DOWNLOAD_TIME, maxDownloadTime)
        return tag
    }

    fun activateLeft() {
        activeLeft = true
    }
    
    fun deactivateLeft() {
        activeLeft = false
    }
    
    fun activateRight() {
        activeRight = true
    }

    fun deactivateRight() {
        activeRight = false
    }

    fun tick() {
        val level = level
        if (level !is ServerLevel) return
        age++
        if (maxDownloadTime > 0) {
            downloadTime--
            if (downloadTime == 0) {
                maxDownloadTime = 0
                popResource(level, blockPos, Items.DRIED_KELP.stack(level.random.nextInt(3)))
                level.sendParticles(ParticleTypes.HAPPY_VILLAGER, blockPos.x + 0.5, blockPos.y + 0.25, blockPos.z + 0.5, 10, 0.2, 0.2, 0.2, 0.0)
                safeLet(inventory[0].upgrades, recipe) { upgrades, recipe ->
                    upgrades.install(recipe)
                    setChanged()
                }
                level.setBlock(blockPos, blockState.setValue(HAS_TAPE, false), UPDATE_ALL)
            } else {
                setChanged()
                level.sendBlockUpdated(blockPos, blockState, blockState, UPDATE_ALL)
                level.sendParticles(ParticleTypes.SMOKE, blockPos.x + 0.5, blockPos.y + 0.25, blockPos.z + 0.5, 2, 0.2, 0.2, 0.2, 0.0)
            }
        }
        if (active) {
            val timedoor = timedoor
            if (timedoor == null && age > 10) openTimedoor()
            else if(timedoor != null && (age) % 20 == 0) {
                chronons?.drainAndExecute(CommonConfig.TimeDoor.costToPersist) {
                    timedoor.closingTime += 20
                    timedoor.linkedPortalEntity?.let { it.closingTime += 20 }
                }
            }
        }
    }

    fun openTimedoor() {
        val level = level
        if(level == null || level !is ServerLevel) return
        val nearby = level.getEntitiesOfClass(ServerPlayer::class.java, AABB(blockPos).inflate(5.0))
        safeLet(inventory[0].selectedPos, upgrades, chronons, owner) { pos, upgrades, chronons, player ->
            pos.get(upgrades, chronons)?.let {
                val (_, _, provider, id) = pos
                TimedoorEntity.openTimedoor(player, this, provider, id, it, getSizing()) {
                    this.timedoorId = it.uuid
                    it.yRot = inventory[0].portalOffset.angle.toFloat() + direction.toYRot()
                }?.let { msg ->
                    nearby.error(msg)
                }
            }
        }
    }

    val timedoor: TimedoorEntity? get() = safeLet(timedoorId, level as? ServerLevel) { id, lvl -> lvl.entities.get(id) } as? TimedoorEntity

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