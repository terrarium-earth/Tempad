package earth.terrarium.tempad.common.entity

import com.mojang.authlib.GameProfile
import com.mojang.datafixers.util.Either
import com.teamresourceful.resourcefullib.common.color.Color
import earth.terrarium.tempad.Tempad
import earth.terrarium.tempad.api.ActionType
import earth.terrarium.tempad.api.context.SyncableContext
import earth.terrarium.tempad.api.event.TimedoorEvent
import earth.terrarium.tempad.api.locations.NamedGlobalVec3
import earth.terrarium.tempad.api.locations.offsetLocation
import earth.terrarium.tempad.api.sizing.DoorType
import earth.terrarium.tempad.api.sizing.DynamicAngledPlacement
import earth.terrarium.tempad.api.sizing.FloorPlacementSettings
import earth.terrarium.tempad.api.sizing.TimedoorPlacementSettings
import earth.terrarium.tempad.api.tva_device.chronons
import earth.terrarium.tempad.common.config.CommonConfig
import earth.terrarium.tempad.common.network.s2c.RotatePlayerMomentumPacket
import earth.terrarium.tempad.common.registries.ModEntities
import earth.terrarium.tempad.common.registries.ModSounds
import earth.terrarium.tempad.common.registries.ModTags
import earth.terrarium.tempad.common.registries.ageUntilAllowedThroughTimedoor
import earth.terrarium.tempad.common.utils.*
import net.minecraft.core.particles.DustParticleOptions
import net.minecraft.core.registries.Registries
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.*
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.portal.DimensionTransition
import net.minecraft.world.phys.Vec3
import java.util.*
import kotlin.jvm.optionals.getOrNull

