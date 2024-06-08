package earth.terrarium.tempad.client.screens.base;

import com.teamresourceful.resourcefullib.client.screens.AbstractContainerCursorScreen;
import earth.terrarium.tempad.common.menu.PrinterMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.jetbrains.annotations.NotNull;

public class BackgroundMenuScreen<T extends AbstractContainerMenu> extends AbstractContainerCursorScreen<T> implements MenuAccess<T> {
    protected final ResourceLocation sprite;

    public BackgroundMenuScreen(T menu, Inventory playerInventory, Component title, int screenWidth, int screenHeight, ResourceLocation sprite) {
        super(menu, playerInventory, title);
        this.imageWidth = screenWidth;
        this.imageHeight = screenHeight;
        this.sprite = sprite;
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        graphics.blitSprite(this.sprite, this.leftPos, this.topPos, this.imageWidth, this.imageHeight);
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float f) {
        super.render(graphics, mouseX, mouseY, f);
        this.renderTooltip(graphics, mouseX, mouseY);
    }
}
