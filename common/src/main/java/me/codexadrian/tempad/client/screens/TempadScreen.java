package me.codexadrian.tempad.client.screens;

import com.teamresourceful.resourcefullib.client.screens.BaseCursorScreen;
import me.codexadrian.tempad.api.options.TempadOption;
import me.codexadrian.tempad.client.components.*;
import me.codexadrian.tempad.client.config.TempadClientConfig;
import me.codexadrian.tempad.client.widgets.TextEntry;
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
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TempadScreen extends BaseCursorScreen {

    private static final int TEMPAD_WIDTH = 249;
    private static final int TEMPAD_HEIGHT = 138;

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

    public TempadScreen(List<LocationData> locations, UUID favorite) {
        super(CommonComponents.EMPTY);
        this.locations = locations;
        this.favorite = favorite;
    }

    @Override
    protected void init() {
        super.init();
        int left = (width - TEMPAD_WIDTH) / 2;
        int top = (height - TEMPAD_HEIGHT) / 2;

        informationPanel = addRenderableWidget(new InformationPanel(left + 16, top + 33, 91, 78));
        locationPanel = addRenderableWidget(new LocationPanel(
            left + 129, top + 31, 91, 92, this.locations, id -> id.equals(favorite), this::select
        ));

        String searchValue = Optionull.mapOrDefault(search, EditBox::getValue, "");
        this.search = addRenderableWidget(ModWidgets.search(left + 139, top + 16, 66, 12, locationPanel::update));
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

        addRenderableWidget(ModWidgets.add(
            left + 208, top + 14, 12, 12,
            () -> ModalScreen.open(name -> NetworkHandler.CHANNEL.sendToServer(new AddLocationPacket(name)))
        ));
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
            downloadButton.setActivated(selectedLocation.isDownloadable() && ConfigCache.allowExporting && (!ConfigCache.consumeCooldown || isTempadUsable));
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

    @Override
    public void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.renderBackground(graphics, mouseX, mouseY, partialTick);
        int left = (width - TEMPAD_WIDTH) / 2;
        int top = (height - TEMPAD_HEIGHT) / 2;

        graphics.blitSprite(ModSprites.SCREEN, left, top, TEMPAD_WIDTH, TEMPAD_HEIGHT);
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);

        if (minecraft == null) return;
        int left = (width - TEMPAD_WIDTH) / 2;
        int top = (height - TEMPAD_HEIGHT) / 2;

        ItemStack tempad = TeleportUtils.findTempad(minecraft.player);
        if (tempad.getItem() instanceof TempadItem item) {
            TempadOption option = item.getOption();
            int barHeight;
            if(option.isDurabilityBarVisible(tempad)) {
                barHeight = (int) (option.getPercentage(tempad) * 54);
            } else {
                barHeight = option.canTimedoorOpen(minecraft.player, tempad) ? 54 : 0;
            }

            graphics.blitSprite(ModSprites.BAR, 6, 54, 0, 0, left + 234, top + 42 + 54 - barHeight, 6, barHeight);

            if (mouseX >= left + 234 && mouseX <= left + 240 && mouseY >= top + 42 && mouseY <= top + 96) {
                List<Component> tooltip = new ArrayList<>();
                option.addToolTip(tempad, minecraft.level, tooltip, TooltipFlag.NORMAL);
                setTooltipForNextRenderPass(tooltip.stream().map(Component::getVisualOrderText).toList());
            }
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