class TimedoorEntity(type: EntityType<*>, level: Level) : Entity(type, level) {
    companion object {
        internal const val IDLE_BEFORE_START = 26
        internal const val ANIMATION_LENGTH = 5
        private val closingTimeAccessor = createDataKey<TimedoorEntity, Int>(EntityDataSerializers.INT)
        private val colorAccessor = createDataKey<TimedoorEntity, Color>(ModEntities.colorSerializer)
        private val targetPosAccessor = createDataKey<TimedoorEntity, Vec3>(ModEntities.vec3Serializer)
        private val targetDimAccessor =
            createDataKey<TimedoorEntity, ResourceKey<Level>>(ModEntities.dimensionKeySerializer)
        private val sizingAccessor = createDataKey<TimedoorEntity, TimedoorPlacementSettings>(ModEntities.sizingSerializer)
        private val glitchingAccessor = createDataKey<TimedoorEntity, Boolean>(EntityDataSerializers.BOOLEAN)
        private val offsetAccessor = createDataKey<TimedoorEntity, Int>(EntityDataSerializers.INT)

        //feedback
        val fail = Component.translatable("entity.tempad.timedoor.fail")
        val posFail = Component.translatable("entity.tempad.timedoor.fail.pos")
        val interDimFail = Component.translatable("entity.tempad.timedoor.fail.interdimensional")
        val intraDimAllFail = Component.translatable("entity.tempad.timedoor.fail.interdimensional_all")
        val intraDimFail = Component.translatable("entity.tempad.timedoor.fail.interdimensional")
        val enteringFail = Component.translatable("entity.tempad.timedoor.fail.entering")
        val leavingFail = Component.translatable("entity.tempad.timedoor.fail.leaving")
        val noChrononsFail = Component.translatable("entity.tempad.timedoor.fail.no_chronons")

        fun openTimedoor(
            player: Player,
            ctx: SyncableContext<*>,
            provider: ResourceLocation?,
            locationId: UUID?,
            location: NamedGlobalVec3,
            onOpen: (TimedoorEntity) -> Unit = {},
        ): Component? {
            val stack = ctx.stack
            if (!player.isCreative && (stack.chronons?.extract(CommonConfig.TimeDoor.costPerDoor, ActionType.Simulate) ?: 0) < CommonConfig.TimeDoor.costPerDoor) return noChrononsFail

            val result = getTimedoor(player.level(), location)
            result.right().getOrNull()?.let { return it }

            val timedoor = result.left().getOrNull() ?: return fail

            timedoor.owner = player.uuid
            timedoor.sizing = if (player.xRot > 45) FloorPlacementSettings() else DynamicAngledPlacement()
            timedoor.sizing.placeTimedoor(DoorType.ENTRY, player.position(), player.yRot, timedoor)

            val event = TimedoorEvent.OpenWithItem(timedoor, player.gameProfile, ctx, provider, locationId).post()
            if (event.isCanceled) return event.errorMessage ?: fail
            else logTimedoorOpen(player.name.string, location, timedoor)

            if (!player.isCreative) {
                stack.chronons?.extract(CommonConfig.TimeDoor.costPerDoor, ActionType.Execute)
                player.cooldowns.addCooldown(stack.item, 40)
            }

            player.level().addFreshEntity(timedoor)
            onOpen(timedoor)
            timedoor.tryInitReceivingPortal()
            return null
        }

        fun openTimedoor(
            player: GameProfile,
            block: BlockEntity,
            provider: ResourceLocation?,
            locationId: UUID?,
            location: NamedGlobalVec3,
            sizing: TimedoorPlacementSettings = DynamicAngledPlacement(),
            onOpen: (TimedoorEntity) -> Unit = {},
        ): Component? {
            if ((block.chronons?.extract(CommonConfig.TimeDoor.costPerDoor, ActionType.Simulate) ?: 0) < CommonConfig.TimeDoor.costPerDoor) return noChrononsFail
            val result = getTimedoor(block.level!!, location)
            result.right().getOrNull()?.let { return it }

            val timedoor = result.left().getOrNull() ?: return fail
            timedoor.owner = player.id
            timedoor.sizing = sizing
            timedoor.sizing.placeTimedoor(
                DoorType.ENTRY,
                Vec3.atCenterOf(block.blockPos).add(0.0, -1.5, 0.0),
                block.blockState.getValue(BlockStateProperties.HORIZONTAL_FACING).toYRot() + 180,
                timedoor
            )

            val event = TimedoorEvent.OpenWithBlock(timedoor, player, block, provider, locationId).post()

            if (event.isCanceled) return event.errorMessage ?: fail
            else logTimedoorOpen(player.name, location, timedoor)

            block.chronons?.extract(CommonConfig.TimeDoor.costPerDoor, ActionType.Execute)
            block.setChanged()
            block.level!!.sendBlockUpdated(block.blockPos, block.blockState, block.blockState, Block.UPDATE_ALL)

            block.level!!.addFreshEntity(timedoor)
            onOpen(timedoor)
            timedoor.tryInitReceivingPortal()
            return null
        }

        fun getTimedoor(
            level: Level,
            location: NamedGlobalVec3,
            ignoreRestrictions: Boolean = false,
        ): Either<TimedoorEntity, Component> {
            val lookup = level.registryAccess().lookup(Registries.DIMENSION)
            val targetHolder = lookup.get().get(location.dimension).getOrNull() ?: return Either.right(posFail)
            val sourceHolder = lookup.get().get(level.dimension()).getOrNull() ?: return Either.right(posFail)
            if (!ignoreRestrictions) {
                level.dimension().let {
                    if (it != location.dimension) {
                        if (!CommonConfig.allowInterdimensionalTravel) return Either.right(interDimFail)
                        if (sourceHolder in ModTags.leavingNotSupported) return Either.right(leavingFail)
                        if (targetHolder in ModTags.enteringNotSupported) return Either.right(enteringFail)
                    } else {
                        if (!CommonConfig.allowIntradimensionalTravel) return Either.right(intraDimAllFail)
                        if (sourceHolder in ModTags.intradimensionalTravelNotSupported) return Either.right(intraDimFail)
                    }
                }
            }

            return Either.left(TimedoorEntity(ModEntities.timedoor, level).apply {
                setLocation(location)
            })
        }

        private fun logTimedoorOpen(player: String, location: NamedGlobalVec3, timedoor: TimedoorEntity) {
            if (CommonConfig.TimeDoor.logWhenOpen) {
                Tempad.logger.debug(
                    "Player {} opened a timedoor at {} in dimension {} to {} in dimension {}",
                    player,
                    timedoor.blockPosition(),
                    timedoor.level().dimension(),
                    location.name,
                    timedoor.targetDimension
                )
            }
        }
    }

