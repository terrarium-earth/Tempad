package earth.terrarium.tempad.common.block

import com.mojang.authlib.GameProfile
import earth.terrarium.tempad.api.capabilities.chronons
import earth.terrarium.tempad.api.capabilities.upgrades
import earth.terrarium.tempad.api.locations.IndirectLocation
import earth.terrarium.tempad.api.locations.LocationGetter
import earth.terrarium.tempad.api.sizing.FloorPlacementSettings
import earth.terrarium.tempad.api.sizing.TimedoorPlacementSettings
import earth.terrarium.tempad.api.sizing.VerticalPlacementSettings
import earth.terrarium.tempad.common.config.CommonConfig
import earth.terrarium.tempad.common.data.PortalPlacementComponent
import earth.terrarium.tempad.common.entity.TimedoorEntity
import earth.terrarium.tempad.common.registries.ModBlocks
import earth.terrarium.tempad.common.utils.GAME_PROFILE_CODEC
import earth.terrarium.tempad.common.utils.extract
import earth.terrarium.tempad.common.utils.safeLet
import earth.terrarium.tempad.common.utils.transfer
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.UUIDUtil
import net.minecraft.resources.Identifier
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.storage.ValueInput
import net.minecraft.world.level.storage.ValueOutput
import net.minecraft.world.phys.AABB
import org.joml.component1
import org.joml.component2
import org.joml.component3
import java.util.UUID
import kotlin.jvm.optionals.getOrNull

class TemputerTimeSteelBE(pos: BlockPos, blockState: BlockState) : BlockEntity(ModBlocks.temputerTimeSteelBE, pos, blockState) {
    var age = 0

    var locked = false
    var active: Boolean = false
    var timedoorId: UUID? = null
    var owner: GameProfile? = null
    var portalOffset = PortalPlacementComponent(0f, 0f, 2f, 0, false)
    var selectedPos: LocationGetter? = null

    val direction: Direction get() = blockState.getValue(BlockStateProperties.HORIZONTAL_FACING)
    val timedoor: TimedoorEntity? get() = safeLet(timedoorId, level as? ServerLevel) { id, lvl -> lvl.entities.get(id) } as? TimedoorEntity

    fun tick() {
        val level = level
        if (level !is ServerLevel) return
        if (active) {
            val timedoor = timedoor
            if (timedoor == null && age > 10) openTimedoor()
            else if(timedoor != null && (age) % 20 == 0) {
                transfer {
                    if (chronons?.extract(CommonConfig.TimeDoor.costToPersist) == CommonConfig.TimeDoor.costToPersist) {
                        timedoor.maxLifeTime += 20
                        timedoor.linkedPortalEntity?.let { it.maxLifeTime += 20 }
                    }
                }
            }
        }
        age++
    }

    fun openTimedoor() {
        val level = level
        if(level == null || level !is ServerLevel) return
        val nearby = level.getEntitiesOfClass(ServerPlayer::class.java, AABB(blockPos).inflate(5.0))
        safeLet(selectedPos, upgrades, chronons, owner) { pos, upgrades, chronons, player ->
            pos.get(upgrades, chronons)?.let {
                var provider: Identifier? = null
                var id: UUID? = null
                (selectedPos as? IndirectLocation)?.let { provider = it.provider; id = it.id }
                TimedoorEntity.openTimedoor(player, this, provider, id, it, pos.calculateCost(level, blockPos, upgrades, chronons), getSizing()) {
                    this.timedoorId = it.uuid
                    it.yRot = portalOffset.angle.toFloat() + direction.toYRot() + 180
                }?.let { msg ->
                    nearby.forEach { it.sendOverlayMessage(msg) }
                }
            }
        }
    }

    fun getSizing(): TimedoorPlacementSettings {
        val (x, y, z) = portalOffset.calcOffset(direction)
        return if (portalOffset.isUpright) {
            VerticalPlacementSettings(x, y, z)
        } else {
            FloorPlacementSettings(x, y, z)
        }
    }


    override fun loadAdditional(input: ValueInput) {
        super.loadAdditional(input)
        active = input.getBooleanOr("active", false)
        selectedPos = input.read("selectedPos", LocationGetter.codec).getOrNull()
        portalOffset = input.read("portalOffset", PortalPlacementComponent.codec).getOrNull() ?: portalOffset
        owner = input.read("owner", GAME_PROFILE_CODEC.codec()).getOrNull()
        timedoorId = input.read("timedoorId", UUIDUtil.CODEC).getOrNull()
        locked = input.getBooleanOr("locked", false)
    }

    override fun saveAdditional(output: ValueOutput) {
        super.saveAdditional(output)
        output.putBoolean("active", active)
        selectedPos?.let { output.store("selectedPos", LocationGetter.codec, it) }
        output.store("portalOffset", PortalPlacementComponent.codec, portalOffset)
        owner?.let { output.store("owner", GAME_PROFILE_CODEC.codec(), it) }
        timedoorId?.let { output.store("timedoorId", UUIDUtil.CODEC, it) }
        output.putBoolean("locked", locked)
    }
}