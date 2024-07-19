package earth.terrarium.tempad.common.entity

import com.teamresourceful.resourcefullib.common.color.Color
import earth.terrarium.tempad.Tempad
import earth.terrarium.tempad.api.event.TimedoorEvent
import earth.terrarium.tempad.common.config.CommonConfig
import earth.terrarium.tempad.api.locations.LocationData
import earth.terrarium.tempad.api.test.SyncableContext
import earth.terrarium.tempad.common.items.TempadItem
import earth.terrarium.tempad.common.registries.ModEntities
import earth.terrarium.tempad.common.utils.*
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.server.level.ServerLevel
import net.minecraft.util.Mth
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.level.portal.DimensionTransition
import net.minecraft.world.phys.Vec3
import net.neoforged.neoforge.common.NeoForge
import net.neoforged.neoforge.common.NeoForgeEventHandler
import net.neoforged.neoforge.common.Tags
import java.util.UUID

class TimedoorEntity(type: EntityType<*>, level: Level) : Entity(type, level) {
    companion object {
        internal const val ANIMATION_LENGTH = 8
        private val CLOSING_TIME = createDataKey<TimedoorEntity, Int>(EntityDataSerializers.INT)
        private val COLOR = createDataKey<TimedoorEntity, Color>(ModEntities.colorSerializaer)

        fun openTimedoor(player: Player, ctx: SyncableContext<*>, location: LocationData) {
            val stack = ctx.stack
            if (stack.item !is TempadItem) return
            val timedoor = TimedoorEntity(ModEntities.TIMEDOOR_ENTITY, player.level())
            timedoor.owner = player.uuid
            timedoor.pos = LocationData.offsetLocation(player.position(), player.yHeadRot, CommonConfig.TimeDoor.placementDistance)
            timedoor.yRot = -player.yHeadRot
            timedoor.targetLocation = location
            timedoor.closingTime = -1
            val event = TimedoorEvent.Open(timedoor, player, ctx).post()
            if (event.isCanceled) return
            player.level().addFreshEntity(timedoor)
        }
    }

    private fun isInside(entity: Entity): Boolean {
        val hypotenuse = (entity.x - x) * (entity.x - x) + (entity.z - z) * (entity.z - z)
        val alpha = Mth.atan2((entity.z - z), (entity.x - x)).toFloat()
        val theta = Mth.sin(alpha - yRot * Mth.DEG_TO_RAD)
        val maxDistance = 0.2 + entity.bbWidth / 2f
        return theta * theta * hypotenuse < maxDistance * maxDistance
    }

    private fun canTeleport(entity: Entity): Boolean {
        return (entity is LivingEntity || entity is ItemEntity) && isInside(entity) && entity !in Tags.EntityTypes.TELEPORTING_NOT_SUPPORTED
    }

    var color by DataDelegate(COLOR)

    var closingTime by DataDelegate(CLOSING_TIME)

    var targetLocation: LocationData? = null
        set(value) {
            field = value
            color = value?.color ?: Tempad.ORANGE
        }

    var linkedPortalEntity: TimedoorEntity? = null
        private set

    var owner: UUID? = null

    private val targetLevel: ServerLevel?
        get() = targetLocation?.dimension?.let { level().server[it] }

    private val selfLocation: LocationData
        get() = LocationData("Return from ${targetLocation?.name}", LocationData.offsetLocation(this.pos, this.yRot), level().dimension(), yRot, color)

    override fun defineSynchedData(pBuilder: SynchedEntityData.Builder) {
        pBuilder.define(CLOSING_TIME, CommonConfig.TimeDoor.idleAfterEnter)
        pBuilder.define(COLOR, Tempad.ORANGE)
    }

    override fun isAlwaysTicking() = true

    override fun tick() {
        val location = targetLocation
        val targetLevel = targetLevel
        if (location == null || targetLevel == null || level().isClientSide()) return
        val entities = level().getEntities<Entity>(boundingBox, ::canTeleport)
        if (entities.isEmpty()) {
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
                entity.teleportTo(location.pos)
                entity.yRot = location.angle
                entity.yHeadRot = location.angle
            } else {
                entity.changeDimension(
                    DimensionTransition(
                        targetLevel,
                        location.pos,
                        Vec3.ZERO,
                        location.angle,
                        0.0F,
                        false,
                        DimensionTransition.DO_NOTHING
                    )
                )
            }

            if (entity is Player && entity.uuid == owner) {
                this.closingTime = this.tickCount + CommonConfig.TimeDoor.idleAfterOwnerEnter
            }

            TimedoorEvent.Exit(linkedPortalEntity, entity).post()
        }
        tryClose()
    }

    private fun tryInitReceivingPortal() {
        val location = targetLocation
        val targetLevel = targetLevel
        if (location == null || targetLevel == null || linkedPortalEntity != null) return
        val targetPortal = TimedoorEntity(ModEntities.TIMEDOOR_ENTITY, targetLevel)
        targetPortal.pos = location.offsetLocation
        targetPortal.linkedPortalEntity = this
        targetPortal.color = color
        targetPortal.closingTime = CommonConfig.TimeDoor.idleAfterOwnerEnter
        targetPortal.targetLocation = selfLocation
        targetPortal.yRot = location.angle
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

    override fun onRemovedFromWorld() {
        super.onRemovedFromWorld()
        if (this.linkedPortalEntity != null) this.linkedPortalEntity!!.linkedPortalEntity = null
        this.linkedPortalEntity = null
    }

    fun resetClosingTime() {
        if (closingTime != -1) {
            closingTime = this.tickCount + CommonConfig.TimeDoor.idleAfterEnter
        }
    }

    override fun readAdditionalSaveData(pCompound: CompoundTag) {}
    override fun addAdditionalSaveData(pCompound: CompoundTag) {}
}