    var beganClosing = 0;
    var targetAngle = 0f
    var targetPos by DataDelegate(targetPosAccessor)
    var targetDimension by DataDelegate(targetDimAccessor)
    var color by DataDelegate(colorAccessor)
    var closingTime by DataDelegate(closingTimeAccessor)
    var owner: UUID? = null
    var glitching: Boolean by DataDelegate(glitchingAccessor)
    var animationOffset: Int by DataDelegate(offsetAccessor)
    var linkedPortalId: UUID? = null
    var original: Boolean = true

    var sizing: TimedoorPlacementSettings
        get() = entityData.get(sizingAccessor)
        set(value) {
            entityData.set(sizingAccessor, value)
            this.fixupDimensions()
        }

    val linkedPortalEntity: TimedoorEntity?
        get() = linkedPortalId?.let { targetLevel?.entities?.get(it) } as? TimedoorEntity

    private val targetLevel: ServerLevel?
        get() = targetDimension.let { level().server[it] }

    private val selfLocation: NamedGlobalVec3
        get() = NamedGlobalVec3(
            name,
            offsetLocation(this.pos, this.yRot),
            level().dimension(),
            yRot,
            color
        )


    private fun canTeleport(entity: Entity, targetLevel: Level): Boolean {
        with(sizing) {
            return entity !is TimedoorEntity
                    && isInside(entity)
                    && entity !in ModTags.teleportingNotSupport
                    && entity.canChangeDimensions(level(), targetLevel)
                    && !entity.isPassenger
                    && entity.ageUntilAllowedThroughTimedoor?.let { entity.tickCount > it } ?: true
        }
    }

    override fun defineSynchedData(builder: SynchedEntityData.Builder) {
        builder.define(closingTimeAccessor, CommonConfig.TimeDoor.timeInWorld)
        builder.define(colorAccessor, Tempad.ORANGE)
        builder.define(targetPosAccessor, Vec3.ZERO)
        builder.define(targetDimAccessor, Level.OVERWORLD)
        builder.define(sizingAccessor, DynamicAngledPlacement())
        builder.define(glitchingAccessor, false)
        builder.define(offsetAccessor, 0)
    }

    override fun saveWithoutId(compound: CompoundTag): CompoundTag {
        val tag = super.saveWithoutId(compound)
        tag.putInt("Age", tickCount)
        tag.putInt("ClosingTime", closingTime)
        tag.putFloat("TargetAngle", targetAngle)
        tag.putBoolean("IsGlitching", glitching)
        tag.putBoolean("IsOriginal", original)
        tag.putString("LinkedPortalId", linkedPortalId.toString())
        tag.save(Color.CODEC, "Color", color)
        tag.save(Vec3.CODEC, "TargetPos", targetPos)
        tag.save(ResourceKey.codec(Registries.DIMENSION), "TargetDimension", targetDimension)
        tag.save(TimedoorPlacementSettings.codec, "PlacementSettings", sizing)
        return tag
    }

    override fun load(compound: CompoundTag) {
        super.load(compound)
        tickCount = compound.getInt("Age")
        closingTime = compound.getInt("ClosingTime")
        targetAngle = compound.getFloat("TargetAngle")
        glitching = compound.getBoolean("IsGlitching")
        original = compound.getBoolean("IsOriginal")
        linkedPortalId = compound.getString("LinkedPortalId").takeUnless { it.isBlank() }?.let { UUID.fromString(it) }
        compound.load(Color.CODEC, "Color")?.let { color = it }
        compound.load(Vec3.CODEC, "TargetPos")?.let { targetPos = it }
        compound.load(ResourceKey.codec(Registries.DIMENSION), "TargetDimension")?.let { targetDimension = it }
        compound.load(TimedoorPlacementSettings.codec, "PlacementSettings")?.let { sizing = it }
    }

    override fun getDimensions(pose: Pose): EntityDimensions = sizing.dimensions

    override fun isAlwaysTicking() = true

