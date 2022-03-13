package me.codexadrian.tempad.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import me.codexadrian.tempad.Constants;
import me.codexadrian.tempad.client.widgets.TextButton;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;

public class TempadScreen extends Screen {
    private static final ResourceLocation GRID = new ResourceLocation(Constants.MODID, "textures/widget/tempad_grid.png");
    private static final ResourceLocation TVA_LOGO = new ResourceLocation(Constants.MODID, "textures/widget/tva_logo.png");
    private final int color;

    private static final int WIDTH = 480;
    private static final int HEIGHT = 256;
    private final InteractionHand hand;

    public TempadScreen(int color, InteractionHand hand) {
        super(Component.nullToEmpty(""));
        this.color = color;
        this.hand = hand;
    }

    @Override
    protected void init() {
        super.init();
        int offset = 3;
        addRenderableWidget(new TextButton((width - WIDTH) / 2 + 16 * 17 + offset, (height - HEIGHT) / 2 + 16 * 8 + offset, 12, new TranslatableComponent("gui." + Constants.MODID + ".options"), color, button -> minecraft.setScreen(new OptionsScreen(color, hand))));

        addRenderableWidget(new TextButton((width - WIDTH) / 2 + 16 * 17 + offset, (height - HEIGHT) / 2 + 16 * 9 + offset, 12, new TranslatableComponent("gui." + Constants.MODID + ".run_program"), color, button -> minecraft.setScreen(new RunProgramScreen(color, this.hand))));

        addRenderableWidget(new TextButton((width - WIDTH) / 2 + 16 * 17 + offset, (height - HEIGHT) / 2 + 16 * 10 + offset, 12, new TranslatableComponent("gui." + Constants.MODID + ".wiki"), color, button -> {
        }));
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

    private void renderTvaLogo(PoseStack poseStack, float red, float green, float blue) {
        int tvaWidth = WIDTH / 2 + 16;
        int tvaHeight = HEIGHT / 2;
        RenderSystem.setShaderTexture(0, TVA_LOGO);
        RenderSystem.setShaderColor(red, green, blue, 1f);
        blit(poseStack, width / 2 - tvaWidth + 24, (height - tvaHeight) / 2, tvaWidth, tvaHeight, 0, 0, 32, 16, 32, 16);
    }

    private void renderHeaders(PoseStack matrices) {
        Font font = minecraft.font;
        int cornerX = (width - WIDTH) / 2 + 3;
        int cornerY = (height - HEIGHT) / 2 + 7;
        int x = cornerX + 16 * 17;
        int y = cornerY + 16 * 5;
        matrices.pushPose();
        matrices.translate(x * (-1.2), y * (-1.2), 0);
        matrices.scale(2.2F, 2.2F, 0);
        drawString(matrices, font, new TranslatableComponent("gui." + Constants.MODID + ".header_line_1"), x, y, color);
        drawString(matrices, font, new TranslatableComponent("gui." + Constants.MODID + ".header_line_2"), x, y + 10, color);
        matrices.popPose();
    }

    @Override
    public void renderBackground(PoseStack poseStack, int offset) {
        super.renderBackground(poseStack, offset);

        float red = (color >> 16 & 0xFF) / 255f;
        float green = (color >> 8 & 0xFF) / 255f;
        float blue = (color & 0xFF) / 255f;
        renderOutline(poseStack);
        renderGridBackground(poseStack, red, green, blue);
        renderTvaLogo(poseStack, red, green, blue);
        renderHeaders(poseStack);
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

}
