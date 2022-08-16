package me.codexadrian.tempad.registry;

import dev.architectury.injectables.annotations.ExpectPlatform;
import me.codexadrian.tempad.blocks.TempadBaseBlock;
import me.codexadrian.tempad.blocks.TempadBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class TempadBlocks {

    public static final Supplier<Block> TEMPAD_BLOCK = registerBlockWithItem("tempad_manager", () -> new TempadBaseBlock(BlockBehaviour.Properties.of(Material.HEAVY_METAL)));

    public static final Supplier<BlockEntityType<TempadBlockEntity>> TEMPAD_BLOCK_ENTITY = registerBlockEntity("tempad_manager_entity", () -> createBlockEntityType(TempadBlockEntity::new, TEMPAD_BLOCK.get()));

    public static <T extends Block> Supplier<T> registerBlockWithItem(String id, Supplier<T> block) {
        var tempBlock = registerBlock(id, block);
        TempadItems.registerItem(id, () -> new BlockItem(tempBlock.get(), new Item.Properties()));
        return tempBlock;
    }

    @ExpectPlatform
    public static <T extends Block> Supplier<T> registerBlock(String id, Supplier<T> item) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static <E extends BlockEntity, T extends BlockEntityType<E>> Supplier<T> registerBlockEntity(String id, Supplier<T> item) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static <E extends BlockEntity> BlockEntityType<E> createBlockEntityType(BlockEntityFactory<E> factory, Block... blocks) {
        throw new AssertionError();
    }

    public static void register() {

    }

    @FunctionalInterface
    public interface BlockEntityFactory<T extends BlockEntity> {
        @NotNull T create(BlockPos blockPos, BlockState blockState);
    }
}
