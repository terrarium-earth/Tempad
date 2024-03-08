package me.codexadrian.tempad.client.screens;

import me.codexadrian.tempad.client.components.*;
import me.codexadrian.tempad.client.screens.base.BackgroundMenuScreen;
import me.codexadrian.tempad.common.config.ConfigCache;
import me.codexadrian.tempad.common.data.LocationData;
import me.codexadrian.tempad.common.items.TempadItem;
import me.codexadrian.tempad.common.menu.PrinterMenu;
import me.codexadrian.tempad.common.network.NetworkHandler;
import me.codexadrian.tempad.common.network.messages.c2s.WriteLocationCard;
import me.codexadrian.tempad.common.utils.TeleportUtils;
import net.minecraft.Optionull;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class PrinterScreen extends BackgroundMenuScreen<PrinterMenu> {
    private LocationData selectedLocation;
    private InformationPanel informationPanel;
    private LocationPanel locationPanel;
    private SpriteButton writeButton;

    private EditBox search;

    public PrinterScreen(PrinterMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title, 190, 250, ModSprites.PRINTER_SCREEN);
        this.inventoryLabelX = 14;
        this.inventoryLabelY = 155;
    }

    @Override
    protected void init() {
        super.init();
        this.informationPanel = addRenderableWidget(new InformationPanel(leftPos + 17, topPos + 34, 77, 71));
        this.locationPanel = addRenderableWidget(new LocationPanel(
            leftPos + 100, topPos + 34, 74, 92, this.getMenu().locations, id -> false, this::select
        ));

        if (!this.getMenu().locations.isEmpty()) {
            informationPanel.update(null);
        }

        this.writeButton = addRenderableWidget(new SpriteButton(leftPos + 82, topPos + 112, 12, 12, ModSprites.SAVE, this::write));
        this.writeButton.setActivated(false);

        String searchValue = Optionull.mapOrDefault(search, EditBox::getValue, "");
        this.search = addRenderableWidget(ModWidgets.search(leftPos + 108, topPos + 18, 66, 12, locationPanel::update));
        this.search.setValue(searchValue);

        this.locationPanel.select(this.selectedLocation);
    }

    private void select(TextEntry entry) {
        if (entry == null) return;
        if (entry.data == null) return;
        selectedLocation = entry.data;

        informationPanel.update(selectedLocation);
        writeButton.setActivated(selectedLocation.isDownloadable() && ConfigCache.allowExporting);
    }

    private void write() {
        if (selectedLocation == null) return;
        NetworkHandler.CHANNEL.sendToServer(new WriteLocationCard(this.getMenu().printerPos, selectedLocation.getId()));
    }


    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, 0xFF453d27, false);
    }

    @Override
    protected void renderTooltip(GuiGraphics guiGraphics, int x, int y) {
        super.renderTooltip(guiGraphics, x, y);
    }
}