    override fun tick() {
        super.tick()
        if (level().isClientSide()) {
            if (sizing.dimensions.width != bbWidth || sizing.dimensions.height != bbHeight) {
                this.fixupDimensions()
                this.boundingBox = makeBoundingBox()
            }
            var percent = (tickCount / IDLE_BEFORE_START.toDouble())
            if (tickCount < IDLE_BEFORE_START && random.nextDouble() < percent) {
                percent *= 0.25
                val y = this.y + bbHeight / 2.0
                level().addParticle(
                    DustParticleOptions(color.vec3f, 1.0f),
                    true,
                    x + random.nextDouble() * percent - percent / 2,
                    y + random.nextDouble() * percent - percent / 2,
                    z + random.nextDouble() * percent - percent / 2,
                    0.0,
                    0.0,
                    0.0,
                )
            }
            return
        }
        closingTime--
        tryClose()
        if (tickCount < IDLE_BEFORE_START + ANIMATION_LENGTH || closingTime < ANIMATION_LENGTH) {
            return
        }
        val targetLevel = targetLevel ?: return
        val entities = level().getEntities<Entity>(boundingBox) { canTeleport(it, targetLevel) }
        if (entities.isNotEmpty()) tryInitReceivingPortal()
        for (entity in entities) {
            val event = TimedoorEvent.Enter(this, entity).post()
            if (event.isCanceled) continue

            if (entity.level().dimension() == targetLevel.dimension()) {
                entity.deltaMovement = entity.deltaMovement.yRot(this.yRot - targetAngle)
                entity.teleportTo(
                    targetLevel,
                    targetPos.x,
                    targetPos.y,
                    targetPos.z,
                    RelativeMovement.ALL,
                    targetAngle,
                    entity.xRot
                )
                entity.hasImpulse = true
                if (entity is Player) {
                    RotatePlayerMomentumPacket(this.yRot - targetAngle).sendToClient(entity)
                }
                entity.ageUntilAllowedThroughTimedoor = entity.tickCount + 30
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
                entity.ageUntilAllowedThroughTimedoor = entity.tickCount + 60
            }

            linkedPortalEntity?.let { TimedoorEvent.Exit(it, entity).post() }
        }
    }

    private fun tryInitReceivingPortal() {
        if ((closingTime <= 0 && closingTime != -1) || !original) return
        val targetLevel = targetLevel ?: return
        linkedPortalEntity?.let { return }
        val targetPortal = TimedoorEntity(ModEntities.timedoor, targetLevel)
        Tempad.ticketController.forceChunk(level() as ServerLevel, targetPortal, chunkPosition().x, chunkPosition().z, true, false)
        targetPortal.linkedPortalId = this.uuid
        targetPortal.closingTime = this.closingTime
        targetPortal.setLocation(selfLocation)
        targetPortal.sizing = this.sizing
        targetPortal.glitching = this.glitching
        targetPortal.original = false
        sizing.placeTimedoor(DoorType.EXIT, targetPos, targetAngle + 180f, targetPortal)
        linkedPortalId = targetPortal.uuid
        targetLevel.addFreshEntity(targetPortal)
    }

    private fun tryClose() {
        if (closingTime <= 0 && closingTime != -1) {
            TimedoorEvent.Close(this).post()
            this.linkedPortalEntity?.linkedPortalId = null
            this.discard()
        }
    }

    override fun remove(reason: RemovalReason) {
        super.remove(reason)
        if(original) this.linkedPortalEntity?.remove(reason)
        Tempad.ticketController.forceChunk(level() as ServerLevel, this, chunkPosition().x, chunkPosition().z, false, false)
    }

    fun setLocation(location: NamedGlobalVec3) {
        this.targetPos = location.pos
        this.targetDimension = location.dimension
        this.customName = location.name
        this.targetAngle = location.angle
        this.color = location.color
    }

    override fun fireImmune(): Boolean = true

    override fun readAdditionalSaveData(pCompound: CompoundTag) {}
    override fun addAdditionalSaveData(pCompound: CompoundTag) {}

    override fun onAddedToLevel() {
        super.onAddedToLevel()
        if (level().isClientSide) return
        Tempad.ticketController.forceChunk(level() as ServerLevel, this, chunkPosition().x, chunkPosition().z, true, false)
        level().playSound(null, blockPosition(), ModSounds.timedoorOpen, soundSource, 1.0f, 1.0f)
    }
}
