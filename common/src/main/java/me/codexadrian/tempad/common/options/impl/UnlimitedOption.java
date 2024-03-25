package me.codexadrian.tempad.common.options.impl;

import me.codexadrian.tempad.api.options.FuelOption;
import me.codexadrian.tempad.api.power.PowerSettings;
import net.minecraft.world.entity.player.Player;

public class UnlimitedOption implements FuelOption {

    @Override
    public boolean canTimedoorOpen(Object dataHolder, PowerSettings attachment, Player player) {
        return true;
    }

    @Override
    public void onTimedoorOpen(Object dataHolder, PowerSettings attachment, Player player) {}
}
