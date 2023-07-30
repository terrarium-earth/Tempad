package me.codexadrian.tempad.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.teamresourceful.resourcefullib.client.CloseablePoseStack;
import me.codexadrian.tempad.client.config.TempadClientConfig;
import me.codexadrian.tempad.client.widgets.TextButton;
import me.codexadrian.tempad.common.Tempad;
import me.codexadrian.tempad.common.data.LocationData;
import net.minecraft.Util;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class TempadScreen extends Screen {
    public static final ResourceLocation GRID = new ResourceLocation(Tempad.MODID, "textures/widget/tempad_grid.png");
    private static final ResourceLocation TVA_LOGO = new ResourceLocation(Tempad.MODID, "textures/widget/tva_logo.png");

    private static final int WIDTH = 256;
    private static final int HEIGHT = 160;
    private final InteractionHand hand;
    private final List<LocationData> locations;

    public TempadScreen(InteractionHand hand, List<LocationData> locations) {
        super(CommonComponents.EMPTY);
        this.hand = hand;
        this.locations = locations;
    }

    @Override
    protected void init() {
        super.init();
        int offset = 4;
        addRenderableWidget(new TextButton((width - WIDTH) / 2 + 16 * 10 + offset, (height - HEIGHT) / 2 + 72 + offset, Component.translatable("gui." + Tempad.MODID + ".options"), 0xFFFFFFFF, button -> minecraft.setScreen(new OptionsScreen(this))));

        addRenderableWidget(new TextButton((width - WIDTH) / 2 + 16 * 10 + offset, (height - HEIGHT) / 2 + 82 + offset, Component.translatable("gui." + Tempad.MODID + ".run_program"), 0xFFFFFFFF, button -> {
            minecraft.setScreen(new RunProgramScreen(this.hand, locations));
        }));

        addRenderableWidget(new TextButton((width - WIDTH) / 2 + 16 * 10 + offset, (height - HEIGHT) / 2 + 92 + offset, Component.translatable("gui." + Tempad.MODID + ".wiki"), 0xFFFFFFFF, button -> {
            this.minecraft.setScreen(new ConfirmLinkScreen((bl) -> {
                if (bl) {
                    Util.getPlatform().openUri("https://codexadrian.tech/tempad-wiki");
                }

                this.minecraft.setScreen(this);
            }, "https://codexadrian.tech/tempad-wiki", true));
        }));
    }

    private void renderOutline(GuiGraphics graphics) {
        int lineWidth = 4;
        graphics.fill((width - WIDTH - lineWidth) / 2, (height - HEIGHT - lineWidth) / 2, (width + WIDTH + lineWidth) / 2, (height + HEIGHT + lineWidth) / 2, TempadClientConfig.color | 0xFF000000);
    }

    private void renderGridBackground(GuiGraphics graphics, float red, float green, float blue) {
        RenderSystem.setShaderColor(red * 0.5f, green * 0.5f, blue * 0.5f, 1f);
        graphics.blit(GRID, (width - WIDTH) / 2, (height - HEIGHT) / 2, WIDTH, HEIGHT, 0, 0, WIDTH, HEIGHT, 16, 16);
    }

    private void renderTvaLogo(GuiGraphics graphics, float red, float green, float blue) {
        int tvaWidth = WIDTH / 2 + 16;
        int tvaHeight = HEIGHT / 2;
        RenderSystem.setShaderColor(red, green, blue, 1f);
        graphics.blit(TVA_LOGO, width / 2 - tvaWidth + 24, (height - tvaHeight) / 2, tvaWidth, tvaHeight, 0, 0, 32, 16, 32, 16);
    }

    private void renderHeaders(GuiGraphics graphics) {
        Font font = minecraft.font;
        int cornerX = (width - WIDTH) / 2 + 3;
        int cornerY = (height - HEIGHT) / 2 + 7;
        int x = cornerX + 16 * 10;
        int y = cornerY + 16 * 3;
        try (var pose = new CloseablePoseStack(graphics)) {
            graphics.drawString(font, Component.translatable("gui." + Tempad.MODID + ".header_line_1"), x, y, 0xFFFFFFFF);
            graphics.drawString(font, Component.translatable("gui." + Tempad.MODID + ".header_line_2"), x, y + 10, 0xFFFFFFFF);
        }
    }

    @Override
    public void renderBackground(@NotNull GuiGraphics guiGraphics) {
        super.renderBackground(guiGraphics);

        float red = (TempadClientConfig.color >> 16 & 0xFF) / 255f;
        float green = (TempadClientConfig.color >> 8 & 0xFF) / 255f;
        float blue = (TempadClientConfig.color & 0xFF) / 255f;
        renderOutline(guiGraphics);
        renderGridBackground(guiGraphics, red, green, blue);
        renderTvaLogo(guiGraphics, red, green, blue);
        renderHeaders(guiGraphics);
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int i, int j, float f) {
        renderBackground(guiGraphics);
        super.render(guiGraphics, i, j, f);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

}
