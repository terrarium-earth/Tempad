package me.codexadrian.tempad.common.compat.trinkets;

import dev.emi.trinkets.api.TrinketsApi;
import me.codexadrian.tempad.common.registry.TempadRegistry;

public class TempadTrinketHandler {
    public static void init() {
        TrinketsApi.registerTrinket(TempadRegistry.TEMPAD.get(), new TempadTrinket());
        TrinketsApi.registerTrinket(TempadRegistry.CREATIVE_TEMPAD.get(), new TempadTrinket());
    }
}
