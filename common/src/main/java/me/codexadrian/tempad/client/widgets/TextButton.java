package me.codexadrian.tempad.client.widgets;

import com.mojang.blaze3d.vertex.PoseStack;
import com.teamresourceful.resourcefullib.client.CloseablePoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.time.Instant;

public class TextButton extends Button {
    private float padding;
    private final int color;
    private final OnPress dynamicPress;
    private final boolean isCentered;
    private Instant disabledUntil;

    public TextButton(int x, int y, int height, Component component, int color, OnPress onPress) {
        this(x, y, height, component, color, false, onPress, null);
    }

    public TextButton(int x, int y, int height, Component component, int color, OnPress onPress, @Nullable Instant diabledUntil) {
        this(x, y, height, component, color, false, onPress, diabledUntil);
    }

    public TextButton(int x, int y, int height, Component component, int color, boolean isCentered, OnPress onPress, @Nullable Instant disabledUntil) {
        super(x, y, Minecraft.getInstance().font.width(component), Minecraft.getInstance().font.lineHeight, component, onPress, Button.DEFAULT_NARRATION);
        this.dynamicPress = onPress;
        this.height = height;
        this.width = Minecraft.getInstance().font.width(getMessage());
        this.color = color;
        this.isCentered = isCentered;
        if(disabledUntil != null) this.disabledUntil = disabledUntil;
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return mouseX >= getX() && mouseX <= getX() + getWidth() && mouseY >= getY() && mouseY <= getY() + getHeight();
    }

    @Override
    public int getWidth() {
        Font font = Minecraft.getInstance().font;
        return (int) (font.width(getMessage()) * (getHeight() / 8F));
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        super.onClick(mouseX, mouseY);
    }

    @Override
    public void renderWidget(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        Minecraft minecraft = Minecraft.getInstance();
        Font font = minecraft.font;

        try (var pose = new CloseablePoseStack(graphics)) {
            boolean disabled = disabledUntil == null || Instant.now().isAfter(disabledUntil);
            int color = isMouseOver(mouseX, mouseY) && disabled ? this.color : getOffColor();
            //drawCenteredString(matrices, font, getMessage(), x + (TempadScreen.this.width - WIDTH) / 2, y + (TempadScreen.this.height - HEIGHT) / 2, color);
            if(isCentered) {
                graphics.drawCenteredString(font, getMessage(), getX(), getY(), color);
            } else {
                graphics.drawString(font, getMessage(), getX(), getY(), color);
            }
        }
    }

    @Override
    public void onPress() {
        this.dynamicPress.onPress(this);
    }

    @Override
    public int getHeight() {
        return height;
    }

    private int getOffColor() {
        Color c0 = Color.getColor("", color);

        double r = 0.5 * c0.getRed() + 0.5;
        double g = 0.5 * c0.getGreen() + 0.5;
        double b = 0.5 * c0.getBlue() + 0.5;
        double a = 1;

        return new Color((int) r, (int) g, (int) b, (int) a).getRGB();
    }

    @Override
    public @NotNull Component getMessage() {
        if(disabledUntil == null) return super.getMessage();
        if(Instant.now().isBefore(disabledUntil)) return super.getMessage().copy().withStyle(ChatFormatting.STRIKETHROUGH);
        return super.getMessage();
    }
}
