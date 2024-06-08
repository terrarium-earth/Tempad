package earth.terrarium.tempad.client.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import earth.terrarium.tempad.client.components.ModSprites;
import earth.terrarium.tempad.client.config.TempadClientConfig;
import earth.terrarium.tempad.client.screens.base.BackgroundScreen;
import earth.terrarium.tempad.common.Tempad;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;

public class ModalScreen extends BackgroundScreen {

    private static final Component NAME_FIELD = Component.translatable("gui." + Tempad.MODID + ".name_field");

    private final Screen background;
    private final Consumer<String> callback;

    protected ModalScreen(Screen background, Consumer<String> callback) {
        super(94, 32, ModSprites.MODAL);
        this.background = background;
        this.callback = callback;
    }

    @Override
    protected void init() {
        super.init();

        EditBox box = new EditBox(this.font, this.left + 16, this.top + 5, 74, 8, CommonComponents.EMPTY);
        box.setMaxLength(32);
        box.setBordered(false);
        box.setHint(NAME_FIELD);
        box.setTextColor(TempadClientConfig.color);

        addRenderableWidget(box);

        LinearLayout layout = LinearLayout.horizontal().spacing(3);

        layout.addChild(new ImageButton(
            0, 0, 12, 12,
            ModSprites.BACK, b -> this.onClose()
        )).setTooltip(Tooltip.create(CommonComponents.GUI_CANCEL));

        layout.addChild(new ImageButton(
            0, 0, 12, 12,
            ModSprites.SAVE, b -> {
            this.callback.accept(box.getValue());
            if (this.minecraft != null) {
                this.minecraft.setScreen(null);
            }
        }
        )).setTooltip(Tooltip.create(CommonComponents.GUI_DONE));

        layout.arrangeElements();
        layout.setPosition(this.left + this.screenWidth - layout.getWidth() - 3, this.top + this.screenHeight - layout.getHeight() - 3);
        layout.visitWidgets(this::addRenderableWidget);
    }

    @Override
    public void added() {
        super.added();
        this.background.clearFocus();
    }

    @Override
    protected void repositionElements() {
        this.background.resize(Minecraft.getInstance(), this.width, this.height);
    }

    @Override
    public void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.background.render(graphics, -1, -1, partialTick);
        graphics.flush();
        RenderSystem.clear(256, Minecraft.ON_OSX);
        super.renderBackground(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    public void onClose() {
        if (this.minecraft == null) return;
        this.minecraft.setScreen(this.background);
    }

    public static void open(Consumer<String> callback) {
        Minecraft.getInstance().setScreen(new ModalScreen(
            Minecraft.getInstance().screen,
            callback
        ));
    }
}
