package earth.terrarium.tempad.common.block

import earth.terrarium.tempad.api.capabilities.chronons
import earth.terrarium.tempad.api.capabilities.upgrades
import earth.terrarium.tempad.api.sizing.FloorPlacementSettings
import earth.terrarium.tempad.api.sizing.TimedoorPlacementSettings
import earth.terrarium.tempad.api.sizing.VerticalPlacementSettings
import earth.terrarium.tempad.common.block.WorkstationBlock.Companion.HAS_TAPE
import earth.terrarium.tempad.common.config.CommonConfig
import earth.terrarium.tempad.common.entity.TimedoorEntity
import earth.terrarium.tempad.common.registries.ModBlocks
import earth.terrarium.tempad.common.registries.owner
import earth.terrarium.tempad.common.registries.portalOffset
import earth.terrarium.tempad.common.registries.selectedPos
import earth.terrarium.tempad.common.utils.access
import earth.terrarium.tempad.common.utils.extract
import earth.terrarium.tempad.common.utils.get
import earth.terrarium.tempad.common.utils.safeLet
import earth.terrarium.tempad.common.utils.stack
import earth.terrarium.tempad.common.utils.transfer
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.HolderLookup
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket
import net.minecraft.resources.Identifier
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.ItemOwner
import net.minecraft.world.item.Items
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block.UPDATE_ALL
import net.minecraft.world.level.block.Block.popResource
import net.minecraft.world.level.block.ShelfBlock
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.storage.ValueInput
import net.minecraft.world.level.storage.ValueOutput
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler
import org.joml.component1
import org.joml.component2
import org.joml.component3
import java.util.*

class WorkstationBE(pos: BlockPos, state: BlockState) : BlockEntity(ModBlocks.workstationBE, pos, state), ItemOwner {
    val inventory = ItemStacksResourceHandler(1)
    var downloadTime: Int = 0
    var maxDownloadTime: Int = 0
    var recipe: Identifier? = null
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

    override fun saveAdditional(tag: ValueOutput) {
        super.saveAdditional(tag)
        tag.putChild(INVENTORY, inventory)
        tag.putInt(DOWNLOAD_TIME, downloadTime)
        tag.putInt(MAX_DOWNLOAD_TIME, maxDownloadTime)
        tag.putBoolean(LEFT, activeLeft)
        tag.putBoolean(RIGHT, activeRight)
        recipe?.let { tag.putString(RECIPE, it.toString()) }
        timedoorId?.let { tag.putString(TIMEDOOR, it.toString()) }
    }

    override fun loadAdditional(tag: ValueInput) {
        super.loadAdditional(tag)
        inventory.deserialize(tag.childOrEmpty(INVENTORY))
        downloadTime = tag.getIntOr(DOWNLOAD_TIME, 0)
        maxDownloadTime = tag.getIntOr(MAX_DOWNLOAD_TIME, 0)
        activeLeft = tag.getBooleanOr(LEFT, false)
        activeRight = tag.getBooleanOr(RIGHT, false)
        recipe = Identifier.tryParse(tag.getStringOr(RECIPE, ""))
        timedoorId = tag.getStringOr(TIMEDOOR, "").takeUnless { it.isEmpty() }?.let { UUID.fromString(it) }
    }

    override fun getUpdateTag(registries: HolderLookup.Provider): CompoundTag {
        val tag = super.getUpdateTag(registries)
        // tag.put(INVENTORY, inventory.serialize(registries))
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
                safeLet(inventory.access(0).upgrades, recipe) { upgrades, recipe ->
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
                transfer {
                    if (chronons?.extract(CommonConfig.TimeDoor.costToPersist) == CommonConfig.TimeDoor.costToPersist) {
                        timedoor.closingTime += 20
                        timedoor.linkedPortalEntity?.let { it.closingTime += 20 }
                    }
                }
            }
        }
    }

    fun openTimedoor() {
        val level = level
        if(level == null || level !is ServerLevel) return
        val nearby = level.getEntitiesOfClass(ServerPlayer::class.java, AABB(blockPos).inflate(5.0))
        safeLet(inventory.getResource(0).selectedPos, upgrades, chronons, owner) { pos, upgrades, chronons, player ->
            pos.get(upgrades, chronons)?.let {
                val (_, _, provider, id) = pos
                TimedoorEntity.openTimedoor(player, this, provider, id, it, getSizing()) {
                    this.timedoorId = it.uuid
                    it.yRot = inventory.getResource(0).portalOffset.angle.toFloat() + direction.toYRot() + 180
                }?.let { msg ->
                    nearby.error(msg)
                }
            }
        }
    }

    val timedoor: TimedoorEntity? get() = safeLet(timedoorId, level as? ServerLevel) { id, lvl -> lvl.entities.get(id) } as? TimedoorEntity

    fun getSizing(): TimedoorPlacementSettings {
        val offset = inventory.getResource(0).portalOffset
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
        forEach { it.sendOverlayMessage(msg) }
    }

    override fun level(): Level {
        return this.level!!
    }

    override fun position(): Vec3 {
        return this.getBlockPos().getCenter()
    }

    override fun getVisualRotationYInDegrees(): Float {
        return (this.blockState.getValue(BlockStateProperties.HORIZONTAL_FACING)).opposite.toYRot()
    }
}