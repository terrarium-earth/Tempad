package me.codexadrian.tempad.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.teamresourceful.resourcefullib.client.components.selection.SelectionList;
import me.codexadrian.tempad.api.options.TempadOption;
import me.codexadrian.tempad.client.config.TempadClientConfig;
import me.codexadrian.tempad.client.widgets.NewLocationModal;
import me.codexadrian.tempad.client.widgets.TemporaryWidget;
import me.codexadrian.tempad.client.widgets.TextEntry;
import me.codexadrian.tempad.common.Tempad;
import me.codexadrian.tempad.common.config.ConfigCache;
import me.codexadrian.tempad.common.data.LocationData;
import me.codexadrian.tempad.common.network.NetworkHandler;
import me.codexadrian.tempad.common.items.TempadItem;
import me.codexadrian.tempad.common.network.messages.c2s.*;
import me.codexadrian.tempad.common.utils.TeleportUtils;
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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ConsolidatedScreen extends Screen {
    private static final ResourceLocation SCREEN = new ResourceLocation(Tempad.MODID, "textures/widget/tempad_screen.png");
    private static final int TEMPAD_WIDTH = 249;
    private static final int TEMPAD_HEIGHT = 138;

    protected final List<TemporaryWidget> temporaryWidgets = new ArrayList<>();
    private final List<LocationData> locations;
    private UUID favorite;
    private LocationData selectedLocation;
    private SelectionList<TextEntry> informationPanel;
    private SelectionList<TextEntry> locationPanel;
    private Button favoriteButton;
    private Button unfavoriteButton;
    private Button downloadButton;
    private Button deleteButton;
    private Button teleportButton;

    public ConsolidatedScreen(List<LocationData> locations, UUID favorite) {
        super(Component.nullToEmpty(""));
        this.locations = locations;
        this.favorite = favorite;
    }

    @Override
    protected void init() {
        super.init();
        int offset = 3;
        int cornerX = (width - TEMPAD_WIDTH) / 2;
        int cornerY = (height - TEMPAD_HEIGHT) / 2;
        informationPanel = addRenderableWidget(new SelectionList<>(cornerX + 16, cornerY + 33, 91, 78, 10, textEntry -> {}));
        locationPanel = addRenderableWidget(new SelectionList<>(cornerX + 129, cornerY + 31, 91, 92, 10, textEntry -> {
            if (textEntry != null && textEntry.data != null) {
                selectedLocation = textEntry.data;

                informationPanel.updateEntries(List.of(
                    new TextEntry(Component.literal(selectedLocation.getName())),
                    new TextEntry(Component.translatable("gui." + Tempad.MODID + ".x", Mth.floor(selectedLocation.getBlockPos().getX()))),
                    new TextEntry(Component.translatable("gui." + Tempad.MODID + ".y", Mth.floor(selectedLocation.getBlockPos().getY()))),
                    new TextEntry(Component.translatable("gui." + Tempad.MODID + ".z", Mth.floor(selectedLocation.getBlockPos().getZ()))),
                    new TextEntry(Component.translatable("gui." + Tempad.MODID + ".dimension", Component.translatable(selectedLocation.getLevelKey().location().toLanguageKey("dimension")))))
                );

                if (selectedLocation.getId().equals(favorite)) {
                    favoriteButton.visible = false;
                    unfavoriteButton.visible = true;
                } else {
                    favoriteButton.visible = true;
                    unfavoriteButton.visible = false;
                }
                downloadButton.visible = true;
                deleteButton.visible = true;
                teleportButton.visible = true;

                deleteButton.active = selectedLocation.isDeletable();
                if (selectedLocation.isDeletable()) {
                    deleteButton.setTooltip(Tooltip.create(Component.translatable("gui." + Tempad.MODID + ".delete")));
                } else {
                    deleteButton.setTooltip(Tooltip.create(Component.translatable("gui." + Tempad.MODID + ".delete_disabled")));
                }

                if (minecraft != null && minecraft.player != null) {
                    ItemStack itemInHand = TeleportUtils.findTempad(minecraft.player);
                    boolean isTempadUsable = itemInHand.getItem() instanceof TempadItem tempadItem && tempadItem.getOption().canTimedoorOpen(minecraft.player, itemInHand);
                    teleportButton.active = selectedLocation.isTeleportable() && TeleportUtils.mayTeleport(selectedLocation.getLevelKey(), minecraft.player) && isTempadUsable;
                    if (selectedLocation.isTeleportable()) {
                        teleportButton.setTooltip(Tooltip.create(Component.translatable("gui." + Tempad.MODID + ".teleport")));
                    } else {
                        teleportButton.setTooltip(Tooltip.create(Component.translatable("gui." + Tempad.MODID + ".teleport_disabled")));
                    }

                    downloadButton.active = selectedLocation.isDownloadable() && ConfigCache.allowExporting && (!ConfigCache.consumeCooldown || isTempadUsable);
                    if (selectedLocation.isDownloadable()) {
                        downloadButton.setTooltip(Tooltip.create(Component.translatable("gui." + Tempad.MODID + ".download")));
                    } else {
                        downloadButton.setTooltip(Tooltip.create(Component.translatable("gui." + Tempad.MODID + ".download_disabled")));
                    }
                }
            }
        }));
        if (locations.isEmpty()) {
            locationPanel.updateEntries(List.of(
                new TextEntry(Component.translatable("gui." + Tempad.MODID + ".no_locations.first_line")),
                new TextEntry(Component.translatable("gui." + Tempad.MODID + ".no_locations.second_line"))
            ));
        } else {
            locationPanel.updateEntries(locations.stream().map(locationData -> new TextEntry(locationData, locData -> locData.getId().equals(favorite))).toList());
            informationPanel.updateEntries(List.of(
                new TextEntry(Component.translatable("gui." + Tempad.MODID + ".no_selection.first_line")),
                new TextEntry(Component.translatable("gui." + Tempad.MODID + ".no_selection.second_line"))
            ));
        }

        EditBox editBox = new EditBox(font, cornerX + 139, cornerY + 16, 66, 12, Component.translatable("gui." + Tempad.MODID + ".search_field"));
        editBox.setBordered(false);
        editBox.setHint(Component.translatable("gui." + Tempad.MODID + ".search_field"));
        editBox.setTextColor(TempadClientConfig.color);
        if (!locations.isEmpty()) editBox.setResponder((string) -> locationPanel.updateEntries(locations.stream().filter(locationData -> locationData.getName().toLowerCase().contains(string.toLowerCase())).map(locationData -> new TextEntry(locationData, locData -> locData.getId().equals(favorite))).toList()));
        addRenderableWidget(editBox);

        favoriteButton = addRenderableWidget(new TempadButton(cornerX + 46, cornerY + 110, 14, 14, Component.translatable("gui." + Tempad.MODID + ".favorite"), (button) -> favoriteAction(), 0));
        favoriteButton.setTooltip(Tooltip.create(Component.translatable("gui." + Tempad.MODID + ".favorite")));
        favoriteButton.visible = false;
        unfavoriteButton = addRenderableWidget(new TempadButton(cornerX + 46, cornerY + 110, 14, 14, Component.translatable("gui." + Tempad.MODID + ".unfavorite"), (button) -> favoriteAction(), 1));
        unfavoriteButton.setTooltip(Tooltip.create(Component.translatable("gui." + Tempad.MODID + ".unfavorite")));
        unfavoriteButton.visible = false;
        downloadButton = addRenderableWidget(new TempadButton(cornerX + 62, cornerY + 110, 14, 14, Component.translatable("gui." + Tempad.MODID + ".download"), (button) -> exportAction(), 2));
        downloadButton.visible = false;
        deleteButton = addRenderableWidget(new TempadButton(cornerX + 78, cornerY + 110, 14, 14, Component.translatable("gui." + Tempad.MODID + ".delete"), (button) -> deleteAction(), 3));
        deleteButton.visible = false;
        teleportButton = addRenderableWidget(new TempadButton(cornerX + 94, cornerY + 110, 14, 14, Component.translatable("gui." + Tempad.MODID + ".teleport"), (button) -> teleportAction(), 4));
        teleportButton.visible = false;
        addRenderableWidget(new TempadButton(cornerX + 208, cornerY + 14, 12, 12, Component.translatable("gui." + Tempad.MODID + ".add_location"), (button) -> openNewLocationModal(), 5)).setTooltip(Tooltip.create(Component.translatable("gui." + Tempad.MODID + ".add_location")));
    }

    private void favoriteAction() {
        if (minecraft != null && minecraft.player != null) {
            if (selectedLocation.getId().equals(favorite)) {
                favorite = null;
                favoriteButton.visible = true;
                unfavoriteButton.visible = false;
            } else {
                favorite = selectedLocation.getId();
                favoriteButton.visible = false;
                unfavoriteButton.visible = true;
            }
            NetworkHandler.CHANNEL.sendToServer(new FavoriteLocationPacket(favorite));
        }
    }

    private void teleportAction() {
        if (minecraft != null && minecraft.player != null) {
            NetworkHandler.CHANNEL.sendToServer(new SummonTimedoorPacket(selectedLocation.getId(), TempadClientConfig.color));
            Minecraft.getInstance().setScreen(null);
        }
    }

    private void deleteAction() {
        if (minecraft != null && minecraft.player != null) {
            NetworkHandler.CHANNEL.sendToServer(new DeleteLocationPacket(selectedLocation.getId()));
            locations.removeIf(locationData -> locationData.getId().equals(selectedLocation.getId()));
            selectedLocation = null;
            locationPanel.updateEntries(locations.stream().map(locationData -> new TextEntry(locationData, locData -> locData.getId().equals(favorite))).toList());
            informationPanel.updateEntries(List.of(
                new TextEntry(Component.translatable("gui." + Tempad.MODID + ".no_selection.first_line")),
                new TextEntry(Component.translatable("gui." + Tempad.MODID + ".no_selection.second_line"))
            ));
            favoriteButton.visible = false;
            unfavoriteButton.visible = false;
            downloadButton.visible = false;
            deleteButton.visible = false;
            teleportButton.visible = false;
        }
    }

    private void exportAction() {
        if (minecraft != null && minecraft.player != null && ConfigCache.allowExporting) {
            Minecraft.getInstance().setScreen(null);
            NetworkHandler.CHANNEL.sendToServer(new ExportLocationPacket(selectedLocation.getId()));
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
            NetworkHandler.CHANNEL.sendToServer(new AddLocationPacket(name));
        }
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        renderBackground(graphics);
        graphics.blit(SCREEN, (width - TEMPAD_WIDTH) / 2, (height - TEMPAD_HEIGHT) / 2, TEMPAD_WIDTH, TEMPAD_HEIGHT, 0, 0, TEMPAD_WIDTH, TEMPAD_HEIGHT, 256, 256);

        super.render(graphics, mouseX, mouseY, partialTicks);

        int barHeight = 0;
        ItemStack tempad = TeleportUtils.findTempad(minecraft.player);
        if (tempad.getItem() instanceof TempadItem item) {
            TempadOption option = item.getOption();
            if(option.isDurabilityBarVisible(tempad)) {
                barHeight = (int) (option.getPercentage(tempad) * 54);
            } else {
                barHeight = option.canTimedoorOpen(minecraft.player, tempad) ? 54 : 0;
            }

            graphics.blit(SCREEN, (width - TEMPAD_WIDTH) / 2 + 234, (height - TEMPAD_HEIGHT) / 2 + 42 + 54 - barHeight, 6, barHeight, 249, 54 - barHeight, 6, barHeight, 256, 256);

            if (mouseX >= (width - TEMPAD_WIDTH) / 2 + 234 && mouseX <= (width - TEMPAD_WIDTH) / 2 + 240 && mouseY >= (height - TEMPAD_HEIGHT) / 2 + 42 && mouseY <= (height - TEMPAD_HEIGHT) / 2 + 96) {
                List<Component> tooltip = new ArrayList<>();
                option.addToolTip(tempad, minecraft.level, tooltip, TooltipFlag.NORMAL);
                graphics.renderTooltip(font, tooltip, Optional.empty(), mouseX, mouseY);
            }
        }
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
