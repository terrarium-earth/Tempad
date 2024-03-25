package me.codexadrian.tempad.client.screens;

import me.codexadrian.tempad.api.apps.TempadAppApi;
import me.codexadrian.tempad.client.apps.impl.NewLocationApp;
import me.codexadrian.tempad.client.components.InformationPanel;
import me.codexadrian.tempad.client.components.MapWidget;
import me.codexadrian.tempad.client.components.ModSprites;
import me.codexadrian.tempad.client.components.SpriteButton;
import me.codexadrian.tempad.client.config.TempadClientConfig;
import me.codexadrian.tempad.common.Tempad;
import me.codexadrian.tempad.common.network.NetworkHandler;
import me.codexadrian.tempad.common.network.messages.c2s.AddLocationPacket;
import me.codexadrian.tempad.common.utils.LookupLocation;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class NewLocationScreen extends TempadScreen {
    private static final Component NAME_FIELD = Component.translatable("gui." + Tempad.MODID + ".name_field");

    public NewLocationScreen(LookupLocation search) {
        super(ModSprites.NEW_LOCATION, NewLocationApp.ID, search);
    }

    @Override
    protected void init() {
        super.init();
        addRenderableOnly(new MapWidget(localLeft + 101, localTop + 21, 92, 5));

        InformationPanel informationPanel = addRenderableWidget(new InformationPanel(this.localLeft + 6, this.localTop + 24, 91, 78));
        if (minecraft != null && minecraft.level != null && minecraft.player != null) {
            informationPanel.updateNameless(GlobalPos.of(minecraft.level.dimension(), minecraft.player.blockPosition()));
        }

        EditBox box = addRenderableWidget(new EditBox(this.font, this.localLeft + 8, this.localTop + 103, 74, 8, CommonComponents.EMPTY));
        box.setMaxLength(32);
        box.setBordered(false);
        box.setHint(NAME_FIELD);
        box.setTextColor(TempadClientConfig.color);

        addRenderableWidget(new SpriteButton(
            this.localLeft + 84, this.localTop + 100, 14, 14,
            ModSprites.SAVE, () -> {
            NetworkHandler.CHANNEL.sendToServer(new AddLocationPacket(box.getValue()));
            TempadAppApi.API.getHomePage().openOnClient(minecraft.player, getLookup());
        })).setTooltip(Tooltip.create(CommonComponents.GUI_DONE));
    }
}
