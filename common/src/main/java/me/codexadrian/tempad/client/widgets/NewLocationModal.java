package me.codexadrian.tempad.client.widgets;

import com.mojang.blaze3d.systems.RenderSystem;
import me.codexadrian.tempad.client.config.TempadClientConfig;
import me.codexadrian.tempad.common.Tempad;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Consumer;

public class NewLocationModal extends BaseModal {
    private static final ResourceLocation SCREEN = new ResourceLocation(Tempad.MODID, "textures/widget/location_modal.png");
    private static final int WIDTH = 94;
    private static final int HEIGHT = 32;
    public NewLocationModal(int screenWidth, int screenHeight, int x, int y, Consumer<String> callback) {
        super(screenWidth, screenHeight, WIDTH, HEIGHT, 1, x, y);

        var editBox = new EditBox(font, x + 18, y + 5, 72, 12, Component.nullToEmpty(""));
        editBox.setMaxLength(32);
        editBox.setBordered(false);
        editBox.setHint(Component.translatable("gui." + Tempad.MODID + ".textfield"));
        editBox.setTextColor(TempadClientConfig.color);
        addChild(editBox);

        addChild(new TempadButton(x + 65, y + 17, 12, 12, Component.nullToEmpty(""), button -> {
            this.setVisible(false);
        }, 0));

        addChild(new TempadButton(x + 79, y + 17, 12, 12, Component.nullToEmpty(""), button -> {
            this.setVisible(false);
            callback.accept(editBox.getValue());
        }, 1));
    }

    @Override
    protected void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.enableBlend();
        RenderSystem.enableDepthTest();
        graphics.blit(SCREEN, x, y, WIDTH, HEIGHT, 0, 0, WIDTH, HEIGHT, 128, 128);
    }

    @Override
    protected void renderForeground(GuiGraphics graphics, int mouseX, int mouseY, float partialTick){
        children().forEach(child -> {
            if (child instanceof Renderable renderable) {
                renderable.render(graphics, mouseX, mouseY, partialTick);
            }
        });
    }

    public static class TempadButton extends Button {
        private final int buttonOffset;
        protected TempadButton(int x, int y, int width, int height, Component message, OnPress onPress, int buttonOffset) {
            super(x, y, width, height, message, onPress, Button.DEFAULT_NARRATION);
            this.buttonOffset = buttonOffset;
        }

        @Override
        protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
            guiGraphics.setColor(1.0F, 1.0F, 1.0F, this.alpha);
            RenderSystem.enableBlend();
            RenderSystem.enableDepthTest();
            guiGraphics.blit(SCREEN, this.getX(), this.getY(), this.getWidth(), this.getHeight(), buttonOffset * 12, this.getTextureY(), this.getWidth(), this.getHeight(), 128, 128);
        }

        protected int getTextureY() {
            int i = 0;
            if (!this.isActive()) {
                i = 2;
            } else if (this.isHovered()) {
                i = 1;
            }

            return 32 + i * this.getHeight();
        }
    }
}
