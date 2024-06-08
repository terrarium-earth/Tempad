package earth.terrarium.tempad.common.options.impl;

import earth.terrarium.botarium.common.energy.base.EnergyContainer;
import earth.terrarium.tempad.api.options.FuelOption;
import earth.terrarium.tempad.api.power.PowerSettings;
import earth.terrarium.tempad.common.Tempad;
import earth.terrarium.tempad.common.registry.TempadData;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

public class EnergyOption implements FuelOption {
    public static final ResourceLocation ID = new ResourceLocation(Tempad.MODID, "energy");

    @Override
    public boolean canTimedoorOpen(Object dataHolder, PowerSettings attachment, Player player) {
        EnergyContainer energyStorage = TempadData.ENERGY.getData(dataHolder);
        if (energyStorage == null) return false;
        return energyStorage.getStoredEnergy() >= attachment.getFuelCost();
    }

    @Override
    public void addToolTip(Object dataHolder, PowerSettings attachment, Level level, List<Component> components, TooltipFlag flag) {
        EnergyContainer energyStorage = TempadData.ENERGY.getData(dataHolder);
        if (energyStorage == null) return;
        components.add(Component.translatable("tempad_option.tempad.energy", energyStorage.getStoredEnergy(), energyStorage.getMaxCapacity()).withStyle(ChatFormatting.GRAY));
        components.add(Component.translatable("tempad_option.tempad.energy_cost", attachment.getFuelCost()).withStyle(ChatFormatting.GRAY));
    }

    @Override
    public void onTimedoorOpen(Object dataHolder, PowerSettings attachment, Player player) {
        var energyStorage = TempadData.ENERGY.getData(dataHolder);
        if (energyStorage == null) return;
        energyStorage.internalExtract(attachment.getFuelCost(), false);
    }

    @Override
    public boolean isDurabilityBarVisible(Object dataHolder, PowerSettings attachment) {
        return true;
    }

    @Override
    public double getPercentage(Object dataHolder, PowerSettings attachment) {
        EnergyContainer energyStorage = TempadData.ENERGY.getData(dataHolder);
        if (energyStorage == null) return 0;
        return (double) energyStorage.getStoredEnergy() / energyStorage.getMaxCapacity();
    }
}
