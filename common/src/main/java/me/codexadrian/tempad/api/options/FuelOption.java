package me.codexadrian.tempad.api.options;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

public interface FuelOption {

    /**
     * Determines whether the timedoor can be opened for the player with the given Tempad item stack.
     *
     * @param player the player trying to open the timedoor
     * @param stack the item stack being used to open the timedoor
     * @return true if the timedoor can be opened, false otherwise
     */
    boolean canTimedoorOpen(Player player, ItemStack stack);

    /**
     * Called when a timedoor is opened by a player using a Tempad item stack.
     *
     * @param player the player who is opening the timedoor
     * @param stack  the Tempad item stack being used to open the timedoor
     */
    void onTimedoorOpen(Player player, ItemStack stack);

    /**
     * Adds tooltip information to the provided item stack when rendering a GUI.
     *
     * @param stack      the item stack to add the tooltip to
     * @param level      the level the item stack is in
     * @param components the list of tooltip components
     * @param flag       the tooltip flag
     */
    default void addToolTip(ItemStack stack, Level level, List<Component> components, TooltipFlag flag) {}

    /**
     * Determines whether the durability bar should be visible for the given item stack.
     *
     * @param stack the item stack to check
     * @return true if the durability bar should be visible, false otherwise
     */
    default boolean isDurabilityBarVisible(ItemStack stack) {
        return false;
    }

    /**
     * Returns the percentage value of the given item stack.
     *
     * @param stack the item stack to calculate the percentage for
     * @return the percentage value of the item stack
     */
    default double getPercentage(ItemStack stack) {
        return 1;
    }

    /**
     * Performs a tick operation on the given ItemStack and Entity.
     * This method is called every tick while the ItemStack is in the player's inventory or Curio/Trinkets slot.
     *
     * @param stack  the ItemStack being ticked
     * @param entity the Entity being ticked
     */
    default void tick(ItemStack stack, Entity entity) {};
}
