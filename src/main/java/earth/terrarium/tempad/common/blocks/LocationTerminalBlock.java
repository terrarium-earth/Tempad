package earth.terrarium.tempad.common.blocks;

import com.mojang.serialization.MapCodec;
import earth.terrarium.tempad.api.locations.LocationData;
import earth.terrarium.tempad.common.data.TempadLocationHandler;
import earth.terrarium.tempad.common.items.LocationCard;
import earth.terrarium.tempad.common.items.TempadItem;
import earth.terrarium.tempad.common.registry.TempadRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class LocationTerminalBlock extends BaseEntityBlock {
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final BooleanProperty HAS_CARD = BooleanProperty.create("has_card");
    public static final MapCodec<LocationTerminalBlock> CODEC = simpleCodec(LocationTerminalBlock::new);

    public static final VoxelShape NORTH_SHAPE = Block.box(1, 1, 14, 15, 15, 16);
    public static final VoxelShape SOUTH_SHAPE = Block.box(1, 1, 0, 15, 15, 2);
    public static final VoxelShape EAST_SHAPE = Block.box(0, 1, 1, 2, 15, 15);
    public static final VoxelShape WEST_SHAPE = Block.box(14, 1, 1, 16, 15, 15);

    public LocationTerminalBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(FACING, Direction.NORTH).setValue(HAS_CARD, false));
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite()).setValue(HAS_CARD, false);
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING).add(HAS_CARD);
    }

    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING))).setValue(HAS_CARD, state.getValue(HAS_CARD));
    }

    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING))).setValue(HAS_CARD, state.getValue(HAS_CARD));
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new LocationTerminalBlockEntity(pos, state);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            ItemStack heldItem = player.getItemInHand(hand);
            if (blockEntity instanceof LocationTerminalBlockEntity terminal) {
                if ((heldItem.is(TempadRegistry.LOCATION_CARD.get()) && LocationCard.getLocation(heldItem) != null) || heldItem.isEmpty()) {
                    ItemStack copy = terminal.getContainer().getItem(0).copy();
                    terminal.getContainer().setItem(0, player.getMainHandItem().copy());
                    player.setItemInHand(hand, copy);
                    if (terminal.getContainer().isEmpty()) {
                        level.setBlockAndUpdate(pos, state.setValue(HAS_CARD, false));
                    } else {
                        level.setBlockAndUpdate(pos, state.setValue(HAS_CARD, true));
                    }
                    terminal.setChanged();
                } else if (heldItem.getItem() instanceof TempadItem && !terminal.getContainer().isEmpty()) {
                    ItemStack card = terminal.getContainer().getItem(0);
                    LocationData location = LocationCard.getLocation(card);
                    if (location != null) {
                        TempadLocationHandler.addLocation(level, player.getUUID(), location);
                        player.displayClientMessage(Component.translatable("item.tempad.location_card.added_location", Component.literal(location.name()).withStyle(ChatFormatting.GOLD)), true);
                    }
                }
            }
        }
        return InteractionResult.SUCCESS;
    }


    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return switch (state.getValue(FACING)) {
            case NORTH -> NORTH_SHAPE;
            case SOUTH -> SOUTH_SHAPE;
            case EAST -> EAST_SHAPE;
            case WEST -> WEST_SHAPE;
            default -> super.getShape(state, level, pos, context);
        };
    }
}
