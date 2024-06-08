package earth.terrarium.tempad.client.components;

import it.unimi.dsi.fastutil.booleans.Boolean2ObjectFunction;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.resources.ResourceLocation;

public class ToggleButton extends Button {

    private final WidgetSprites selectedSprites;
    private final WidgetSprites unselectedSprites;

    private boolean selected = false;
    private Boolean2ObjectFunction<Tooltip> tooltip = value -> Tooltip.create(CommonComponents.EMPTY);

    public ToggleButton(int x, int y, int width, int height, WidgetSprites selectedSprites, WidgetSprites unselectedSprites, OnPress onPress) {
        super(x, y, width, height, CommonComponents.EMPTY, onPress, DEFAULT_NARRATION);
        this.selectedSprites = selectedSprites;
        this.unselectedSprites = unselectedSprites;
    }

    @Override
    public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        WidgetSprites sprites = this.selected ? this.selectedSprites : this.unselectedSprites;
        ResourceLocation resourceLocation = sprites.get(this.isActive(), this.isHovered());
        graphics.blitSprite(resourceLocation, this.getX(), this.getY(), this.width, this.height);
    }

    @Override
    public void onPress() {
        this.selected = !this.selected;
        this.setTooltip(this.tooltip.get(this.selected));
        super.onPress();
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public void setTooltip(Boolean2ObjectFunction<Tooltip> tooltip) {
        this.tooltip = tooltip == null ? value -> Tooltip.create(CommonComponents.EMPTY) : tooltip;
        this.setTooltip(this.tooltip.get(this.selected));
    }
}
