package me.codexadrian.tempad.common.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.bytecodecs.base.ByteCodec;
import com.teamresourceful.bytecodecs.base.object.ObjectByteCodec;
import com.teamresourceful.resourcefullib.common.codecs.CodecExtras;
import com.teamresourceful.resourcefullib.common.utils.CommonUtils;
import me.codexadrian.tempad.common.Tempad;
import me.codexadrian.tempad.common.utils.CodecUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

public record LocationData(String name, ResourceKey<Level> levelKey, BlockPos blockPos, float angle, UUID id, boolean isDeletable, boolean isDownloadable) implements Comparable<LocationData> {
    public static ByteCodec<LocationData> BYTE_CODEC = ObjectByteCodec.create(
        ByteCodec.STRING.fieldOf(LocationData::name),
        CodecUtils.DIMENSION.fieldOf(LocationData::levelKey),
        CodecUtils.BLOCK_POS.fieldOf(LocationData::blockPos),
        ByteCodec.FLOAT.fieldOf(LocationData::angle),
        ByteCodec.UUID.fieldOf(LocationData::id),
        ByteCodec.BOOLEAN.fieldOf(LocationData::isDeletable),
        ByteCodec.BOOLEAN.fieldOf(LocationData::isDownloadable),
        LocationData::new
    );

    public static Codec<LocationData> CODEC = RecordCodecBuilder.create(
        instance -> instance.group(
            Codec.STRING.fieldOf("name").forGetter(LocationData::name),
            Level.RESOURCE_KEY_CODEC.fieldOf("levelKey").forGetter(LocationData::levelKey),
            BlockPos.CODEC.fieldOf("blockpos").forGetter(LocationData::blockPos),
            Codec.FLOAT.optionalFieldOf("angle", 0f).forGetter(LocationData::angle),
            UUIDUtil.CODEC.fieldOf("uuid").forGetter(LocationData::id),
            Codec.BOOL.optionalFieldOf("isDeletable", true).forGetter(LocationData::isDeletable),
            Codec.BOOL.optionalFieldOf("isDownloadable", true).forGetter(LocationData::isDownloadable)
        ).apply(instance, LocationData::new)
    );

    public LocationData(String name, @Nullable ResourceKey<Level> levelKey, BlockPos pos, float angle) {
        this(name, levelKey, pos, angle, UUID.randomUUID());
    }

    public LocationData(String name, @Nullable ResourceKey<Level> levelKey, BlockPos pos, float angle, UUID uuid) {
        this(name, levelKey, pos, angle, uuid, true, true);
    }

    public LocationData(String name, @Nullable ResourceKey<Level> levelKey, BlockPos pos) {
        this(name, levelKey, pos, 0, UUID.randomUUID(), true, true);
    }

    public LocationData(String name, @Nullable ResourceKey<Level> levelKey, BlockPos pos, UUID uuid) {
        this(name, levelKey, pos, 0, uuid, true, true);
    }

    public CompoundTag toTag() {
        CompoundTag tag = new CompoundTag();
        tag.putUUID("uuid", id);
        tag.putString("name", name);
        tag.put("blockpos", NbtUtils.writeBlockPos(blockPos));
        Level.RESOURCE_KEY_CODEC.encodeStart(NbtOps.INSTANCE, levelKey).result().ifPresent(levelKey -> tag.put("levelKey", levelKey));

        return tag;
    }

    public static LocationData fromTag(CompoundTag tag) {
        UUID uuid = tag.getUUID("uuid");
        String name = tag.getString("name");
        BlockPos pos = NbtUtils.readBlockPos(tag.getCompound("blockpos"));
        ResourceKey<Level> key = Level.RESOURCE_KEY_CODEC.parse(NbtOps.INSTANCE, tag.get("levelKey")).result().orElse(null);

        return new LocationData(name, key, pos, uuid);
    }

    @Override
    public int compareTo(@NotNull LocationData o) {
        return name().compareTo(o.name());
    }
}
