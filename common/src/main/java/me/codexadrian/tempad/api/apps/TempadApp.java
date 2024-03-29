package me.codexadrian.tempad.api.apps;

import me.codexadrian.tempad.client.screens.TempadScreen;
import me.codexadrian.tempad.common.utils.LookupLocation;
import me.codexadrian.tempad.common.utils.TeleportUtils;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

public interface TempadApp {
    WidgetSprites getWidgetSprites();

    void openOnClient(Player player, LookupLocation lookup);

    void openOnServer(Player player, LookupLocation lookup);

    default void openOnServer(Player player) {
        openOnServer(player, TeleportUtils.findTempad(player).getSecond());
    }

    default void openOnClient(Player player) {
        openOnClient(player, TeleportUtils.findTempad(player).getSecond());
    }

    default int priority() {
        return 0;
    }
}
