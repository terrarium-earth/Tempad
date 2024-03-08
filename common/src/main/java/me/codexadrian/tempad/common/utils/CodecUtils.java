package me.codexadrian.tempad.common.utils;

import com.teamresourceful.bytecodecs.base.ByteCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

public class CodecUtils {
    public static final ByteCodec<ResourceKey<Level>> DIMENSION = ByteCodec.STRING.map(s -> ResourceKey.create(Registries.DIMENSION, new ResourceLocation(s)), levelResourceKey -> levelResourceKey.location().toString());
    public static final ByteCodec<BlockPos> BLOCK_POS = ByteCodec.LONG.map(BlockPos::of, BlockPos::asLong);
}
