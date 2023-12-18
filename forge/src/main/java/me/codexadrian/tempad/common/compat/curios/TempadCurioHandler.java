package me.codexadrian.tempad.common.compat.curios;

import me.codexadrian.tempad.common.registry.TempadRegistry;
import top.theillusivec4.curios.api.CuriosApi;

public class TempadCurioHandler {

    public static void init() {
        CuriosApi.registerCurio(TempadRegistry.TEMPAD.get(), new TempadCurio());
        CuriosApi.registerCurio(TempadRegistry.CREATIVE_TEMPAD.get(), new TempadCurio());
    }
}
