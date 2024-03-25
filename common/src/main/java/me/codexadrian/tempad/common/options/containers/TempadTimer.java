package me.codexadrian.tempad.common.options.containers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class TempadTimer {
    public static final Codec<TempadTimer> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("time").forGetter(TempadTimer::getTime)
    ).apply(instance, TempadTimer::new));

    public int time;

    public TempadTimer() {
        this(0);
    }

    public TempadTimer(int time) {
        this.time = time;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public void tick() {
        if (time > 0) {
            time--;
        }
    }

    public boolean isFinished() {
        return time == 0;
    }
}
