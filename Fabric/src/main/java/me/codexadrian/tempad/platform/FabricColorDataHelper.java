package me.codexadrian.tempad.platform;

import me.codexadrian.tempad.ColorDataComponent;
import me.codexadrian.tempad.platform.services.IColorDataHelper;
import net.minecraft.world.entity.player.Player;

public class FabricColorDataHelper implements IColorDataHelper {

    @Override
    public int getColor(Player player) {
        return ColorDataComponent.COLOR_DATA.get(player).getColor();
    }

    @Override
    public void setColor(int color, Player player) {
        ColorDataComponent.COLOR_DATA.get(player).setColor(color);
        ColorDataComponent.COLOR_DATA.sync(player);
    }
}
