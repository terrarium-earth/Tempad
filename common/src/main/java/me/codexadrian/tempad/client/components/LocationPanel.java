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

    public LocationPanel(int x, int y, int width, int height, Consumer<@Nullable TextEntry> onSelection) {
        super(x, y, width, height, 10, onSelection);
    }

    public void update(String text, List<LocationData> locations, Predicate<UUID> isFavorite) {
        if (locations.isEmpty()) {
            updateEntries(EMPTY);

        } else {
            updateEntries(locations.stream()
                .filter(data -> text.isBlank() || data.getName().toLowerCase().contains(text.toLowerCase()))
                .map(locationData -> new TextEntry(locationData, locData -> isFavorite.test(locData.getId())))
                .toList());
        }
    }
}
