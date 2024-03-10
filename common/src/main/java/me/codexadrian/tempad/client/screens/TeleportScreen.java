package me.codexadrian.tempad.client.screens;

import me.codexadrian.tempad.api.options.FuelOption;
import me.codexadrian.tempad.client.apps.impl.TeleportApp;
import me.codexadrian.tempad.client.components.*;
import me.codexadrian.tempad.client.config.TempadClientConfig;
import me.codexadrian.tempad.client.screens.base.BackgroundScreen;
import me.codexadrian.tempad.common.config.ConfigCache;
import me.codexadrian.tempad.common.data.LocationData;
import me.codexadrian.tempad.common.items.TempadItem;
import me.codexadrian.tempad.common.network.NetworkHandler;
import me.codexadrian.tempad.common.network.messages.c2s.*;
import me.codexadrian.tempad.common.utils.TeleportUtils;
import net.minecraft.Optionull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TeleportScreen extends TempadScreen {

    private final List<LocationData> locations;
    private UUID favorite;
    private LocationData selectedLocation;

    private InformationPanel informationPanel;
    private LocationPanel locationPanel;

    private EditBox search;
    private ToggleButton favoriteButton;
    private SpriteButton downloadButton;
    private SpriteButton deleteButton;
    private SpriteButton teleportButton;

    public TeleportScreen(List<LocationData> locations, @Nullable UUID favorite) {
        super(ModSprites.TELEPORT_SCREEN, TeleportApp.ID);
        this.locations = locations;
        this.favorite = favorite;
    }

    @Override
    protected void init() {
        super.init();

        this.informationPanel = addRenderableWidget(new InformationPanel(this.left + 16, this.top + 33, 91, 78));
        this.locationPanel = addRenderableWidget(new LocationPanel(
            left + 113, top + 33, 91, 92, this.locations, id -> id.equals(favorite), this::select
        ));

        String searchValue = Optionull.mapOrDefault(search, EditBox::getValue, "");
        this.search = addRenderableWidget(ModWidgets.search(localLeft + 113, localTop + 7, 66, 12, locationPanel::update));
        this.search.setValue(searchValue);

        if (!locations.isEmpty()) {
            informationPanel.update(null);
        }

        favoriteButton = addRenderableWidget(ModWidgets.favorite(
            left + 46, top + 110, 14, 14, this::favoriteAction
        ));
        downloadButton = addRenderableWidget(ModWidgets.download(
            left + 62, top + 110, 14, 14, this::exportAction
        ));
        deleteButton = addRenderableWidget(ModWidgets.delete(
            left + 78, top + 110, 14, 14, this::deleteAction
        ));
        teleportButton = addRenderableWidget(ModWidgets.teleport(
            left + 94, top + 110, 14, 14, this::teleportAction
        ));

        this.locationPanel.select(this.selectedLocation);
    }

    private void select(TextEntry entry) {
        if (entry == null) return;
        if (entry.data == null) return;
        selectedLocation = entry.data;

        informationPanel.update(selectedLocation);
        favoriteButton.setSelected(selectedLocation.getId().equals(favorite));
        favoriteButton.visible = true;
        downloadButton.visible = true;
        deleteButton.visible = true;
        teleportButton.visible = true;

        if (minecraft != null && minecraft.player != null) {
            ItemStack itemInHand = TeleportUtils.findTempad(minecraft.player);
            boolean isTempadUsable = itemInHand.getItem() instanceof TempadItem tempadItem && tempadItem.getOption().canTimedoorOpen(minecraft.player, itemInHand);
            teleportButton.setActivated(selectedLocation.isTeleportable() && TeleportUtils.mayTeleport(selectedLocation.getLevelKey(), minecraft.player) && isTempadUsable);
            downloadButton.setActivated(selectedLocation.isDownloadable() && ConfigCache.allowExporting && TeleportUtils.hasLocationCard(minecraft.player));
            deleteButton.setActivated(selectedLocation.isDeletable());
        }
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
            this.locationPanel.update(this.search.getValue());
            informationPanel.update(null);

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
}
