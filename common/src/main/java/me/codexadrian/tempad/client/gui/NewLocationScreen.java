package me.codexadrian.tempad.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.teamresourceful.resourcefullib.client.CloseablePoseStack;
import me.codexadrian.tempad.client.config.TempadClientConfig;
import me.codexadrian.tempad.client.widgets.TextButton;
import me.codexadrian.tempad.client.widgets.TimedoorSprite;
import me.codexadrian.tempad.common.Tempad;
import me.codexadrian.tempad.common.network.NetworkHandler;
import me.codexadrian.tempad.common.network.messages.AddLocationPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import org.jetbrains.annotations.NotNull;

public class NewLocationScreen extends Screen {
    private static final ResourceLocation GRID = new ResourceLocation(Tempad.MODID, "textures/widget/tempad_grid.png");

    private static final int WIDTH = 256;
    private static final int HEIGHT = 160;
    private final InteractionHand hand;

    public NewLocationScreen(InteractionHand hand) {
        super(Component.nullToEmpty(""));
        this.hand = hand;
    }

    @Override
    protected void init() {
        super.init();
        int cornerX = (width - WIDTH) / 2 + 3;
        int cornerY = (height - HEIGHT) / 2 + 27;
        TimedoorSprite timeDoorSprite = new TimedoorSprite(cornerX + 16, cornerY, TempadClientConfig.color, 100);
        Component addLocationText = Component.translatable("gui." + Tempad.MODID + ".add_location");
        EditBox textField = new EditBox(font, cornerX + 16 * 8, cornerY + 16 * 2 + 12, 104, 16, Component.translatable("gui." + Tempad.MODID + ".textfield"));
        TextButton addLocation = new TextButton(cornerX + 16 * 8, cornerY + 16 * 4, addLocationText, TempadClientConfig.color, (button -> {
            String nameFieldText = textField.getValue();
            if (!nameFieldText.isBlank()) {
                NetworkHandler.CHANNEL.sendToServer(new AddLocationPacket(nameFieldText, hand));
                Minecraft.getInstance().setScreen(null);
            }

        }));
        addRenderableWidget(timeDoorSprite);
        addRenderableWidget(textField);
        addRenderableWidget(addLocation);
    }

    private void renderOutline(@NotNull GuiGraphics graphics) {
        int lineWidth = 4;
        graphics.fill((width - WIDTH - lineWidth) / 2, (height - HEIGHT - lineWidth) / 2, (width + WIDTH + lineWidth) / 2, (height + HEIGHT + lineWidth) / 2, TempadClientConfig.color | 0xFF000000);
    }

    private void renderGridBackground(@NotNull GuiGraphics graphics, float red, float green, float blue) {
        RenderSystem.setShaderTexture(0, GRID);
        RenderSystem.setShaderColor(red * 0.5f, green * 0.5f, blue * 0.5f, 1f);
        graphics.blit(GRID, (width - WIDTH) / 2, (height - HEIGHT) / 2, WIDTH, HEIGHT, 0, 0, WIDTH, HEIGHT, 16, 16);
    }

    private void renderHeaders(@NotNull GuiGraphics graphics) {
        Font font = minecraft.font;
        int cornerX = (width - WIDTH) / 2 + 3;
        int cornerY = (height - HEIGHT) / 2 + 3;
        int x = cornerX + 16 * 8;
        int y = cornerY + 16 * 2 + 24;
        try (var pose = new CloseablePoseStack(graphics)) {
            graphics.drawString(font, Component.literal(minecraft.player.blockPosition().toShortString()), x, y, 0xFFFFFF);
        }
    }

    @Override
    public void renderBackground(@NotNull GuiGraphics graphics) {
        super.renderBackground(graphics);

        float red = (TempadClientConfig.color >> 16 & 0xFF) / 255f;
        float green = (TempadClientConfig.color >> 8 & 0xFF) / 255f;
        float blue = (TempadClientConfig.color & 0xFF) / 255f;
        renderOutline(graphics);
        renderGridBackground(graphics, red, green, blue);
        RenderSystem.setShaderColor(red, green, blue, 1f);
        renderHeaders(graphics);
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

}
