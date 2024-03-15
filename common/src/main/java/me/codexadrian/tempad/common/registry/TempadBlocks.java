package me.codexadrian.tempad.common.registry;

import com.teamresourceful.resourcefullib.common.registry.RegistryEntry;
import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistries;
import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistry;
import me.codexadrian.tempad.common.Tempad;
import me.codexadrian.tempad.common.blocks.LocationPrinterBlock;
import me.codexadrian.tempad.common.blocks.LocationTerminalBlock;
import me.codexadrian.tempad.common.blocks.WarpBlock;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class TempadBlocks {
    public static final ResourcefulRegistry<Block> REGISTRY = ResourcefulRegistries.create(BuiltInRegistries.BLOCK, Tempad.MODID);

    public static final RegistryEntry<Block> LOCATION_PRINTER = REGISTRY.register("location_printer", () -> new LocationPrinterBlock(BlockBehaviour.Properties.of()));
    public static final RegistryEntry<Block> WARP_PAD = REGISTRY.register("warp_pad", () -> new WarpBlock(BlockBehaviour.Properties.of()));
    public static final RegistryEntry<Block> LOCATION_TERMINAL = REGISTRY.register("location_terminal", () -> new LocationTerminalBlock(BlockBehaviour.Properties.of()));
}
