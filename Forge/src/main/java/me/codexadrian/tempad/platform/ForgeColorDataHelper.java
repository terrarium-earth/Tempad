package me.codexadrian.tempad.platform;

import me.codexadrian.tempad.Constants;
import me.codexadrian.tempad.ForgeTempad;
import me.codexadrian.tempad.platform.services.IColorDataHelper;
import net.minecraft.world.entity.player.Player;

public class ForgeColorDataHelper implements IColorDataHelper {
    @Override
    public int getColor(Player player) {
        return player.getCapability(ForgeTempad.INSTANCE).map(colorData -> colorData.color).orElse(Constants.ORANGE);
    }

    @Override
    public void setColor(int color, Player player) {
        player.getCapability(ForgeTempad.INSTANCE).ifPresent(colorData -> colorData.color = color);
    }
}
