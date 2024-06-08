package earth.terrarium.tempad.client.components;

import com.teamresourceful.resourcefullib.client.components.selection.SelectionList;
import earth.terrarium.tempad.common.Tempad;
import earth.terrarium.tempad.common.data.LocationData;
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
        super(x, y, width, height, 11, onSelection);
        this.locations = locations;
        this.isFavorite = isFavorite;
    }

    public void select(LocationData selected) {
        for (TextEntry entry : this.children()) {
            if (entry.data != null && entry.data.equals(selected)) {
                this.setSelected(entry);
                break;
            }
        }
    }

    public void update(String text) {
        if (this.locations.isEmpty()) {
            updateEntries(EMPTY);
        } else {
            updateEntries(this.locations.stream()
                .filter(data -> text.isBlank() || data.name().toLowerCase().contains(text.toLowerCase()))
                .map(locationData -> new TextEntry(locationData, locData -> this.isFavorite.test(locData.id())))
                .sorted(TextEntry::compareTo)
                .toList());
        }
    }
}
