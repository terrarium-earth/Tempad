package me.codexadrian.tempad.api.options;

import me.codexadrian.tempad.api.power.PowerSettings;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

public interface FuelOption {

    /**
     * Determines whether the timedoor can be opened for the player with the given Tempad item stack.
     *
     * @param player the player trying to open the timedoor
     * @return true if the timedoor can be opened, false otherwise
     */
    boolean canTimedoorOpen(Object dataHolder, PowerSettings attachment, Player player);

    /**
     * Called when a timedoor is opened by a player using a Tempad item stack.
     *
     * @param player the player who is opening the timedoor
     */
    void onTimedoorOpen(Object dataHolder, PowerSettings attachment, Player player);

    /**
     * Adds tooltip information to the provided item stack when rendering a GUI.
     *
     * @param level      the level the item stack is in
     * @param components the list of tooltip components
     * @param flag       the tooltip flag
     */
    default void addToolTip(Object dataHolder, PowerSettings attachment, Level level, List<Component> components, TooltipFlag flag) {}

    /**
     * Determines whether the durability bar should be visible for the given item stack.
     *
     * @return true if the durability bar should be visible, false otherwise
     */
    default boolean isDurabilityBarVisible(Object dataHolder, PowerSettings attachment) {
        return false;
    }

    /**
     * Returns the percentage value of the given item stack.
     *
     * @return the percentage value of the item stack
     */
    default double getPercentage(Object dataHolder, PowerSettings attachment) {
        return 1;
    }

    /**
     * Performs a tick operation on the given ItemStack and Entity.
     * This method is called every tick while the ItemStack is in the player's inventory or Curio/Trinkets slot.
     *
     * @param entity the Entity being ticked
     */
    default void tick(Object dataHolder, PowerSettings attachment, Entity entity) {};
}
