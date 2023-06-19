package me.codexadrian.tempad.common.data;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TempadComponent {

    private final Map<UUID, LocationData> locations = new HashMap<>();

    public void toStack(ItemStack stack) {
        CompoundTag compoundTag = stack.getOrCreateTag();
        ListTag locationData = new ListTag();

        for (LocationData data : locations.values()) {
            locationData.add(data.toTag());
        }

        compoundTag.put("tempad_locations", locationData);

        stack.setTag(compoundTag);
    }

    public static TempadComponent fromStack(ItemStack stack) {
        TempadComponent component = new TempadComponent();
        CompoundTag compoundTag = stack.getOrCreateTag();

        ListTag locationData = compoundTag.getList("tempad_locations", 10);
        for (Tag dataTag : locationData) {
            component.addLocation(LocationData.fromTag((CompoundTag) dataTag));
        }

        return component;
    }

    public static void addStackLocation(ItemStack stack, LocationData data) {
        TempadComponent component = fromStack(stack);
        component.addLocation(data);
        component.toStack(stack);
    }

    public static void deleteStackLocation(ItemStack stack, UUID id) {
        TempadComponent component = fromStack(stack);
        component.removeLocation(id);
        component.toStack(stack);
    }

    public void addLocation(LocationData data) {
        this.locations.put(data.getId(), data);
    }

    public Collection<LocationData> getLocations() {
        return locations.values();
    }

    public void removeLocation(UUID id) {
        System.out.println("Removing Location with ID: " + id.toString());
        this.locations.remove(id);
    }
}
