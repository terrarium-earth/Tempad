package earth.terrarium.tempad.common.entity

import com.mojang.authlib.GameProfile
import com.mojang.datafixers.util.Either
import com.teamresourceful.resourcefullib.common.color.Color
import earth.terrarium.tempad.Tempad
import earth.terrarium.tempad.api.capabilities.chronons
import earth.terrarium.tempad.api.event.TimedoorEvent
import earth.terrarium.tempad.api.locations.NamedGlobalVec3
import earth.terrarium.tempad.api.locations.offsetLocation
import earth.terrarium.tempad.api.sizing.DoorType
import earth.terrarium.tempad.api.sizing.DynamicAngledPlacement
import earth.terrarium.tempad.api.sizing.FloorPlacementSettings
import earth.terrarium.tempad.api.sizing.TimedoorPlacementSettings
import earth.terrarium.tempad.common.config.CommonConfig
import earth.terrarium.tempad.common.network.s2c.RotatePlayerMomentumPacket
import earth.terrarium.tempad.common.registries.ModEntities
import earth.terrarium.tempad.common.registries.ModSounds
import earth.terrarium.tempad.common.registries.ModTags
import earth.terrarium.tempad.common.registries.ageUntilAllowedThroughTimedoor
import earth.terrarium.tempad.common.utils.*
import net.minecraft.core.BlockPos
import net.minecraft.core.Holder
import net.minecraft.core.particles.DustParticleOptions
import net.minecraft.core.registries.Registries
import net.minecraft.network.chat.Component
import net.minecraft.network.protocol.game.ClientboundSoundEntityPacket
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.resources.Identifier
import net.minecraft.resources.ResourceKey
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.server.level.TicketType
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.AnimationState
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityDimensions
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.Pose
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.ChunkPos
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.portal.TeleportTransition
import net.minecraft.world.level.storage.ValueInput
import net.minecraft.world.level.storage.ValueOutput
import net.minecraft.world.phys.Vec3
import net.neoforged.neoforge.transfer.access.ItemAccess
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
        private val sizingAccessor =
            createDataKey<TimedoorEntity, TimedoorPlacementSettings>(ModEntities.sizingSerializer)
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
            access: ItemAccess,
            provider: Identifier?,
            locationId: UUID?,
            location: NamedGlobalVec3,
            onOpen: (TimedoorEntity) -> Unit = {},
        ): Component? {
            val stack = access.stack
            val result = getTimedoor(player.level(), location)
            result.right().getOrNull()?.let { return it }

            val timedoor = result.left().getOrNull() ?: return fail

            timedoor.owner = player.uuid
            timedoor.sizing = if (player.xRot > 45) FloorPlacementSettings() else DynamicAngledPlacement()
            timedoor.sizing.placeTimedoor(DoorType.ENTRY, player.position(), player.yRot, timedoor)

            val event = TimedoorEvent.OpenWithItem(timedoor, player.gameProfile, access, provider, locationId).post()
            if (event.isCanceled) return event.errorMessage ?: fail
            else logTimedoorOpen(player.name.string, location, timedoor)

            var success = false
            if (!player.isCreative) {
                transfer {
                    success =
                        access.chronons?.extract(CommonConfig.TimeDoor.costPerDoor) == CommonConfig.TimeDoor.costPerDoor
                    if (success) {
                        player.cooldowns.addCooldown(stack, 40)
                        commit()
                    }
                }
            }
            if (!success) return noChrononsFail

            player.level().addFreshEntity(timedoor)
            onOpen(timedoor)
            timedoor.tryInitReceivingPortal()
            return null
        }

        fun openTimedoor(
            player: GameProfile,
            block: BlockEntity,
            provider: Identifier?,
            locationId: UUID?,
            location: NamedGlobalVec3,
            sizing: TimedoorPlacementSettings = DynamicAngledPlacement(),
            onOpen: (TimedoorEntity) -> Unit = {},
        ): Component? {
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

            var success = false
            transfer {
                success =
                    block.chronons?.extract(CommonConfig.TimeDoor.costPerDoor) == CommonConfig.TimeDoor.costPerDoor
                if (success) commit()
            }
            if (!success) return noChrononsFail
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
            val lookup = level.registryAccess().lookup(Registries.DIMENSION_TYPE)
            val targetHolder = level.server?.getLevel(location.dimension)?.dimensionTypeRegistration()?.key?.let {
                lookup.get().get(it).getOrNull()
            } ?: return Either.right(posFail)
            val sourceHolder =
                level.dimensionTypeRegistration().key?.let { lookup.get().get(it).getOrNull() } ?: return Either.right(
                    posFail
                )
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

    val openingState = AnimationState()

    private fun canTeleport(entity: Entity, targetLevel: Level): Boolean {
        with(sizing) {
            return entity !is TimedoorEntity
                    && isInside(entity)
                    && entity !in ModTags.teleportingNotSupport
                    && entity.canTeleport(level(), targetLevel)
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

    override fun readAdditionalSaveData(input: ValueInput) {
        tickCount = input.getIntOr("Age", 0)
        closingTime = input.getIntOr("ClosingTime", 0)
        targetAngle = input.getFloatOr("TargetAngle", 0.0f)
        glitching = input.getBooleanOr("IsGlitching", false)
        original = input.getBooleanOr("IsOriginal", false)
        linkedPortalId =
            input.getString("LinkedPortalId").getOrNull()?.takeUnless { it.isBlank() }?.let { UUID.fromString(it) }
        input.read("Color", Color.CODEC).getOrNull()?.let { color = it }
        input.read("TargetPos", Vec3.CODEC).getOrNull()?.let { targetPos = it }
        input.read("TargetDimension", ResourceKey.codec(Registries.DIMENSION)).getOrNull()
            ?.let { targetDimension = it }
        input.read("PlacementSettings", TimedoorPlacementSettings.codec).getOrNull()?.let { sizing = it }
    }

    override fun addAdditionalSaveData(output: ValueOutput) {
        output.putInt("Age", tickCount)
        output.putInt("ClosingTime", closingTime)
        output.putFloat("TargetAngle", targetAngle)
        output.putBoolean("IsGlitching", glitching)
        output.putBoolean("IsOriginal", original)
        output.putString("LinkedPortalId", linkedPortalId.toString())
        output.store("Color", Color.CODEC, color)
        output.store("TargetPos", Vec3.CODEC, targetPos)
        output.store("TargetDimension", ResourceKey.codec(Registries.DIMENSION), targetDimension)
        output.store("PlacementSettings", TimedoorPlacementSettings.codec, sizing)
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
                    DustParticleOptions(color.value, 1.0f),
                    true,
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
        if (entities.isNotEmpty()) {
            tryInitReceivingPortal()
            val pos = BlockPos.containing(targetPos)
            targetLevel.chunkSource.addTicketAndLoadWithRadius(TicketType.PORTAL, ChunkPos.containing(pos), 3)
        }
        for (entity in entities) {
            val event = TimedoorEvent.Enter(this, entity).post()
            if (event.isCanceled) continue

            (entity as? ServerPlayer)?.let {
                entity.connection.send(
                    ClientboundSoundEntityPacket(
                        Holder.direct(ModSounds.timedoorEnterMono),
                        soundSource,
                        this,
                        1.0f,
                        1.0f,
                        0
                    )
                )
                level().playSound(it, this, ModSounds.timedoorEnterStereo, soundSource, 1.0f, 1.0f)
            }

            if (entity.level().dimension() == targetLevel.dimension()) {
                entity.deltaMovement = entity.deltaMovement.yRot(this.yRot - targetAngle)
                entity.teleportTo(
                    targetPos.x,
                    targetPos.y,
                    targetPos.z,
                )
                if (entity is Player) {
                    RotatePlayerMomentumPacket(this.yRot - targetAngle).sendToClient(entity)
                }
                entity.ageUntilAllowedThroughTimedoor = entity.tickCount + 30
            } else {
                entity.teleport(
                    TeleportTransition(
                        targetLevel,
                        targetPos,
                        entity.deltaMovement,
                        targetAngle,
                        0.0F,
                        TeleportTransition.DO_NOTHING
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
        if (original) this.linkedPortalEntity?.remove(reason)
    }

    fun setLocation(location: NamedGlobalVec3) {
        this.targetPos = location.pos
        this.targetDimension = location.dimension
        this.customName = location.name
        this.targetAngle = location.angle
        this.color = location.color
    }

    override fun fireImmune(): Boolean = true

    override fun hurtServer(
        var1: ServerLevel,
        var2: DamageSource,
        var3: Float,
    ): Boolean = false

    override fun onAddedToLevel() {
        super.onAddedToLevel()
        if (level().isClientSide) return
        level().playSound(null, blockPosition(), ModSounds.timedoorOpen, soundSource, 1.0f, 1.0f)
    }
}
