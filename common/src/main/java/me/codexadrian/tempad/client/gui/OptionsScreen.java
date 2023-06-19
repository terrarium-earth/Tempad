package me.codexadrian.tempad.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.teamresourceful.resourcefullib.client.CloseablePoseStack;
import me.codexadrian.tempad.common.Tempad;
import me.codexadrian.tempad.common.TempadClientConfig;
import me.codexadrian.tempad.common.TempadClient;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import org.jetbrains.annotations.NotNull;

public class OptionsScreen extends Screen {
    private static final ResourceLocation GRID = new ResourceLocation(Tempad.MODID, "textures/widget/tempad_grid.png");
    private final InteractionHand hand;
    private int color;

    private static final int WIDTH = 256;
    private static final int HEIGHT = 160;

    public OptionsScreen(int color, InteractionHand hand) {
        super(Component.nullToEmpty(""));
        this.color = color;
        this.hand = hand;
    }

    @Override
    protected void init() {
        super.init();
        int colorPerRow = 10;
        int margin = 6;
        int scale = 16;
        int startX = (width - WIDTH) / 2 + 16 + 5;
        int startY = (height - HEIGHT) / 2 + 16 * 3 + 2;
        int x = 0;
        int y = 0;
        int[] colors = TempadClient.getClientConfig().getColorOptions();
        for (int i = 0; i < colors.length; i++) {
            //Create button here
            //Set it's colors and stuff
            //Set it's position
            int finalI = i;
            ColorButton button = new ColorButton(x + startX, y + startY, scale, colors[i] | 0xFF000000, (button1) -> {
                this.color = colors[finalI];
                TempadClient.getClientConfig().setColor(color);
                TempadClientConfig.saveConfig(TempadClient.getClientConfig());
            });
            addRenderableWidget(button);
            x += scale + margin;
            if((i+1) % colorPerRow == 0) {
                x = 0;
                y += scale + margin;
            }
        }
    }

    private void renderOutline(GuiGraphics graphics) {
        int lineWidth = 4;
        graphics.fill((width - WIDTH - lineWidth) / 2, (height - HEIGHT - lineWidth) / 2, (width + WIDTH + lineWidth) / 2, (height + HEIGHT + lineWidth) / 2, color | 0xFF000000);
    }

    private void renderGridBackground(@NotNull GuiGraphics graphics, float red, float green, float blue) {
        RenderSystem.setShaderColor(red * 0.5f, green * 0.5f, blue * 0.5f, 1f);
        graphics.blit(GRID, (width - WIDTH) / 2, (height - HEIGHT) / 2, WIDTH, HEIGHT, 0, 0, WIDTH, HEIGHT, 16, 16);
    }

    @Override
    public void renderBackground(@NotNull GuiGraphics graphics) {
        super.renderBackground(graphics);
        float red = (color >> 16 & 0xFF) / 255f;
        float green = (color >> 8 & 0xFF) / 255f;
        float blue = (color & 0xFF) / 255f;
        renderOutline(graphics);
        renderGridBackground(graphics, red, green, blue);
        RenderSystem.setShaderColor(red, green, blue, 1f);
        renderHeaders(graphics);
    }

    private void renderHeaders(GuiGraphics graphics) {
        Font font = minecraft.font;
        int cornerX = (width - WIDTH) / 2 + 5;
        int cornerY = (height - HEIGHT) / 2;
        int x = cornerX + 16;
        int y = cornerY + 16 * 2;
        try(var pose = new CloseablePoseStack(graphics)) {
            graphics.drawString(font, Component.translatable("gui." + Tempad.MODID + ".options_header"), x, y, 0xFFFFFFFF);
        }
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void onClose() {
        minecraft.setScreen(new TempadScreen(color, hand));
    }

    public static class ColorButton extends Button {
        private final int buttonColor;
        public ColorButton(int x, int y, int size, int buttonColor, OnPress onPress) {
            super(x, y, size, size, Component.nullToEmpty(""), onPress, Button.DEFAULT_NARRATION);
            this.buttonColor = buttonColor;
        }

        @Override
        protected void renderWidget(GuiGraphics graphics, int i, int j, float f) {
            try(var pose = new CloseablePoseStack(graphics)) {
                RenderSystem.setShaderColor(1, 1, 1, 1);
                graphics.fill(getX() - 1, getY() - 1, getX() + 1 + width, getY() + 1 + height, 0xFFFFFFFF);
                graphics.fill(getX(), getY(), getX() + width, getY() + height, buttonColor);
            }
        }
    }
}
