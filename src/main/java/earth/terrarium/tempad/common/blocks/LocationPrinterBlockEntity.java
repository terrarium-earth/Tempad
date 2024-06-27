package earth.terrarium.tempad.common.blocks;

import earth.terrarium.botarium.common.item.ItemContainerBlock;
import earth.terrarium.botarium.common.item.SerializableContainer;
import earth.terrarium.botarium.common.item.base.BotariumItemBlock;
import earth.terrarium.botarium.common.menu.ExtraDataMenuProvider;
import earth.terrarium.tempad.api.locations.LocationApi;
import earth.terrarium.tempad.common.containers.PrinterContainer;
import earth.terrarium.tempad.api.locations.LocationData;
import earth.terrarium.tempad.common.menu.PrinterMenu;
import earth.terrarium.tempad.common.registry.TempadBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class LocationPrinterBlockEntity extends BlockEntity implements BotariumItemBlock<PrinterContainer>, ExtraDataMenuProvider {
    private PrinterContainer container;

    public LocationPrinterBlockEntity(BlockPos pos, BlockState blockState) {
        super(TempadBlockEntities.LOCATION_PRINTER.get(), pos, blockState);
    }

    @Override
    public void writeExtraData(ServerPlayer player, FriendlyByteBuf buffer) {
        LocationData.BYTE_CODEC.listOf().encode(LocationApi.API.getAllAsList(player.level(), player.getUUID()), buffer);
    }

    @Override
    public Component getDisplayName() {
        return this.getBlockState().getBlock().getName();
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
        return new PrinterMenu(i, inventory, getItemContainer(level, getBlockPos(), null, null, null), LocationApi.API.getAllAsList(player.level(), player.getUUID()));
    }

    @Override
    public @Nullable PrinterContainer getItemContainer(Level level, BlockPos pos, @Nullable BlockState state, @Nullable BlockEntity entity, @Nullable Direction direction) {
        return container == null ? container = new PrinterContainer(level, pos) : container;
    }
}
