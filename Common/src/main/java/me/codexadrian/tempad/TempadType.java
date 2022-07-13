package me.codexadrian.tempad;

public enum TempadType {
    HE_WHO_REMAINS(Tempad.getTempadConfig().getHeWhoRemainsOptions()),
    NORMAL(Tempad.getTempadConfig().getTempadOptions());

    private TempadConfig.TempadOptionConfig optionConfig;

    TempadType(TempadConfig.TempadOptionConfig optionConfig) {
        this.optionConfig = optionConfig;
    }

    public TempadConfig.TempadOptionConfig getOptionConfig() {
        return optionConfig;
    }
}
