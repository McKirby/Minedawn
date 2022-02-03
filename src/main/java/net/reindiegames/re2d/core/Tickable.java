package net.reindiegames.re2d.core;

public interface Tickable {
    public abstract void syncTick(long totalTicks, float delta);

    public abstract void asyncTick(long totalTicks, float delta);
}
