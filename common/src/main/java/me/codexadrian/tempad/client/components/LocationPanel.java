package me.codexadrian.tempad.client.components;

import com.teamresourceful.resourcefullib.client.components.selection.SelectionList;
import me.codexadrian.tempad.client.widgets.TextEntry;
import me.codexadrian.tempad.common.Tempad;
import me.codexadrian.tempad.common.data.LocationData;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class LocationPanel extends SelectionList<TextEntry> {

    private static final List<TextEntry> EMPTY = List.of(
        new TextEntry(Component.translatable("gui." + Tempad.MODID + ".no_locations.first_line")),
        new TextEntry(Component.translatable("gui." + Tempad.MODID + ".no_locations.second_line"))
    );

    private final List<LocationData> locations;
    private final Predicate<UUID> isFavorite;

    public LocationPanel(
        int x, int y, int width, int height,
        List<LocationData> locations, Predicate<UUID> isFavorite,
        Consumer<@Nullable TextEntry> onSelection
    ) {
        super(x, y, width, height, 10, onSelection);
        this.locations = locations;
        this.isFavorite = isFavorite;
    }

    public void update(String text) {
        if (this.locations.isEmpty()) {
            updateEntries(EMPTY);

        } else {
            updateEntries(this.locations.stream()
                .filter(data -> text.isBlank() || data.getName().toLowerCase().contains(text.toLowerCase()))
                .map(locationData -> new TextEntry(locationData, locData -> this.isFavorite.test(locData.getId())))
                .sorted(TextEntry::compareTo)
                .toList());
        }
    }
}
