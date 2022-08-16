package me.codexadrian.tempad.registry.fabric;

import me.codexadrian.tempad.Constants;
import me.codexadrian.tempad.registry.TempadBlocks;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.function.Supplier;

public class TempadBlocksImpl {
    public static <T extends Block> Supplier<T> registerBlock(String id, Supplier<T> item) {
        var registry = Registry.register(Registry.BLOCK, new ResourceLocation(Constants.MODID, id), item.get());
        return () -> registry;
    }

    public static <E extends BlockEntity, T extends BlockEntityType<E>> Supplier<T> registerBlockEntity(String id, Supplier<T> item) {
        var registry = Registry.register(Registry.BLOCK_ENTITY_TYPE, new ResourceLocation(Constants.MODID, id), item.get());
        return () -> registry;
    }

    public static <E extends BlockEntity> BlockEntityType<E> createBlockEntityType(TempadBlocks.BlockEntityFactory<E> factory, Block... blocks) {
        return FabricBlockEntityTypeBuilder.create(factory::create, blocks).build();
    }
}
