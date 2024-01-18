package me.codexadrian.tempad.client.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import com.teamresourceful.resourcefullib.client.screens.BaseCursorScreen;
import me.codexadrian.tempad.client.config.TempadClientConfig;
import me.codexadrian.tempad.common.Tempad;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Consumer;

public class ModalScreen extends BaseCursorScreen {

    private static final ResourceLocation BACKGROUND = new ResourceLocation(Tempad.MODID, "modal/background");
    private static final WidgetSprites BACK = new WidgetSprites(
        new ResourceLocation(Tempad.MODID, "modal/back/normal"),
        new ResourceLocation(Tempad.MODID, "modal/back/disabled"),
        new ResourceLocation(Tempad.MODID, "modal/back/hover")
    );
    private static final WidgetSprites SAVE = new WidgetSprites(
        new ResourceLocation(Tempad.MODID, "modal/save/normal"),
        new ResourceLocation(Tempad.MODID, "modal/save/disabled"),
        new ResourceLocation(Tempad.MODID, "modal/save/hover")
    );
    private static final Component NAME_FIELD = Component.translatable("gui." + Tempad.MODID + ".name_field");

    private static final int WIDTH = 94;
    private static final int HEIGHT = 32;

    private final Screen background;
    private final Consumer<String> callback;

    private int left;
    private int top;

    protected ModalScreen(Screen background, Consumer<String> callback) {
        super(CommonComponents.EMPTY);
        this.background = background;
        this.callback = callback;
    }

    @Override
    protected void init() {
        this.left = (this.width - WIDTH) / 2;
        this.top = (this.height - HEIGHT) / 2;

        EditBox box = new EditBox(this.font, this.left + 16, this.top + 5, 74, 8, CommonComponents.EMPTY);
        box.setMaxLength(32);
        box.setBordered(false);
        box.setHint(NAME_FIELD);
        box.setTextColor(TempadClientConfig.color);

        addRenderableWidget(box);

        LinearLayout layout = LinearLayout.horizontal().spacing(3);

        layout.addChild(new ImageButton(
            0, 0, 12, 12,
            BACK, b -> this.onClose()
        )).setTooltip(Tooltip.create(CommonComponents.GUI_CANCEL));

        layout.addChild(new ImageButton(
            0, 0, 12, 12,
            SAVE, b -> {
            this.callback.accept(box.getValue());
            this.minecraft.setScreen(null);
        }
        )).setTooltip(Tooltip.create(CommonComponents.GUI_DONE));

        layout.arrangeElements();
        layout.setPosition(this.left + WIDTH - layout.getWidth() - 3, this.top + HEIGHT - layout.getHeight() - 3);
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
        this.renderTransparentBackground(graphics);

        graphics.blitSprite(BACKGROUND, this.left, this.top, WIDTH, HEIGHT);
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(this.background);
    }

    public static void open(Consumer<String> callback) {
        Minecraft.getInstance().setScreen(new ModalScreen(
            Minecraft.getInstance().screen,
            callback
        ));
    }
}
