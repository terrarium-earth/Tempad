package earth.terrarium.tempad.common.entity;

import earth.terrarium.tempad.common.config.CommonConfig;
import earth.terrarium.tempad.api.locations.LocationData;
import earth.terrarium.tempad.common.registry.TempadRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;

public class TimedoorEntity extends Entity {
    public static final int ANIMATION_LENGTH = 8;
    private static final EntityDataAccessor<Integer> CLOSING_TIME = SynchedEntityData.defineId(TimedoorEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> COLOR = SynchedEntityData.defineId(TimedoorEntity.class, EntityDataSerializers.INT);
    private LocationData locationData = null;
    private UUID owner = null;
    private UUID linkedPortalId = null;
    private TimedoorEntity linkedPortalEntity = null;

    public TimedoorEntity(EntityType<TimedoorEntity> entityType, Level level) {
        super(entityType, level);
    }


    @Override
    public @NotNull Packet<ClientGamePacketListener> getAddEntityPacket() {
        return new ClientboundAddEntityPacket(this);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder pBuilder) {
        pBuilder.define(CLOSING_TIME, CommonConfig.timedoorWait);
        pBuilder.define(COLOR, Tempad.ORANGE);
    }

    @Override
    public void tick() {
        super.tick();
        AABB box = getBoundingBox();
        if (getTargetLocation() != null && !this.level().isClientSide()) {
            Predicate<Entity> isInside = (entity) -> {
                var hypotenuse = Math.sqrt((entity.getX() - getX()) * (entity.getX() - getX()) + (entity.getZ() - getZ()) * (entity.getZ() - getZ()));
                var alpha = Math.atan((entity.getZ() - getZ()) / (entity.getX() - getX()));
                var theta = alpha - Math.toRadians(getYRot());
                return Math.abs(Math.sin(theta) * hypotenuse) < 0.2 + entity.getBbWidth() / 2f;
            };
            Predicate<Entity> b = (entity) -> ((entity instanceof LivingEntity || entity instanceof ItemEntity) && !entity.getType().is(Tempad.TEMPAD_ENTITY_BLACKLIST)) && isInside.test(entity);
            List<Entity> entities = this.level().getEntities(Entity.class, box, b);
            MinecraftServer server = level().getServer();
            if (!entities.isEmpty() && server != null) {
                ServerLevel destinationLevel = server.getLevel(getTargetLocation().getGlobalPos().dimension());
                if (destinationLevel != null) {
                    entities.stream().flatMap(entity -> entity.getRootVehicle().getSelfAndPassengers()).distinct().forEach(entity -> {
                        entity.ejectPassengers();
                        Vec3 deltaMovement = new Vec3(entity.getDeltaMovement().x, entity.getDeltaMovement().y, entity.getDeltaMovement().z);
                        entity.changeDimension(destinationLevel, Vec3.atBottomCenterOf(getTargetLocation().getGlobalPos().pos()), deltaMovement, (int) getTargetLocation().getAngle(), entity);
                        entity.setDeltaMovement(deltaMovement);
                        entity.hurtMarked = true;
                        entity.hasImpulse = true;
                        if (getLinkedPortalEntity() != null) getLinkedPortalEntity().resetClosingTime();
                        this.resetClosingTime();
                        if (entity instanceof Player player) {
                            if (player.getUUID().equals(getOwner())) {
                                this.setClosingTime(this.tickCount + CommonConfig.timedoorOwnerCloseBehindTime);
                                if (getLinkedPortalEntity() != null)
                                    this.getLinkedPortalEntity().setClosingTime(getLinkedPortalEntity().tickCount + CommonConfig.timedoorOwnerCloseBehindTime);
                            }
                        }
                    });
                    if (getLinkedPortalEntity() == null) {
                        TimedoorEntity recipientPortal = new TimedoorEntity(TempadRegistry.TIMEDOOR_ENTITY.get(), destinationLevel);
                        recipientPortal.setOwner(this.getOwner());
                        recipientPortal.setClosingTime(CommonConfig.timedoorOwnerCloseBehindTime);
                        recipientPortal.setTargetLocation(null);
                        recipientPortal.setColor(this.getColor());
                        this.setLinkedPortalId(recipientPortal.getUUID());
                        recipientPortal.setLinkedPortalId(this.getUUID());
                        var position = getTargetLocation().getGlobalPos().pos();
                        double radians = Math.toRadians(getTargetLocation().getAngle() + 270);
                        recipientPortal.setPos(position.getX() + 0.5 + Math.cos(radians), position.getY(), position.getZ() + 0.5 + Math.sin(radians));
                        recipientPortal.setYRot(getTargetLocation().getAngle());
                        destinationLevel.addFreshEntity(recipientPortal);
                    }
                }
            }
            if (this.tickCount > getClosingTime() + ANIMATION_LENGTH && getClosingTime() != -1) {
                if (this.getLinkedPortalEntity() != null) this.getLinkedPortalEntity().setLinkedPortalId(null);
                this.setLinkedPortalId(null);
                this.discard();
            }
        }
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
    }

    public void setLocation(LocationData location) {
        this.locationData = location;
    }

    @Nullable
    public LocationData getLocation() {
        return locationData;
    }

    public int getClosingTime() {
        return entityData.get(CLOSING_TIME);
    }

    public void setClosingTime(int closingTime) {
        entityData.set(CLOSING_TIME, closingTime);
    }

    public UUID getOwner() {
        return owner;
    }

    public void setOwner(UUID owner) {
        this.owner = owner;
    }

    public UUID getLinkedPortalId() {
        return linkedPortalId;
    }

    public TimedoorEntity getLinkedPortalEntity() {
        if (!level().isClientSide() && linkedPortalEntity == null) {
            ServerLevel serverLevel = (ServerLevel) level();
            linkedPortalEntity = (TimedoorEntity) serverLevel.getEntity(linkedPortalId);
        }
        return linkedPortalEntity;
    }

    public void setLinkedPortalId(UUID id) {
        this.linkedPortalId = id;
        this.linkedPortalEntity = null;
    }

    public void setColor(int color) {
        entityData.set(COLOR, color);
    }

    public int getColor() {
        return entityData.get(COLOR);
    }

    public void resetClosingTime() {
        if (getClosingTime() != -1) {
            this.setClosingTime(this.tickCount + CommonConfig.timedoorWait);
        }
    }

    @Override
    public boolean canChangeDimensions() {
        return false;
    }
}
