package me.codexadrian.tempad.common.blocks;

import earth.terrarium.botarium.common.menu.ExtraDataMenuProvider;
import me.codexadrian.tempad.api.locations.LocationApi;
import me.codexadrian.tempad.common.data.LocationData;
import me.codexadrian.tempad.common.menu.PrinterMenu;
import me.codexadrian.tempad.common.registry.TempadBlockEntities;
import me.codexadrian.tempad.common.utils.CodecUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.WorldlyContainerHolder;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LocationPrinterBlockEntity extends BlockEntity implements WorldlyContainerHolder, ExtraDataMenuProvider {
    public PrinterContainer container = new PrinterContainer(this);

    public LocationPrinterBlockEntity(BlockPos pos, BlockState blockState) {
        super(TempadBlockEntities.LOCATION_PRINTER.get(), pos, blockState);
    }

    @Override
    public @NotNull WorldlyContainer getContainer(BlockState state, LevelAccessor level, BlockPos pos) {
        return container;
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        ContainerHelper.loadAllItems(tag, container.items);
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        ContainerHelper.saveAllItems(tag, container.items);
    }

    @Override
    public void writeExtraData(ServerPlayer player, FriendlyByteBuf buffer) {
        LocationData.BYTE_CODEC.listOf().encode(LocationApi.API.getAllAsList(player.level(), player.getUUID()), buffer);
        CodecUtils.BLOCK_POS.encode(this.getBlockPos(), buffer);
    }

    @Override
    public Component getDisplayName() {
        return this.getBlockState().getBlock().getName();
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
        return new PrinterMenu(i, inventory, container, LocationApi.API.getAllAsList(player.level(), player.getUUID()), this.getBlockPos());
    }
}
