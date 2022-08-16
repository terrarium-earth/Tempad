package me.codexadrian.tempad.client.gui.blockmanager;

import com.mojang.blaze3d.vertex.PoseStack;
import com.teamresourceful.resourcefullib.client.components.selection.ListEntry;
import com.teamresourceful.resourcefullib.client.scissor.ScissorBoxStack;
import me.codexadrian.tempad.TempadClient;
import me.codexadrian.tempad.data.LocationData;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class LocationEntry extends ListEntry {
    private final LocationData data;

    public LocationEntry(LocationData data) {
        super();
        this.data = data;
    }

    @Override
    protected void render(@NotNull ScissorBoxStack scissorBoxStack,@NotNull PoseStack stack, int id, int left, int top, int width, int height, int mouseX, int mouseY, boolean hovered, float partialTick, boolean selected) {
        Font font = Minecraft.getInstance().font;
        MutableComponent component = Component.literal(data.getName());
        if(hovered) component = component.withStyle(ChatFormatting.UNDERLINE);
        GuiComponent.drawString(stack, font, component, left, top + 1, selected ? TempadClient.getClientConfig().getColor() : getOffColor());
    }

    private int getOffColor() {
        Color c0 = Color.getColor("", TempadClient.getClientConfig().getColor());

        double r = 0.5 * c0.getRed() + 0.5;
        double g = 0.5 * c0.getGreen() + 0.5;
        double b = 0.5 * c0.getBlue() + 0.5;
        double a = 1;

        return new Color((int) r, (int) g, (int) b, (int) a).getRGB();
    }

    public LocationData getData() {
        return data;
    }
}
