package me.codexadrian.tempad.api.options;

import me.codexadrian.tempad.api.ApiHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.Collection;
import java.util.Map;

public interface FuelOptionsApi {
    FuelOptionsApi API = ApiHelper.load(FuelOptionsApi.class);

    /**
     * Registers a TempadOption with the given identifier.
     *
     * @param id     The resource location identifier for the TempadOption.
     * @param option The TempadOption to register.
     */
    void register(ResourceLocation id, FuelOption option);

    /**
     * Retrieves a TempadOption with the specified resource location identifier.
     *
     * @param id The resource location identifier of the TempadOption to retrieve.
     * @return The TempadOption with the specified resource location identifier.
     */
    FuelOption getOption(ResourceLocation id);

    /**
     * Retrieves the selected TempadOption for the given ItemStack.
     *
     * @param stack The ItemStack to retrieve the selected TempadOption from.
     * @return The selected TempadOption for the given ItemStack.
     */
    FuelOption getSelected(ItemStack stack);

    /**
     * Retrieves the resource location identifier of the selected TempadOption for the given ItemStack.
     *
     * @param stack The ItemStack to retrieve the selected TempadOption from.
     * @return The resource location identifier of the selected TempadOption.
     */
    ResourceLocation getSelectedId(ItemStack stack);


    /**
     * Retrieves the options available for fuel.
     *
     * @return A map of resource locations to FuelOptions.
     */
    Map<ResourceLocation, FuelOption> getOptions();

    /**
     * Retrieves the fuel cost for the given ItemStack.
     *
     * @param stack The ItemStack to retrieve the fuel cost from.
     * @return The fuel cost for the given ItemStack.
     */
    int getFuelCost(ItemStack stack);

    /**
     * Retrieves the fuel capacity for the given ItemStack.
     *
     * @param stack The ItemStack to retrieve the fuel capacity from.
     * @return The fuel capacity for the given ItemStack.
     */
    int getFuelCapacity(ItemStack stack);
}
