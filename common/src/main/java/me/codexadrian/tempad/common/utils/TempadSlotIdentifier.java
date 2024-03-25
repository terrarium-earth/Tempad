package me.codexadrian.tempad.common.utils;

import earth.terrarium.baubly.common.SlotIdentifier;

public class TempadSlotIdentifier implements SlotIdentifier {
    public static final TempadSlotIdentifier INSTANCE = new TempadSlotIdentifier();

    @Override
    public String[] trinketIds() {
        return new String[]{"hand/glove", "offhand/glove"};
    }

    @Override
    public String curioId() {
        return "bracelet";
    }
}
