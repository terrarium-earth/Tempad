package me.codexadrian.tempad.client;

import me.codexadrian.tempad.client.config.TempadClientConfig;
import me.codexadrian.tempad.client.gui.TempadScreen;
import me.codexadrian.tempad.common.registry.TempadRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public class TimedoorErrorToast implements Toast {

    private final Component message;

    public TimedoorErrorToast(Component message) {
        this.message = message;
    }

    @Override
    public Visibility render(GuiGraphics graphics, ToastComponent toastComponent, long l) {
        ItemStack itemStack = new ItemStack(TempadRegistry.TEMPAD.get());
        float red = (TempadClientConfig.color >> 16 & 0xFF) / 255f;
        float green = (TempadClientConfig.color >> 8 & 0xFF) / 255f;
        float blue = (TempadClientConfig.color & 0xFF) / 255f;
        graphics.setColor(red, green, blue, 1f);
        graphics.fill(0, 0, this.width(), this.height(), TempadClientConfig.color | 0xFF000000);
        graphics.setColor(red / 2, green / 2, blue / 2, 1f);
        graphics.blit(TempadScreen.GRID, 0, 0, 0, 0, this.width(), this.height(), 16, 16);
        Font font = Minecraft.getInstance().font;
        graphics.renderItem(itemStack, 6, 6);
        graphics.drawString(font, message, 25, 7, TempadClientConfig.color);
        return l >= 5000L ? Visibility.HIDE : Visibility.SHOW;
    }
}
