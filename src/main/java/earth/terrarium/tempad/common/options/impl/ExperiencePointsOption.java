package earth.terrarium.tempad.common.options.impl;

import earth.terrarium.tempad.api.options.FuelOption;
import earth.terrarium.tempad.api.power.PowerSettings;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

public class ExperiencePointsOption implements FuelOption {

    @Override
    public boolean canTimedoorOpen(Object dataHolder, PowerSettings attachment, Player player) {
        return player.totalExperience >= attachment.getFuelCost();
    }

    @Override
    public void onTimedoorOpen(Object dataHolder, PowerSettings attachment, Player player) {
        player.giveExperiencePoints(-attachment.getFuelCost());
    }

    @Override
    public void addToolTip(Object dataHolder, PowerSettings attachment, Level level, List<Component> components, TooltipFlag flag) {
        components.add(Component.translatable("tooltip.tempad.experience_points_cost", attachment.getFuelCost()));
    }
}
