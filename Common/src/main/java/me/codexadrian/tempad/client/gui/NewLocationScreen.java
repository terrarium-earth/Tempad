package me.codexadrian.tempad.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import me.codexadrian.tempad.Constants;
import me.codexadrian.tempad.client.widgets.TextButton;
import me.codexadrian.tempad.client.widgets.TimedoorSprite;
import me.codexadrian.tempad.network.messages.AddLocationPacket;
import me.codexadrian.tempad.platform.Services;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;

public class NewLocationScreen extends Screen {
    private static final ResourceLocation GRID = new ResourceLocation(Constants.MODID, "textures/widget/tempad_grid.png");
    private final int color;

    private static final int WIDTH = 480;
    private static final int HEIGHT = 256;
    private final InteractionHand hand;

    public NewLocationScreen(int color, InteractionHand hand) {
        super(Component.nullToEmpty(""));
        this.color = color;
        this.hand = hand;
    }

    @Override
    protected void init() {
        super.init();
        int cornerX = (width - WIDTH) / 2;
        int cornerY = (height - HEIGHT) / 2;
        TimedoorSprite timeDoorSprite = new TimedoorSprite(cornerX + 16 * 4, cornerY + 16 * 4, color, 128);
        Component addLocationText = new TranslatableComponent("gui." + Constants.MODID + ".add_location");
        EditBox textField = new EditBox(font, cornerX + 16 * 15, cornerY + 16 * 8 - 4, 16 * 8, 24, new TranslatableComponent("gui." + Constants.MODID + ".textfield"));
        TextButton addLocation = new TextButton(cornerX + 16 * 15, cornerY + 16 * 10, 12, addLocationText, color, (button -> {
            String nameFieldText = textField.getValue();
            if (!nameFieldText.equals("")) {
                Services.NETWORK.sendToServer(new AddLocationPacket(nameFieldText, hand));
            }
            Minecraft.getInstance().setScreen(null);

        }));
        textField.setTextColor(color);
        addRenderableWidget(timeDoorSprite);
        addRenderableWidget(textField);
        addRenderableWidget(addLocation);
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

    private void renderHeaders(PoseStack matrices) {
        Font font = minecraft.font;
        int cornerX = (width - WIDTH) / 2 + 3;
        int cornerY = (height - HEIGHT) / 2 + 3;
        int x = cornerX + 16 * 15;
        int y = cornerY + 16 * 6;
        matrices.pushPose();
        matrices.translate(x * (-0.5), y * (-0.5), 0);
        matrices.scale(1.5F, 1.5F, 0);
        drawString(matrices, font, new TextComponent(minecraft.player.blockPosition().toShortString()), x, y, color);
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
