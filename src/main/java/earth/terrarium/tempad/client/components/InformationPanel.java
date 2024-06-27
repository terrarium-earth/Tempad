package earth.terrarium.tempad.client.components;

import com.teamresourceful.resourcefullib.client.components.selection.SelectionList;
import earth.terrarium.tempad.client.config.TempadClientConfig;
import earth.terrarium.tempad.common.Tempad;
import earth.terrarium.tempad.api.locations.LocationData;
import net.minecraft.core.GlobalPos;
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
    private static final String ANGLE = "gui." + Tempad.MODID + ".angle";
    private static final String LOCATION_DIMENSION = "gui." + Tempad.MODID + ".dimension";
    private final int color;
    private final boolean shadow;

    public InformationPanel(int x, int y, int width, int height) {
        this(x, y, width, height, TempadClientConfig.color, true);
    }

    public InformationPanel(int x, int y, int width, int height, int color, boolean shadow) {
        super(x, y, width, height, 10, entry -> {});
        this.color = color;
        this.shadow = shadow;
    }

    public void updateBlank() {
        updateBlank(true);
    }

    public void updateBlank(boolean showNoSelection) {
        if (showNoSelection) {
            updateEntries(NO_SELECTION);
        } else {
            updateEntries(List.of());
        }
    }

    public void update(LocationData location) {
        updateEntries(List.of(
            new TextEntry(Component.literal(location.name()), color, shadow),
            new TextEntry(Component.translatable(LOCATION_X, Mth.floor(location.blockPos().getX())), color, shadow),
            new TextEntry(Component.translatable(LOCATION_Y, Mth.floor(location.blockPos().getY())), color, shadow),
            new TextEntry(Component.translatable(LOCATION_Z, Mth.floor(location.blockPos().getZ())), color, shadow),
            new TextEntry(Component.translatable(ANGLE, location.angle()), color, shadow),
            new TextEntry(Component.translatable(LOCATION_DIMENSION, Component.translatable(location.levelKey().location().toLanguageKey("dimension"))), color, shadow)
            )
        );
    }

    public void updateNameless(GlobalPos pos) {
        updateEntries(List.of(
            new TextEntry(Component.translatable(LOCATION_X, Mth.floor(pos.pos().getX())), color, shadow),
            new TextEntry(Component.translatable(LOCATION_Y, Mth.floor(pos.pos().getY())), color, shadow),
            new TextEntry(Component.translatable(LOCATION_Z, Mth.floor(pos.pos().getZ())), color, shadow),
            new TextEntry(Component.translatable(LOCATION_DIMENSION, Component.translatable(pos.dimension().location().toLanguageKey("dimension"))), color, shadow))
        );
    }
}
