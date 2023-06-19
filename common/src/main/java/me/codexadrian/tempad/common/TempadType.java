package me.codexadrian.tempad.common;

public enum TempadType {
    HE_WHO_REMAINS(1000),
    NORMAL(100);

    public final int durability;
    private TempadType(int durability) {
        this.durability = durability;
    }
}
