package me.codexadrian.tempad.blocks;

import me.codexadrian.tempad.Tempad;
import me.codexadrian.tempad.data.LocationData;
import me.codexadrian.tempad.entity.TimedoorEntity;
import me.codexadrian.tempad.registry.TempadBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class TempadBlockEntity extends BlockEntity implements WorldlyContainer, ExtraDataMenuProvider {
    private final NonNullList<ItemStack> inventory;
    public LocationData teleportLocation;
    private @Nullable UUID linkedEntity;

    public TempadBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(TempadBlocks.TEMPAD_BLOCK_ENTITY.get(), blockPos, blockState);
        inventory = NonNullList.withSize(12, ItemStack.EMPTY);
    }

    @Override
    public int[] getSlotsForFace(@NotNull Direction direction) {
        return new int[0];
    }

    @Override
    public boolean canPlaceItemThroughFace(int i, @NotNull ItemStack itemStack, @Nullable Direction direction) {
        return false;
    }

    @Override
    public boolean canTakeItemThroughFace(int i, @NotNull ItemStack itemStack, @NotNull Direction direction) {
        return false;
    }

    @Override
    public void load(@NotNull CompoundTag compoundTag) {
        super.load(compoundTag);
        ContainerHelper.loadAllItems(compoundTag, this.inventory);
        if(compoundTag.contains("TargetLocation")) {
            teleportLocation = LocationData.fromTag(compoundTag.getCompound("TargetLocation"));
        }
        if(compoundTag.contains("LinkedEntity")) {
            linkedEntity = compoundTag.getUUID("LinkedEntity");
        }
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag compoundTag) {
        super.saveAdditional(compoundTag);
        ContainerHelper.saveAllItems(compoundTag, this.inventory);
        if(teleportLocation != null) {
            compoundTag.put("TargetLocation", teleportLocation.toTag());
        }
        if(linkedEntity != null) {
            compoundTag.putUUID("LinkedEntity", linkedEntity);
        }
    }

    @Override
    public int getContainerSize() {
        return 12;
    }

    @Override
    public boolean isEmpty() {
        return inventory.isEmpty();
    }

    @Override
    public ItemStack getItem(int i) {
        return inventory.get(i);
    }

    @Override
    public ItemStack removeItem(int i, int j) {
        return ContainerHelper.removeItem(inventory, i, j);
    }

    @Override
    public ItemStack removeItemNoUpdate(int i) {
        return ContainerHelper.takeItem(inventory, i);
    }

    @Override
    public void setItem(int i, @NotNull ItemStack itemStack) {
        inventory.set(i, itemStack);
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return player.distanceToSqr(this.getBlockPos().getX(), this.getBlockPos().getY(), this.getBlockPos().getZ()) <= 64;
    }

    @Override
    public void clearContent() {
        this.inventory.clear();
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("gui.tempad.block_menu");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int i, @NotNull Inventory inventory, @NotNull Player player) {
        return new TempadBlockMenu(this, i, inventory, this.getBlockPos());
    }

    @Override
    public void writeExtraData(ServerPlayer player, FriendlyByteBuf buffer) {
        buffer.writeBlockPos(this.getBlockPos());
    }

    public boolean hasLinkedTimedoor() {
        return linkedEntity != null;
    }

    public void linkTimedoor(TimedoorEntity timedoor) {
        this.linkedEntity = timedoor.getUUID();
    }

    @Nullable
    public TimedoorEntity getLinkedTimedoor() {
        if(getLevel() instanceof ServerLevel serverLevel && linkedEntity != null) {
            Entity entity = serverLevel.getEntity(linkedEntity);
            if(entity instanceof TimedoorEntity timedoor) {
                return timedoor;
            }
        }
        return null;
    }

    public void unlinkTimedoor() {
        TimedoorEntity linkedTimedoor = this.getLinkedTimedoor();
        if(linkedTimedoor != null) {
            linkedTimedoor.setClosingTime(linkedTimedoor.tickCount + Tempad.getTempadConfig().getTimedoorAddWaitTime());
        }
        this.linkedEntity = null;
    }

    public boolean hasFuel() {
        return true; //return this.inventory.stream().filter(itemStack -> itemStack.getItem() == TempadBlocks.TEMPAD_FUEL.get().asItem()).findFirst().isPresent();
    }
}
