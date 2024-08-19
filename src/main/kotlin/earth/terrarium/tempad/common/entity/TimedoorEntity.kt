package earth.terrarium.tempad.common.entity

import com.teamresourceful.resourcefullib.common.color.Color
import earth.terrarium.tempad.Tempad
import earth.terrarium.tempad.api.event.TimedoorEvent
import earth.terrarium.tempad.common.config.CommonConfig
import earth.terrarium.tempad.api.locations.StaticNamedGlobalPos
import earth.terrarium.tempad.api.context.SyncableContext
import earth.terrarium.tempad.api.locations.NamedGlobalPos
import earth.terrarium.tempad.api.locations.offsetLocation
import earth.terrarium.tempad.api.sizing.DefaultSizing
import earth.terrarium.tempad.api.sizing.TimedoorSizing
import earth.terrarium.tempad.common.items.TempadItem
import earth.terrarium.tempad.common.items.chrononContainer
import earth.terrarium.tempad.common.network.s2c.RotatePlayerMomentumPacket
import earth.terrarium.tempad.common.registries.ModEntities
import earth.terrarium.tempad.common.registries.chrononContent
import earth.terrarium.tempad.common.utils.*
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.resources.ResourceKey
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.*
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.level.portal.DimensionTransition
import net.minecraft.world.phys.Vec3
import net.neoforged.neoforge.common.Tags
import java.util.UUID

class TimedoorEntity(type: EntityType<*>, level: Level) : Entity(type, level) {
    companion object {
        internal const val ANIMATION_LENGTH = 8
        private val CLOSING_TIME = createDataKey<TimedoorEntity, Int>(EntityDataSerializers.INT)
        private val COLOR = createDataKey<TimedoorEntity, Color>(ModEntities.colorSerializer)
        private val TARGET_POS = createDataKey<TimedoorEntity, Vec3>(ModEntities.vec3Serializer)
        private val TARGET_DIMENSION = createDataKey<TimedoorEntity, ResourceKey<Level>>(ModEntities.dimensionKeySerializer)
        private val SIZING = createDataKey<TimedoorEntity, TimedoorSizing>(ModEntities.sizingSerializer)

        fun openTimedoor(player: Player, ctx: SyncableContext<*>, location: NamedGlobalPos) {
            val stack = ctx.stack
            if (!player.isCreative() && (stack.item !is TempadItem || stack.chrononContent < 1000 || location.pos == null || location.dimension == null)) return
            val timedoor = TimedoorEntity(ModEntities.TIMEDOOR_ENTITY, player.level())
            timedoor.owner = player.uuid
            timedoor.pos = offsetLocation(player.position(), player.yHeadRot, CommonConfig.TimeDoor.placementDistance)
            timedoor.yRot = player.yHeadRot
            timedoor.setLocation(location)
            // timedoor.closingTime = -1
            val event = TimedoorEvent.Open(timedoor, player, ctx).post()
            if (event.isCanceled) return
            stack.chrononContainer -= 1000
            player.level().addFreshEntity(timedoor)
        }
    }

    private fun canTeleport(entity: Entity): Boolean {
        with(sizing) {
            return entity !is TimedoorEntity && isInside(entity) && entity !in Tags.EntityTypes.TELEPORTING_NOT_SUPPORTED
        }
    }

    var targetAngle = 0f

    var targetPos by DataDelegate(TARGET_POS)
    var targetDimension by DataDelegate(TARGET_DIMENSION)
    var color by DataDelegate(COLOR)
    var closingTime by DataDelegate(CLOSING_TIME)
    var sizing: TimedoorSizing by DataDelegate(SIZING)

    var linkedPortalEntity: TimedoorEntity? = null
        private set

    var owner: UUID? = null

    private val targetLevel: ServerLevel?
        get() = targetDimension.let { level().server[it] }

    private val selfLocation: StaticNamedGlobalPos
        get() = StaticNamedGlobalPos(Component.translatable("misc.tempad.return", name), StaticNamedGlobalPos.offsetLocation(this.pos, this.yRot), level().dimension(), yRot, color)

