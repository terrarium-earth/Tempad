package me.codexadrian.tempad.common.data;

import com.teamresourceful.bytecodecs.base.ByteCodec;
import com.teamresourceful.bytecodecs.base.object.ObjectByteCodec;
import me.codexadrian.tempad.common.utils.CodecUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

public class LocationData implements Comparable<LocationData>{

    private final UUID id;
    private final String name;
    private final BlockPos blockPos;
    private final ResourceKey<Level> levelKey;
    private boolean isTeleportable;
    private boolean isDeletable;
    private boolean isDownloadable;

    public static ByteCodec<LocationData> CODEC = ObjectByteCodec.create(
        ByteCodec.STRING.fieldOf(LocationData::getName),
        CodecUtils.DIMENSION.optionalFieldOf(LocationData::getLevelKeyOptional),
        CodecUtils.BLOCK_POS.fieldOf(LocationData::getBlockPos),
        ByteCodec.UUID.fieldOf(LocationData::getId),
        ByteCodec.BOOLEAN.fieldOf(LocationData::isTeleportable),
        ByteCodec.BOOLEAN.fieldOf(LocationData::isDeletable),
        ByteCodec.BOOLEAN.fieldOf(LocationData::isDownloadable),
        LocationData::new
    );

    public LocationData(String name, @Nullable ResourceKey<Level> levelKey, BlockPos pos, UUID uuid, boolean isTeleportable, boolean isDeletable, boolean isDownloadable) {
        this.name = name;
        this.levelKey = levelKey;
        this.blockPos = pos;
        this.id = uuid;
        this.isTeleportable = isTeleportable;
        this.isDeletable = isDeletable;
        this.isDownloadable = isDownloadable;
    }

    public LocationData(String name, @Nullable ResourceKey<Level> levelKey, BlockPos pos, UUID uuid) {
        this(name, levelKey, pos, uuid, true, true, true);
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public LocationData(String name, Optional<ResourceKey<Level>> levelKey, BlockPos pos, UUID uuid, boolean isTeleportable, boolean isDeletable, boolean isDownloadable) {
        this(name, levelKey.orElse(null), pos, uuid, isTeleportable, isDeletable, isDownloadable);
    }

    public CompoundTag toTag() {
        CompoundTag tag = new CompoundTag();
        tag.putUUID("uuid", id);
        tag.putString("name", name);
        tag.put("blockpos", NbtUtils.writeBlockPos(blockPos));

        if(levelKey != null) {
            Optional<Tag> optTag = Level.RESOURCE_KEY_CODEC.encodeStart(NbtOps.INSTANCE, levelKey).result();
            tag.put("levelKey", optTag.get());
        }

        return tag;
    }

    public static LocationData fromTag(CompoundTag tag) {
        UUID uuid = tag.getUUID("uuid");
        String name = tag.getString("name");
        BlockPos pos = NbtUtils.readBlockPos(tag.getCompound("blockpos"));

        ResourceKey<Level> key = null;
        if (tag.contains("levelKey")) {
            Optional<ResourceKey<Level>> optKey = Level.RESOURCE_KEY_CODEC.parse(NbtOps.INSTANCE, tag.get("levelKey")).result();
            key = optKey.get();
        }

        return new LocationData(name, key, pos, uuid);
    }

    public UUID getId() {
        return id;
    }

    @Nullable
    public ResourceKey<Level> getLevelKey() {
        return levelKey;
    }

    public Optional<ResourceKey<Level>> getLevelKeyOptional() {
        return Optional.ofNullable(levelKey);
    }

    public String getName() {
        return name;
    }

    public BlockPos getBlockPos() {
        return blockPos;
    }

    public boolean isTeleportable() {
        return isTeleportable;
    }

    public boolean isDeletable() {
        return isDeletable;
    }

    public boolean isDownloadable() {
        return isDownloadable;
    }

    @Override
    public int compareTo(@NotNull LocationData o) {
        return getName().compareTo(o.getName());
    }
}
