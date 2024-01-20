package me.codexadrian.tempad.client.components;

import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.WidgetSprites;
import org.jetbrains.annotations.Nullable;

public class SpriteButton extends ImageButton {

    private Tooltip activeTooltip;
    private Tooltip disabledTooltip;

    public SpriteButton(int x, int y, int width, int height, WidgetSprites sprites, Runnable onPress) {
        super(x, y, width, height, sprites, b -> onPress.run());
    }

    public static SpriteButton hidden(int x, int y, int width, int height, WidgetSprites sprites, Runnable onPress) {
        SpriteButton button = new SpriteButton(x, y, width, height, sprites, onPress);
        button.visible = false;
        return button;
    }

    public void setActivated(boolean activated) {
        this.active = activated;
        updateTooltip();
    }

    public void setDisabledTooltip(Tooltip tooltip) {
        this.disabledTooltip = tooltip;
        updateTooltip();
    }

    @Override
    public void setTooltip(@Nullable Tooltip tooltip) {
        this.activeTooltip = tooltip;
        updateTooltip();
    }

    private void updateTooltip() {
        super.setTooltip(this.active ? this.activeTooltip : this.disabledTooltip);
    }

}

