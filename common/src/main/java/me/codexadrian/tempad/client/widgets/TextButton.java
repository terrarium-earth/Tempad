package me.codexadrian.tempad.client.widgets;

import com.teamresourceful.resourcefullib.client.CloseablePoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class TextButton extends Button {
    private final OnPress dynamicPress;
    private Supplier<Boolean> enabled = () -> true;

    public TextButton(int x, int y, Component component, int color, OnPress onPress) {
        this(x, y, component, color, onPress, null);
    }

    public TextButton(int x, int y, Component component, int color, OnPress onPress, Supplier<Boolean> enabled) {
        super(x, y, Minecraft.getInstance().font.width(component), Minecraft.getInstance().font.lineHeight, component, onPress, Button.DEFAULT_NARRATION);
        this.dynamicPress = onPress;
        this.height = Minecraft.getInstance().font.lineHeight;
        this.width = Minecraft.getInstance().font.width(getMessage());
        if (enabled != null) this.enabled = enabled;
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return mouseX >= getX() && mouseX <= getX() + getWidth() && mouseY >= getY() && mouseY <= getY() + getHeight();
    }

    @Override
    public void renderWidget(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        Minecraft minecraft = Minecraft.getInstance();
        Font font = minecraft.font;

        try (var pose = new CloseablePoseStack(graphics)) {
            int color = isMouseOver(mouseX, mouseY) && enabled.get() ? 0xFFFFFFFF : 0xFFAAAAAA;
            graphics.drawString(font, getMessage(), getX(), getY(), color);
        }
    }

    @Override
    public void onPress() {
        this.dynamicPress.onPress(this);
    }

    @Override
    public @NotNull Component getMessage() {
        if (enabled.get()) return super.getMessage();
        else return super.getMessage().copy().withStyle(ChatFormatting.STRIKETHROUGH);
    }

    @Override
    public void playDownSound(SoundManager soundManager) {
        if (enabled.get()) super.playDownSound(soundManager);
    }
}
