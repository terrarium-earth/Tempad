package me.codexadrian.tempad.common.options.impl;

import me.codexadrian.tempad.api.options.FuelOption;
import me.codexadrian.tempad.api.power.PowerSettings;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

public class ExperienceLevelOption implements FuelOption {

    @Override
    public boolean canTimedoorOpen(Object dataHolder, PowerSettings attachment, Player player) {
        return player.experienceLevel >= attachment.getFuelCost();
    }

    @Override
    public void onTimedoorOpen(Object dataHolder, PowerSettings attachment, Player player) {
        player.giveExperienceLevels(-attachment.getFuelCost());
    }

    @Override
    public void addToolTip(Object dataHolder, PowerSettings attachment, Level level, List<Component> components, TooltipFlag flag) {
        components.add(Component.translatable("tooltip.tempad.experience_level_cost", attachment.getFuelCost()));
    }
}

