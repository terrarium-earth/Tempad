package earth.terrarium.tempad.client.screens;

import earth.terrarium.tempad.client.components.*;
import earth.terrarium.tempad.client.screens.base.BackgroundMenuScreen;
import earth.terrarium.tempad.common.config.ConfigCache;
import earth.terrarium.tempad.common.data.LocationData;
import earth.terrarium.tempad.common.menu.PrinterMenu;
import earth.terrarium.tempad.common.network.NetworkHandler;
import earth.terrarium.tempad.common.network.messages.c2s.WriteLocationCard;
import net.minecraft.Optionull;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class PrinterScreen extends BackgroundMenuScreen<PrinterMenu> {
    private LocationData selectedLocation;
    private InformationPanel informationPanel;
    private LocationPanel locationPanel;
    private SpriteButton writeButton;

    private EditBox search;

    public PrinterScreen(PrinterMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title, 206, 201, ModSprites.PRINTER_SCREEN);
    }

    @Override
    protected void init() {
        super.init();
        this.informationPanel = addRenderableWidget(new InformationPanel(leftPos + 11, topPos + 1, 58, 58, 0xFF444444, false));
        this.locationPanel = addRenderableWidget(new LocationPanel(
            leftPos + 97, topPos + 33, 92, 52, this.getMenu().locations, id -> false, this::select
        ));

        if (!this.getMenu().locations.isEmpty()) {
            informationPanel.updateBlank(false);
        }

        this.writeButton = addRenderableWidget(new SpriteButton(leftPos + 82, topPos + 112, 12, 12, ModSprites.SAVE, this::write));
        this.writeButton.setActivated(false);

        String searchValue = Optionull.mapOrDefault(search, EditBox::getValue, "");
        this.search = addRenderableWidget(ModWidgets.search(leftPos + 109, topPos + 19, 66, 12, locationPanel::update));
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
        NetworkHandler.CHANNEL.sendToServer(new WriteLocationCard(selectedLocation.id()));
    }


    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
    }
}
