package me.codexadrian.tempad.common.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.codexadrian.tempad.api.teams.TeamConfig;
import me.codexadrian.tempad.api.teams.TeamConfigApi;
import net.minecraft.core.UUIDUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

import java.util.UUID;

public record ConfiguredLocationData(LocationData location, ResourceLocation configId, UUID owner)  {
    public static final Codec<ConfiguredLocationData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            LocationData.CODEC.fieldOf("location").forGetter(ConfiguredLocationData::location),
            ResourceLocation.CODEC.fieldOf("configId").forGetter(ConfiguredLocationData::configId),
            UUIDUtil.CODEC.fieldOf("owner").forGetter(ConfiguredLocationData::owner)
    ).apply(instance, ConfiguredLocationData::new));

    public boolean canTeleport(Level level, UUID member) {
        TeamConfig config = TeamConfigApi.API.getConfig(configId);
        return config.test(level, owner, member);
    }
}
