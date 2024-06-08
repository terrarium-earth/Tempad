package earth.terrarium.tempad.common.options.impl;

import earth.terrarium.tempad.api.options.FuelOption;
import earth.terrarium.tempad.api.power.PowerSettings;
import net.minecraft.world.entity.player.Player;

public class UnlimitedOption implements FuelOption {

    @Override
    public boolean canTimedoorOpen(Object dataHolder, PowerSettings attachment, Player player) {
        return true;
    }

    @Override
    public void onTimedoorOpen(Object dataHolder, PowerSettings attachment, Player player) {}
}
