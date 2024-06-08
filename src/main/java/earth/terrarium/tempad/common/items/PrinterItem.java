package earth.terrarium.tempad.common.items;

import dev.architectury.injectables.annotations.PlatformOnly;
import earth.terrarium.botarium.common.item.base.BotariumItemItem;
import earth.terrarium.botarium.common.menu.ExtraDataMenuProvider;
import earth.terrarium.botarium.common.menu.MenuHooks;
import earth.terrarium.tempad.api.locations.LocationApi;
import earth.terrarium.tempad.common.blocks.LocationPrinterBlockEntity;
import earth.terrarium.tempad.common.containers.PrinterContainer;
import earth.terrarium.tempad.common.data.LocationData;
import earth.terrarium.tempad.common.menu.PrinterMenu;
import earth.terrarium.tempad.common.utils.CodecUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class PrinterItem extends BlockItem implements BotariumItemItem<PrinterContainer> {
    public PrinterItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    protected boolean updateCustomBlockEntityTag(BlockPos pos, Level level, @Nullable Player player, ItemStack stack, BlockState state) {
        super.updateCustomBlockEntityTag(pos, level, player, stack, state);
        if (level.getBlockEntity(pos) instanceof LocationPrinterBlockEntity blockEntity)  {
            PrinterContainer itemContainer = blockEntity.getItemContainer(level, pos, state, blockEntity, null);
            if (itemContainer != null) {
                itemContainer.deserialize(stack.getOrCreateTag());
                blockEntity.setChanged();
            }
        }
        return true;
    }

    @Override
    public PrinterContainer getItemContainer(ItemStack stack) {
        return new PrinterContainer(stack);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        if (!player.isShiftKeyDown()) {
            if (player instanceof ServerPlayer serverPlayer) {
                MenuHooks.openMenu(serverPlayer, new PrinterMenuData(player.getItemInHand(usedHand)));
            } else {
                return InteractionResultHolder.success(player.getItemInHand(usedHand));
            }
        }
        return super.use(level, player, usedHand);
    }

    @Override
    public InteractionResult place(BlockPlaceContext context) {
        if (context.getPlayer() == null || context.getPlayer().isShiftKeyDown()) {
            return super.place(context);
        } else {
            return InteractionResult.PASS;
        }
    }

    @PlatformOnly(PlatformOnly.FABRIC)
    public boolean allowNbtUpdateAnimation(Player player, InteractionHand hand, ItemStack oldStack, ItemStack newStack) {
        return false;
    }

    @PlatformOnly("neoforge")
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return false;
    }

    public class PrinterMenuData implements ExtraDataMenuProvider {
        ItemStack stack;

        public PrinterMenuData(ItemStack stack) {
            this.stack = stack;
        }

        @Override
        public void writeExtraData(ServerPlayer player, FriendlyByteBuf buffer) {
            LocationData.BYTE_CODEC.listOf().encode(LocationApi.API.getAllAsList(player.level(), player.getUUID()), buffer);
        }

        @Override
        public Component getDisplayName() {
            return PrinterItem.this.getDescription();
        }

        @Nullable
        @Override
        public AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
            return new PrinterMenu(i, inventory, getItemContainer(stack), LocationApi.API.getAllAsList(player.level(), player.getUUID()));
        }
    }
}
