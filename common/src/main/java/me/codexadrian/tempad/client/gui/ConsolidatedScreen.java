package me.codexadrian.tempad.client.gui;

import me.codexadrian.tempad.api.options.TempadOption;
import me.codexadrian.tempad.client.components.InformationPanel;
import me.codexadrian.tempad.client.components.LocationPanel;
import me.codexadrian.tempad.client.components.ModSprites;
import me.codexadrian.tempad.client.components.ToggleButton;
import me.codexadrian.tempad.client.config.TempadClientConfig;
import me.codexadrian.tempad.client.screens.ModalScreen;
import me.codexadrian.tempad.common.Tempad;
import me.codexadrian.tempad.common.config.ConfigCache;
import me.codexadrian.tempad.common.data.LocationData;
import me.codexadrian.tempad.common.items.TempadItem;
import me.codexadrian.tempad.common.network.NetworkHandler;
import me.codexadrian.tempad.common.network.messages.c2s.*;
import me.codexadrian.tempad.common.utils.TeleportUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ConsolidatedScreen extends Screen {
    private static final ResourceLocation SCREEN = new ResourceLocation(Tempad.MODID, "textures/widget/tempad_screen.png");
    private static final int TEMPAD_WIDTH = 249;
    private static final int TEMPAD_HEIGHT = 138;

    private final List<LocationData> locations;
    private UUID favorite;
    private LocationData selectedLocation;
    private InformationPanel informationPanel;
    private LocationPanel locationPanel;

    private ToggleButton favoriteButton;
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

        addRenderableOnly((graphics, mouseX, mouseY, partialTicks) -> {
            graphics.blit(SCREEN, (width - TEMPAD_WIDTH) / 2, (height - TEMPAD_HEIGHT) / 2, TEMPAD_WIDTH, TEMPAD_HEIGHT, 0, 0, TEMPAD_WIDTH, TEMPAD_HEIGHT, 256, 256);
        });

        addRenderableOnly((graphics, mouseX, mouseY, partialTicks) -> {
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
        });

        informationPanel = addRenderableWidget(new InformationPanel(cornerX + 16, cornerY + 33, 91, 78));
        locationPanel = addRenderableWidget(new LocationPanel(cornerX + 129, cornerY + 31, 91, 92, textEntry -> {
            if (textEntry != null && textEntry.data != null) {
                selectedLocation = textEntry.data;

                informationPanel.setLocation(selectedLocation);

                favoriteButton.setSelected(selectedLocation.getId().equals(favorite));
                favoriteButton.visible = true;
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

        EditBox editBox = addRenderableWidget(new EditBox(font,
            cornerX + 139, cornerY + 16,
            66, 12,
            Component.translatable("gui." + Tempad.MODID + ".search_field")
        ));
        editBox.setBordered(false);
        editBox.setHint(Component.translatable("gui." + Tempad.MODID + ".search_field"));
        editBox.setTextColor(TempadClientConfig.color);
        editBox.setResponder(this::updateLocations);

        updateLocations("");
        if (!locations.isEmpty()) {
            informationPanel.setLocation(null);
        }

        favoriteButton = addRenderableWidget(new ToggleButton(
            cornerX + 46, cornerY + 110, 14, 14,
            ModSprites.UNFAVORITE, ModSprites.FAVORITE,
            b -> favoriteAction()
        ));
        favoriteButton.setTooltip(Tooltip.create(Component.translatable("gui." + Tempad.MODID + ".favorite")));
        favoriteButton.visible = false;

        downloadButton = addRenderableWidget(new TempadButton(cornerX + 62, cornerY + 110, 14, 14, Component.translatable("gui." + Tempad.MODID + ".download"), (button) -> exportAction(), 2));
        downloadButton.visible = false;
        deleteButton = addRenderableWidget(new TempadButton(cornerX + 78, cornerY + 110, 14, 14, Component.translatable("gui." + Tempad.MODID + ".delete"), (button) -> deleteAction(), 3));
        deleteButton.visible = false;
        teleportButton = addRenderableWidget(new TempadButton(cornerX + 94, cornerY + 110, 14, 14, Component.translatable("gui." + Tempad.MODID + ".teleport"), (button) -> teleportAction(), 4));
        teleportButton.visible = false;
        addRenderableWidget(new TempadButton(cornerX + 208, cornerY + 14, 12, 12, Component.translatable("gui." + Tempad.MODID + ".add_location"), (button) -> openNewLocationModal(), 5)).setTooltip(Tooltip.create(Component.translatable("gui." + Tempad.MODID + ".add_location")));
    }

    private void updateLocations(String text) {
        locationPanel.update(text, this.locations, id -> id.equals(favorite));
    }

    private void favoriteAction() {
        if (minecraft != null && minecraft.player != null) {
            favorite = selectedLocation.getId().equals(favorite) ? null : selectedLocation.getId();
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
            updateLocations("");
            informationPanel.setLocation(null);

            favoriteButton.visible = false;
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
        ModalScreen.open(name -> NetworkHandler.CHANNEL.sendToServer(new AddLocationPacket(name)));
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
