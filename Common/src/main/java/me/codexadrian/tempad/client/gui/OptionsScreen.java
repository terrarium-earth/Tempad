package me.codexadrian.tempad.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import me.codexadrian.tempad.ColorConfig;
import me.codexadrian.tempad.Constants;
import me.codexadrian.tempad.TempadClient;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;

public class OptionsScreen extends Screen {
    private static final ResourceLocation GRID = new ResourceLocation(Constants.MODID, "textures/widget/tempad_grid.png");
    private final InteractionHand hand;
    private int color;

    private static final int WIDTH = 480;
    private static final int HEIGHT = 256;

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
        int scale = 32;
        int startX = (width - WIDTH) / 2 + 16 * 3 + 5;
        int startY = (height - HEIGHT) / 2 + 16 * 5 + 2;
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
                ColorConfig.saveConfig(TempadClient.getClientConfig());
            });
            addRenderableWidget(button);
            x += scale + margin;
            if((i+1) % colorPerRow == 0) {
                x = 0;
                y += scale + margin;
            }
        }
    }

    private void renderOutline(PoseStack poseStack) {
        int lineWidth = 4;
        fill(poseStack, (width - WIDTH - lineWidth) / 2, (height - HEIGHT - lineWidth) / 2, (width + WIDTH + lineWidth) / 2, (height + HEIGHT + lineWidth) / 2, color | 0xFF000000);
    }

    private void renderGridBackground(PoseStack poseStack, float red, float green, float blue) {
        RenderSystem.setShaderTexture(0, GRID);
        RenderSystem.setShaderColor(red * 0.5f, green * 0.5f, blue * 0.5f, 1f);
        blit(poseStack, (width - WIDTH) / 2, (height - HEIGHT) / 2, WIDTH, HEIGHT, 0, 0, WIDTH, HEIGHT, 16, 16);
    }

    @Override
    public void renderBackground(PoseStack poseStack, int offset) {
        super.renderBackground(poseStack, offset);
        float red = (color >> 16 & 0xFF) / 255f;
        float green = (color >> 8 & 0xFF) / 255f;
        float blue = (color & 0xFF) / 255f;
        renderOutline(poseStack);
        renderGridBackground(poseStack, red, green, blue);
        renderHeaders(poseStack);
    }

    private void renderHeaders(PoseStack matrices) {
        Font font = minecraft.font;
        int cornerX = (width - WIDTH) / 2 + 3;
        int cornerY = (height - HEIGHT) / 2 + 7;
        int x = cornerX + 16 * 3;
        int y = cornerY + 16 * 2;
        matrices.pushPose();
        matrices.translate(x * (-1.2), y * (-1.2), 0);
        matrices.scale(2.2F, 2.2F, 0);
        drawString(matrices, font, Component.translatable("gui." + Constants.MODID + ".options_header"), x, y, color);
        matrices.popPose();
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        renderBackground(poseStack);
        super.render(poseStack, mouseX, mouseY, partialTicks);
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
            super(x, y, size, size, Component.nullToEmpty(""), onPress);
            this.buttonColor = buttonColor;
        }

        @Override
        public void renderButton(PoseStack matrices, int mouseX, int mouseY, float partialTick) {
            matrices.pushPose();
            RenderSystem.setShaderColor(1, 1, 1, 1);
            fill(matrices, x - 1, y - 1, x + 1 + width, y + 1 + height, 0xFFFFFFFF);
            fill(matrices, x, y, x + width, y + height, buttonColor);
            matrices.popPose();
        }
    }
}
