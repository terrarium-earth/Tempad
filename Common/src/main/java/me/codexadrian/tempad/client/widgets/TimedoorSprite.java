package me.codexadrian.tempad.client.widgets;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import static me.codexadrian.tempad.Constants.MODID;
import static net.minecraft.client.gui.GuiComponent.blit;

public class TimedoorSprite implements Widget, GuiEventListener, NarratableEntry {
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
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        float red = (color >> 16 & 0xFF) / 255f;
        float green = (color >> 8 & 0xFF) / 255f;
        float blue = (color & 0xFF) / 255f;
        poseStack.pushPose();
        RenderSystem.enableBlend();
        RenderSystem.setShaderTexture(0, new ResourceLocation(MODID, "textures/widget/timedoor/timedoor_" + age / 4 + ".png"));
        RenderSystem.setShaderColor(red, green, blue, 1f);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        blit(poseStack, x, y, size, size, 0, 0, 16, 16, 16, 16);
        poseStack.popPose();
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
}
