package me.codexadrian.tempad.tempad;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

public class LocationData {

    private final UUID id;
    private final String name;
    private final BlockPos blockPos;
    private final ResourceKey<Level> levelKey;

    public LocationData(String name, @Nullable ResourceKey<Level> levelKey, BlockPos pos, UUID uuid) {
        this.name = name;
        this.levelKey = levelKey;
        this.blockPos = pos;
        this.id = uuid;
    }

    public LocationData(String name, @Nullable ResourceKey<Level> levelKey, BlockPos pos) {
        this(name, levelKey, pos, UUID.randomUUID());
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

    public String getName() {
        return name;
    }

    public BlockPos getBlockPos() {
        return blockPos;
    }
}
