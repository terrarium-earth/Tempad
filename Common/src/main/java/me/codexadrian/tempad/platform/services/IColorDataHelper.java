package me.codexadrian.tempad.platform.services;

import net.minecraft.world.entity.player.Player;

public interface IColorDataHelper {
    int getColor(Player player);
    void setColor(int color, Player player);
}
