package earth.terrarium.tempad.client.components;

import earth.terrarium.tempad.client.config.TempadClientConfig;
import earth.terrarium.tempad.common.Tempad;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;

public class ModWidgets {

    private static final Component DOWNLOAD = Component.translatable("gui." + Tempad.MODID + ".download");
    private static final Component DOWNLOAD_DISABLED = Component.translatable("gui." + Tempad.MODID + ".download_disabled");

    private static final Component TELEPORT = Component.translatable("gui." + Tempad.MODID + ".teleport");
    private static final Component TELEPORT_DISABLED = Component.translatable("gui." + Tempad.MODID + ".teleport_disabled");

    private static final Component DELETE = Component.translatable("gui." + Tempad.MODID + ".delete");
    private static final Component DELETE_DISABLED = Component.translatable("gui." + Tempad.MODID + ".delete_disabled");

    private static final Component FAVORITE = Component.translatable("gui." + Tempad.MODID + ".favorite");
    private static final Component UNFAVORITE = Component.translatable("gui." + Tempad.MODID + ".unfavorite");

    private static final Component SEARCH = Component.translatable("gui." + Tempad.MODID + ".search_field");
    private static final Component ADD = Component.translatable("gui." + Tempad.MODID + ".add_location");

    public static SpriteButton download(int x, int y, int width, int height, Runnable onPress) {
        SpriteButton button = SpriteButton.hidden(x, y, width, height, ModSprites.DOWNLOAD, onPress);
        button.setTooltip(Tooltip.create(DOWNLOAD));
        button.setDisabledTooltip(Tooltip.create(DOWNLOAD_DISABLED));
        return button;
    }

    public static SpriteButton teleport(int x, int y, int width, int height, Runnable onPress) {
        SpriteButton button = SpriteButton.hidden(x, y, width, height, ModSprites.TELEPORT, onPress);
        button.setTooltip(Tooltip.create(TELEPORT));
        button.setDisabledTooltip(Tooltip.create(TELEPORT_DISABLED));
        return button;
    }

    public static SpriteButton delete(int x, int y, int width, int height, Runnable onPress) {
        SpriteButton button = SpriteButton.hidden(x, y, width, height, ModSprites.DELETE, onPress);
        button.setTooltip(Tooltip.create(DELETE));
        button.setDisabledTooltip(Tooltip.create(DELETE_DISABLED));
        return button;
    }

    public static ToggleButton favorite(int x, int y, int width, int height, Runnable onPress) {
        ToggleButton button = new ToggleButton(x, y, width, height, ModSprites.UNFAVORITE, ModSprites.FAVORITE, b -> onPress.run());
        button.setTooltip(value -> Tooltip.create(value ? UNFAVORITE : FAVORITE));
        button.visible = false;
        return button;
    }

    public static EditBox search(int x, int y, int width, int height, Consumer<String> updater) {
        EditBox editBox = new EditBox(Minecraft.getInstance().font, x, y, width, height, SEARCH);
        editBox.setBordered(false);
        editBox.setHint(SEARCH);
        editBox.setTextColor(TempadClientConfig.color);
        editBox.setResponder(updater);
        return editBox;
    }

    public static SpriteButton add(int x, int y, int width, int height, Runnable onPress) {
        SpriteButton button = new SpriteButton(x, y, width, height, ModSprites.ADD, onPress);
        button.setTooltip(Tooltip.create(ADD));
        return button;
    }

}