    override fun defineSynchedData(pBuilder: SynchedEntityData.Builder) {
        pBuilder.define(CLOSING_TIME, CommonConfig.TimeDoor.idleAfterEnter)
        pBuilder.define(COLOR, Tempad.ORANGE)
        pBuilder.define(TARGET_POS, Vec3.ZERO)
        pBuilder.define(TARGET_DIMENSION, Level.OVERWORLD)
        pBuilder.define(SIZING, DefaultSizing.DEFAULT)
    }

    override fun getDimensions(pose: Pose): EntityDimensions = sizing.dimensions

    override fun isAlwaysTicking() = true

    override fun tick() {
        if (level().isClientSide()) return
        val targetLevel = targetLevel
        val entities = level().getEntities<Entity>(boundingBox, ::canTeleport)
        if (entities.isEmpty() || targetLevel == null) {
            tryClose()
            return
        }
        tryInitReceivingPortal()
        val linkedPortalEntity = linkedPortalEntity ?: return
        linkedPortalEntity.resetClosingTime()
        this.resetClosingTime()
        for (entity in entities) {
            val event = TimedoorEvent.Enter(this, entity).post()
            if (event.isCanceled) continue

            if (entity.level().dimension() == targetLevel.dimension()) {
                entity.deltaMovement = entity.deltaMovement.yRot(this.yRot - targetAngle)
                entity.teleportTo(targetLevel, targetPos.x, targetPos.y, targetPos.z, RelativeMovement.ALL, targetAngle, entity.xRot)
                entity.hasImpulse = true
                if (entity is Player) {
                    RotatePlayerMomentumPacket(this.yRot - targetAngle).sendToClient(entity)
                }
            } else {
                entity.changeDimension(
                    DimensionTransition(
                        targetLevel,
                        targetPos,
                        entity.deltaMovement,
                        targetAngle,
                        0.0F,
                        false,
                        DimensionTransition.DO_NOTHING
                    )
                )
            }

            if (entity is Player && entity.uuid == owner && this.closingTime != -1) {
                this.closingTime = this.tickCount + CommonConfig.TimeDoor.idleAfterOwnerEnter
            }

            TimedoorEvent.Exit(linkedPortalEntity, entity).post()
        }
        tryClose()
    }

    private fun tryInitReceivingPortal() {
        val targetLevel = targetLevel
        val targetPos = targetPos
        if (targetLevel == null || linkedPortalEntity != null) return
        val targetPortal = TimedoorEntity(ModEntities.TIMEDOOR_ENTITY, targetLevel)
        targetPortal.pos = offsetLocation(targetPos, targetAngle + 180)
        targetPortal.linkedPortalEntity = this
        targetPortal.closingTime = CommonConfig.TimeDoor.idleAfterOwnerEnter
        targetPortal.setLocation(selfLocation)
        targetPortal.yRot = targetAngle + 180f
        linkedPortalEntity = targetPortal
        targetLevel.addFreshEntity(targetPortal)
    }

    private fun tryClose() {
        if (tickCount > closingTime + ANIMATION_LENGTH && closingTime != -1) {
            TimedoorEvent.Close(this).post()
            this.linkedPortalEntity?.linkedPortalEntity = null
            this.discard()
        }
    }

    override fun onRemovedFromLevel() {
        super.onRemovedFromLevel()
        if (this.linkedPortalEntity != null) this.linkedPortalEntity!!.linkedPortalEntity = null
        this.linkedPortalEntity = null
    }

    fun resetClosingTime() {
        if (closingTime != -1) {
            closingTime = this.tickCount + CommonConfig.TimeDoor.idleAfterEnter
        }
    }

    fun setLocation(location: NamedGlobalPos) {
        this.targetPos = location.pos!!
        this.targetDimension = location.dimension!!
        this.customName = location.name
        this.targetAngle = location.angle
        this.color = location.color
    }

    override fun readAdditionalSaveData(pCompound: CompoundTag) {}
    override fun addAdditionalSaveData(pCompound: CompoundTag) {}
}
