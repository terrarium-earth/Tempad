package me.codexadrian.tempad.common.registry;

import com.teamresourceful.resourcefullib.common.registry.RegistryEntry;
import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistries;
import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistry;
import me.codexadrian.tempad.common.Tempad;
import me.codexadrian.tempad.common.blocks.LocationPrinterBlockEntity;
import me.codexadrian.tempad.common.blocks.LocationTerminalBlockEntity;
import me.codexadrian.tempad.common.blocks.WarpBlockEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class TempadBlockEntities {

    public static final ResourcefulRegistry<BlockEntityType<?>> REGISTRY = ResourcefulRegistries.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, Tempad.MODID);

    public static final RegistryEntry<BlockEntityType<?>> LOCATION_PRINTER = REGISTRY.register("location_printer", () -> BlockEntityType.Builder.of(LocationPrinterBlockEntity::new, TempadBlocks.LOCATION_PRINTER.get()).build(null));
    public static final RegistryEntry<BlockEntityType<WarpBlockEntity>> WARP_PAD = REGISTRY.register("warp_pad", () -> BlockEntityType.Builder.of(WarpBlockEntity::new, TempadBlocks.WARP_PAD.get()).build(null));
    public static final RegistryEntry<BlockEntityType<?>> LOCATION_TERMINAL = REGISTRY.register("location_terminal", () -> BlockEntityType.Builder.of(LocationTerminalBlockEntity::new, TempadBlocks.LOCATION_TERMINAL.get()).build(null));
}
