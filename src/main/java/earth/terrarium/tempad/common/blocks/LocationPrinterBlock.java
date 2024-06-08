package earth.terrarium.tempad.common.blocks;

import com.mojang.serialization.MapCodec;
import earth.terrarium.botarium.common.menu.MenuHooks;
import earth.terrarium.tempad.common.containers.PrinterContainer;
import earth.terrarium.tempad.common.registry.TempadRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class LocationPrinterBlock extends BaseEntityBlock {
    public static final MapCodec<LocationPrinterBlock> CODEC = simpleCodec(LocationPrinterBlock::new);

    public LocationPrinterBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new LocationPrinterBlockEntity(pos, state);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        } else if (level.getBlockEntity(pos) instanceof LocationPrinterBlockEntity blockEntity) {
            MenuHooks.openMenu((ServerPlayer) player, blockEntity);
            return InteractionResult.CONSUME;
        }
        return InteractionResult.PASS;
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
        ItemStack printer = TempadRegistry.PRINTER_BLOCK.get().getDefaultInstance();
        BlockEntity parameter = params.getParameter(LootContextParams.BLOCK_ENTITY);
        if (parameter instanceof LocationPrinterBlockEntity blockEntity) {
            PrinterContainer itemContainer = blockEntity.getItemContainer(parameter.getLevel(), parameter.getBlockPos(), state, blockEntity, null);
            if (itemContainer != null) {
                itemContainer.serialize(printer.getOrCreateTag());
            }
        }
        return List.of(printer);
    }
}
