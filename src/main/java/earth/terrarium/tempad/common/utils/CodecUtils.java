package earth.terrarium.tempad.common.utils;

import com.teamresourceful.bytecodecs.base.ByteCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

public class CodecUtils {
    public static final ByteCodec<ResourceLocation> RESOURCE_LOCATION = ByteCodec.STRING.map(ResourceLocation::new, ResourceLocation::toString);
    public static final ByteCodec<ResourceKey<Level>> DIMENSION = resourceKeyByteCodec(Registries.DIMENSION);
    public static final ByteCodec<BlockPos> BLOCK_POS = ByteCodec.LONG.map(BlockPos::of, BlockPos::asLong);

    public static <T> ByteCodec<ResourceKey<T>> resourceKeyByteCodec(ResourceKey<? extends Registry<T>> registry) {
        return RESOURCE_LOCATION.map(resourceLocation -> ResourceKey.create(registry, resourceLocation), ResourceKey::location);
    }
}
