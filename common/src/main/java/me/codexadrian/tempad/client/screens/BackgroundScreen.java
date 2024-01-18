package me.codexadrian.tempad.client.screens;

import com.teamresourceful.resourcefullib.client.screens.BaseCursorScreen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.resources.ResourceLocation;

public class BackgroundScreen extends BaseCursorScreen {

    protected final int screenWidth;
    protected final int screenHeight;
    protected final ResourceLocation sprite;

    protected int left;
    protected int top;

    protected BackgroundScreen(int screenWidth, int screenHeight, ResourceLocation sprite) {
        super(CommonComponents.EMPTY);
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.sprite = sprite;
    }

    @Override
    protected void init() {
        this.left = (this.width - this.screenWidth) / 2;
        this.top = (this.height - this.screenHeight) / 2;
    }

    @Override
    public void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.renderBackground(graphics, mouseX, mouseY, partialTick);
        graphics.blitSprite(this.sprite, this.left, this.top, this.screenWidth, this.screenHeight);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
