package me.codexadrian.tempad.client.widgets;

import com.mojang.blaze3d.systems.RenderSystem;
import com.teamresourceful.resourcefullib.client.CloseablePoseStack;
import me.codexadrian.tempad.common.Tempad;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import static me.codexadrian.tempad.common.Tempad.MODID;

public class TimedoorSprite implements Renderable, GuiEventListener, NarratableEntry {
    private final int color;
    private final int size;
    private int x;
    private int y;
    private int age;

    public TimedoorSprite(int x, int y, int color, int size) {
        this.x = x;
        this.y = y;
        this.color = color;
        this.size = size;
    }

    public void changePosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        float red = (color >> 16 & 0xFF) / 255f;
        float green = (color >> 8 & 0xFF) / 255f;
        float blue = (color & 0xFF) / 255f;
        try(var pose = new CloseablePoseStack(graphics)) {
            RenderSystem.enableBlend();
            RenderSystem.setShaderColor(red, green, blue, 1f);
            graphics.blit(new ResourceLocation(Tempad.MODID, "textures/widget/timedoor/timedoor_" + age / 4 + ".png"), x, y, size, size, 0, 0, 16, 16, 16, 16);
        }
        if (age <= 44) age++;
    }

    @Override
    public NarrationPriority narrationPriority() {
        return NarrationPriority.NONE;
    }

    @Override
    public void updateNarration(NarrationElementOutput narrationElementOutput) {
        narrationElementOutput.add(NarratedElementType.TITLE, Component.translatable("gui." + MODID + ".timedoor"));
    }

    @Override
    public void setFocused(boolean bl) {
    }

    @Override
    public boolean isFocused() {
        return false;
    }
}
