package me.codexadrian.tempad.tempad;


import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class TempadOfHeWhoRemainsItem extends TempadItem {

    public TempadOfHeWhoRemainsItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendedText(@NotNull ItemStack stack, @NotNull List<Component> components) {
        components.add(new TranslatableComponent("item.tempad.he_who_remains_tempad.tooltip").withStyle(ChatFormatting.GRAY));
    }

    @Override
    public boolean checkIfUsable(ItemStack tempad) {
        return true;
    }

    @Override
    public void handleUsage(ItemStack tempad) {
        //Do nothin
    }
}
