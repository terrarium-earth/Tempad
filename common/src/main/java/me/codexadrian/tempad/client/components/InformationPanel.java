package me.codexadrian.tempad.client.components;

import com.teamresourceful.resourcefullib.client.components.selection.SelectionList;
import me.codexadrian.tempad.common.Tempad;
import me.codexadrian.tempad.common.data.LocationData;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

import java.util.List;

public class InformationPanel extends SelectionList<TextEntry> {

    private static final TextEntry NO_SELECTION_LINE_1 = new TextEntry(Component.translatable("gui." + Tempad.MODID + ".no_selection.first_line"));
    private static final TextEntry NO_SELECTION_LINE_2 = new TextEntry(Component.translatable("gui." + Tempad.MODID + ".no_selection.second_line"));
    private static final List<TextEntry> NO_SELECTION = List.of(NO_SELECTION_LINE_1, NO_SELECTION_LINE_2);

    private static final String LOCATION_X = "gui." + Tempad.MODID + ".x";
    private static final String LOCATION_Y = "gui." + Tempad.MODID + ".y";
    private static final String LOCATION_Z = "gui." + Tempad.MODID + ".z";
    private static final String LOCATION_DIMENSION = "gui." + Tempad.MODID + ".dimension";

    public InformationPanel(int x, int y, int width, int height) {
        super(x, y, width, height, 10, entry -> {});
    }

    public void update(LocationData location) {
        if (location == null) {
            updateEntries(NO_SELECTION);
        } else {
            updateEntries(List.of(
                new TextEntry(Component.literal(location.getName())),
                new TextEntry(Component.translatable(LOCATION_X, Mth.floor(location.getBlockPos().getX()))),
                new TextEntry(Component.translatable(LOCATION_Y, Mth.floor(location.getBlockPos().getY()))),
                new TextEntry(Component.translatable(LOCATION_Z, Mth.floor(location.getBlockPos().getZ()))),
                new TextEntry(Component.translatable(LOCATION_DIMENSION, Component.translatable(location.getLevelKey().location().toLanguageKey("dimension")))))
            );
        }
    }
}
