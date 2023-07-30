package me.codexadrian.tempad.client.widgets;

import com.teamresourceful.resourcefullib.client.components.selection.ListEntry;
import com.teamresourceful.resourcefullib.client.scissor.ScissorBoxStack;
import me.codexadrian.tempad.common.data.LocationData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.jetbrains.annotations.NotNull;

public class TextEntry extends ListEntry {
    public final LocationData data;
    private boolean isFocused = false;

    public TextEntry(LocationData data) {
        this.data = data;
    }

    @Override
    protected void render(@NotNull GuiGraphics graphics, @NotNull ScissorBoxStack stack, int id, int left, int top, int width, int height, int mouseX, int mouseY, boolean hovered, float partialTick, boolean selected) {
        boolean isMouseOver = mouseX >= left && mouseX <= left + width && mouseY >= top && mouseY <= top + height;
        MutableComponent translatable = Component.translatable(data.getName());
        graphics.drawString(Minecraft.getInstance().font, selected ? Component.literal("➡ ").append(translatable) : translatable, left + 2, top + 2, isMouseOver || selected ? 0xFFFFFFFF : 0xFFAAAAAA);
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
