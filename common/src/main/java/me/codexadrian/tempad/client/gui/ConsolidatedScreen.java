package me.codexadrian.tempad.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.teamresourceful.resourcefullib.client.components.selection.SelectionList;
import me.codexadrian.tempad.client.config.TempadClientConfig;
import me.codexadrian.tempad.client.widgets.NewLocationModal;
import me.codexadrian.tempad.client.widgets.TemporaryWidget;
import me.codexadrian.tempad.client.widgets.TextEntry;
import me.codexadrian.tempad.common.Tempad;
import me.codexadrian.tempad.common.config.TempadConfig;
import me.codexadrian.tempad.common.data.LocationData;
import me.codexadrian.tempad.common.network.NetworkHandler;
import me.codexadrian.tempad.common.network.messages.AddLocationPacket;
import me.codexadrian.tempad.common.network.messages.DeleteLocationPacket;
import me.codexadrian.tempad.common.network.messages.ExportLocationPacket;
import me.codexadrian.tempad.common.network.messages.SummonTimedoorPacket;
import me.codexadrian.tempad.common.items.TempadItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ConsolidatedScreen extends Screen {
    private static final ResourceLocation SCREEN = new ResourceLocation(Tempad.MODID, "textures/widget/tempad_screen.png");
    private static final int TEMPAD_WIDTH = 249;
    private static final int TEMPAD_HEIGHT = 138;

    protected final List<TemporaryWidget> temporaryWidgets = new ArrayList<>();
    private final InteractionHand hand;
    private final List<LocationData> locations;
    private LocationData selectedLocation;
    private SelectionList<TextEntry> informationPanel;
    private SelectionList<TextEntry> locationPanel;
    private Button downloadButton;
    private Button deleteButton;
    private Button teleportButton;

    public ConsolidatedScreen(InteractionHand hand, List<LocationData> locations) {
        super(Component.nullToEmpty(""));
        this.hand = hand;
        this.locations = locations;
    }

    @Override
    protected void init() {
        super.init();
        int offset = 3;
        int cornerX = (width - TEMPAD_WIDTH) / 2;
        int cornerY = (height - TEMPAD_HEIGHT) / 2;
        informationPanel = addRenderableWidget(new SelectionList<>(cornerX + 16, cornerY + 31, 91, 76, 10, textEntry -> {}));
        locationPanel = addRenderableWidget(new SelectionList<>(cornerX + 129, cornerY + 31, 91, 92, 10, textEntry -> {
            if (textEntry != null) {
                selectedLocation = textEntry.data;

                informationPanel.updateEntries(List.of(
                    new TextEntry(Component.literal(selectedLocation.getName())),
                    new TextEntry(Component.translatable("gui." + Tempad.MODID + ".x", Mth.floor(selectedLocation.getBlockPos().getX()))),
                    new TextEntry(Component.translatable("gui." + Tempad.MODID + ".y", Mth.floor(selectedLocation.getBlockPos().getY()))),
                    new TextEntry(Component.translatable("gui." + Tempad.MODID + ".z", Mth.floor(selectedLocation.getBlockPos().getZ()))),
                    new TextEntry(Component.translatable("gui." + Tempad.MODID + ".dimension", Component.translatable(selectedLocation.getLevelKey().location().toLanguageKey("dimension")))))
                );

                downloadButton.active = selectedLocation.isDownloadable();
                if (selectedLocation.isDownloadable()) {
                    downloadButton.setTooltip(Tooltip.create(Component.translatable("gui." + Tempad.MODID + ".download")));
                } else {
                    downloadButton.setTooltip(Tooltip.create(Component.translatable("gui." + Tempad.MODID + ".download_disabled")));
                }

                deleteButton.active = selectedLocation.isDeletable();
                if (selectedLocation.isDeletable()) {
                    deleteButton.setTooltip(Tooltip.create(Component.translatable("gui." + Tempad.MODID + ".delete")));
                } else {
                    deleteButton.setTooltip(Tooltip.create(Component.translatable("gui." + Tempad.MODID + ".delete_disabled")));
                }

                teleportButton.active = selectedLocation.isTeleportable();
                if (selectedLocation.isTeleportable()) {
                    teleportButton.setTooltip(Tooltip.create(Component.translatable("gui." + Tempad.MODID + ".teleport")));
                } else {
                    teleportButton.setTooltip(Tooltip.create(Component.translatable("gui." + Tempad.MODID + ".teleport_disabled")));
                }
            }
        }));

        locationPanel.updateEntries(locations.stream().map(TextEntry::new).toList());
        EditBox editBox = new EditBox(font, cornerX + 139, cornerY + 16, 66, 12, Component.translatable("gui." + Tempad.MODID + ".textfield"));
        editBox.setBordered(false);
        editBox.setHint(Component.translatable("gui." + Tempad.MODID + ".textfield"));
        editBox.setTextColor(TempadClientConfig.color);
        editBox.setResponder((string) -> locationPanel.updateEntries(locations.stream().filter(locationData -> locationData.getName().toLowerCase().contains(string.toLowerCase())).map(TextEntry::new).toList()));
        addRenderableWidget(editBox);

        downloadButton = addRenderableWidget(new TempadButton(cornerX + 62, cornerY + 110, 14, 14, Component.translatable("gui." + Tempad.MODID + ".download"), (button) -> exportAction(), 0));
        downloadButton.active = false;
        deleteButton = addRenderableWidget(new TempadButton(cornerX + 78, cornerY + 110, 14, 14, Component.translatable("gui." + Tempad.MODID + ".delete"), (button) -> deleteAction(), 1));
        deleteButton.active = false;
        teleportButton = addRenderableWidget(new TempadButton(cornerX + 94, cornerY + 110, 14, 14, Component.translatable("gui." + Tempad.MODID + ".teleport"), (button) -> teleportAction(), 2));
        teleportButton.active = false;
        addRenderableWidget(new TempadButton(cornerX + 208, cornerY + 14, 12, 12, Component.translatable("gui." + Tempad.MODID + ".add_location"), (button) -> openNewLocationModal(), 3)).setTooltip(Tooltip.create(Component.translatable("gui." + Tempad.MODID + ".add_location")));
    }

    private void teleportAction() {
        if (minecraft == null || minecraft.player == null) return;
        ItemStack itemInHand = minecraft.player.getItemInHand(hand);
        if (itemInHand.getItem() instanceof TempadItem tempadItem) {
            if (tempadItem.getOption().canTimedoorOpen(minecraft.player, itemInHand)) {
                NetworkHandler.CHANNEL.sendToServer(new SummonTimedoorPacket(selectedLocation.getId(), hand, TempadClientConfig.color));
                Minecraft.getInstance().setScreen(null);
            }
        }
    }

    private void deleteAction() {
        if (minecraft != null && minecraft.player != null) {
            Minecraft.getInstance().setScreen(null);
            NetworkHandler.CHANNEL.sendToServer(new DeleteLocationPacket(selectedLocation.getId()));
        }
    }

    private void exportAction() {
        if (minecraft != null && minecraft.player != null && TempadConfig.allowExporting) {
            Minecraft.getInstance().setScreen(null);
            NetworkHandler.CHANNEL.sendToServer(new ExportLocationPacket(selectedLocation.getId(), hand));
        }
    }

    private void openNewLocationModal() {
        if (minecraft != null && minecraft.player != null) {
            findOrCreateEditWidget();
        }
    }

    private void newLocationAction(String name) {
        if (minecraft != null && minecraft.player != null) {
            Minecraft.getInstance().setScreen(null);
            NetworkHandler.CHANNEL.sendToServer(new AddLocationPacket(name, hand));
        }
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        renderBackground(graphics);
        graphics.blit(SCREEN, (width - TEMPAD_WIDTH) / 2, (height - TEMPAD_HEIGHT) / 2, TEMPAD_WIDTH, TEMPAD_HEIGHT, 0, 0, TEMPAD_WIDTH, TEMPAD_HEIGHT, 256, 256);
        super.render(graphics, mouseX, mouseY, partialTicks);
    }

    public <R extends Renderable & TemporaryWidget> R addTemporary(R renderable) {
        addRenderableOnly(renderable);
        this.temporaryWidgets.add(renderable);
        return renderable;
    }

    @Nullable
    @Override
    public GuiEventListener getFocused() {
        boolean visible = false;
        for (TemporaryWidget widget : this.temporaryWidgets) {
            visible |= widget.isVisible();
            if (widget.isVisible() && widget instanceof GuiEventListener listener) {
                return listener;
            }
        }
        if (visible) {
            return null;
        }
        return super.getFocused();
    }

    @Override
    public @NotNull List<? extends GuiEventListener> children() {
        List<GuiEventListener> listeners = new ArrayList<>();
        for (TemporaryWidget widget : temporaryWidgets) {
            if (widget.isVisible() && widget instanceof GuiEventListener listener) {
                listeners.add(listener);
            }
        }
        if (!listeners.isEmpty()) {
            return listeners;
        }
        return super.children();
    }

    public List<TemporaryWidget> temporaryWidgets() {
        return this.temporaryWidgets;
    }

    public void findOrCreateEditWidget() {
        boolean found = false;
        NewLocationModal widget = new NewLocationModal(this.width, this.height, (width - TEMPAD_WIDTH) / 2 + 70, (height - TEMPAD_HEIGHT) / 2 + 54, this::newLocationAction);
        for (TemporaryWidget temporaryWidget : this.temporaryWidgets()) {
            if (temporaryWidget instanceof NewLocationModal modal) {
                found = true;
                widget = modal;
                break;
            }
        }
        widget.setVisible(true);
        if (!found) {
            this.addTemporary(widget);
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
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
            guiGraphics.blit(SCREEN, this.getX(), this.getY(), this.getWidth(), this.getHeight(), buttonOffset * 14, this.getTextureY(), this.getWidth(), this.getHeight(), 256, 256);
        }

        protected int getTextureY() {
            int i = 0;
            if (!this.isActive()) {
                i = 2;
            } else if (this.isHovered()) {
                i = 1;
            }

            return 138 + i * this.getHeight();
        }
    }
}
