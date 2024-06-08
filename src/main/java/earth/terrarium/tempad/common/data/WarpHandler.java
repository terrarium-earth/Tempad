package earth.terrarium.tempad.common.data;

import com.teamresourceful.resourcefullib.common.utils.SaveHandler;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

import java.util.*;

public class WarpHandler extends SaveHandler {
    private static final WarpHandler CLIENT_ONLY = new WarpHandler();

    private final Map<UUID, ConfiguredLocationData> cachedWarps = new HashMap<>();

    public static WarpHandler read(Level level) {
        return read(level, HandlerType.create(CLIENT_ONLY, WarpHandler::new), "tempad_warp_locations");
    }

    @Override
    public void loadData(CompoundTag tag) {
        tag.getList("warps", Tag.TAG_COMPOUND).forEach(warp -> {
            CompoundTag warpTag = (CompoundTag) warp;
            UUID uuid = warpTag.getUUID("uuid");
            ConfiguredLocationData location = ConfiguredLocationData.CODEC.parse(NbtOps.INSTANCE, warpTag.get("locationData")).result().orElseThrow();
            cachedWarps.put(uuid, location);
        });
    }

    @Override
    public void saveData(CompoundTag tag) {
        ListTag warps = new ListTag();
        cachedWarps.forEach((uuid, globalPos) -> {
            CompoundTag warp = new CompoundTag();
            warp.putUUID("uuid", uuid);
            warp.put("locationData", ConfiguredLocationData.CODEC.encodeStart(NbtOps.INSTANCE, globalPos).result().orElse(new CompoundTag()));
            warps.add(warp);
        });
        tag.put("warps", warps);
    }

    public static void addWarp(Level level, UUID id, ConfiguredLocationData data) {
        WarpHandler handler = WarpHandler.read(level);
        handler.cachedWarps.put(id, data);
        handler.setDirty(true);
    }

    public static void removeWarp(Level level, UUID id) {
        WarpHandler handler = WarpHandler.read(level);
        handler.cachedWarps.remove(id);
        handler.setDirty(true);
    }

    public static Map<UUID, LocationData> getWarps(ServerLevel level, UUID player) {
        WarpHandler handler = WarpHandler.read(level);
        return handler.cachedWarps.values().stream().filter(configuredLocationData -> configuredLocationData.canTeleport(level, player)).map(ConfiguredLocationData::location).collect(HashMap::new, (map, location) -> map.put(location.id(), location), HashMap::putAll);
    }
}
