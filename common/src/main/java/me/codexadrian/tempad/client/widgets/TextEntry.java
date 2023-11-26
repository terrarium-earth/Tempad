package me.codexadrian.tempad.client.widgets;

import com.teamresourceful.resourcefullib.client.components.selection.ListEntry;
import com.teamresourceful.resourcefullib.client.scissor.ScissorBoxStack;
import me.codexadrian.tempad.client.config.TempadClientConfig;
import me.codexadrian.tempad.common.data.LocationData;
import me.codexadrian.tempad.common.utils.ClientUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.jetbrains.annotations.NotNull;

public class TextEntry extends ListEntry {
    public final LocationData data;
    private final Component component;
    private boolean isFocused = false;
    private final boolean isSelectable;

    public TextEntry(LocationData data) {
        this.data = data;
        this.component = Component.translatable(data.getName());
        isSelectable = true;
    }

    public TextEntry(Component component) {
        this.data = null;
        this.component = component;
        isSelectable = false;
    }

    @Override
    protected void render(@NotNull GuiGraphics graphics, @NotNull ScissorBoxStack stack, int id, int left, int top, int width, int height, int mouseX, int mouseY, boolean hovered, float partialTick, boolean selected) {
        boolean isMouseOver = mouseX > left && mouseX < left + width && mouseY > top && mouseY < top + height;
        graphics.drawString(Minecraft.getInstance().font, selected && isSelectable ? Component.literal("➡ ").append(component) : component, left + 2, top + 2, isSelectable && (isMouseOver || selected) ? TempadClientConfig.color : ClientUtils.darkenColor(TempadClientConfig.color));
    }

    @Override
    public void setFocused(boolean bl) {
        isFocused = bl;
    }

    @Override
    public boolean isFocused() {
        return isFocused;
    }
}
