package earth.terrarium.tempad.client.components;

import com.teamresourceful.resourcefullib.client.components.selection.ListEntry;
import com.teamresourceful.resourcefullib.client.scissor.ScissorBoxStack;
import earth.terrarium.tempad.client.config.TempadClientConfig;
import earth.terrarium.tempad.api.locations.LocationData;
import earth.terrarium.tempad.common.utils.ClientUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public class TextEntry extends ListEntry implements Comparable<TextEntry> {

    public final LocationData data;
    private Function<LocationData, Boolean> isFavorite = (data) -> false;
    private final Component component;
    private boolean isFocused = false;
    private final boolean isSelectable;
    private final int color;
    private boolean shadow = true;

    public TextEntry(LocationData data, Function<LocationData, Boolean> isFavorite) {
        this.data = data;
        this.component = Component.translatable(data.name());
        this.isFavorite = isFavorite;
        isSelectable = true;
        this.color = TempadClientConfig.color;
    }

    public TextEntry(Component component) {
        this.data = null;
        this.component = component;
        isSelectable = false;
        this.color = TempadClientConfig.color;
    }

    public TextEntry(Component component, int color, boolean shadow) {
        this.data = null;
        this.component = component;
        isSelectable = false;
        this.color = color;
        this.shadow = shadow;
    }

    @Override
    protected void render(@NotNull GuiGraphics graphics, @NotNull ScissorBoxStack stack, int id, int left, int top, int width, int height, int mouseX, int mouseY, boolean hovered, float partialTick, boolean selected) {
        boolean isMouseOver = mouseX > left && mouseX < left + width && mouseY > top && mouseY < top + height;

        if (isSelectable && selected) {
            graphics.fill(left, top, left + width, top + height, TempadClientConfig.color);
        }

        Component renderedComponent = isFavorite.apply(data) ? Component.literal("❤ ").append(component) : component;
        int color = (selected && isSelectable) ? 0xAA000000 : isMouseOver || !isSelectable ? this.color : ClientUtils.darkenColor(this.color);

        graphics.drawString(Minecraft.getInstance().font, renderedComponent, left + 2, top + 2, color, shadow && !(selected && isSelectable));
    }

    @Override
    public void setFocused(boolean bl) {
        isFocused = bl;
    }

    @Override
    public boolean isFocused() {
        return isFocused;
    }

    public boolean isFavorite() {
        return isFavorite.apply(data);
    }

    @Override
    public int compareTo(@NotNull TextEntry o) {
        int compare = Boolean.compare(
            this.data != null && isFavorite(),
            o.data != null && o.isFavorite()
        );
        if (compare != 0) return compare;
        return component.getString().compareTo(o.component.getString());
    }
}
