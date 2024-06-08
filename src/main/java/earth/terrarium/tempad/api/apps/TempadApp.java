package earth.terrarium.tempad.api.apps;

import earth.terrarium.tempad.client.screens.TempadScreen;
import earth.terrarium.tempad.common.utils.LookupLocation;
import earth.terrarium.tempad.common.utils.TeleportUtils;
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
