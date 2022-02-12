package net.reindiegames.re2d.core;

public interface Tickable {
    public abstract void syncTick(float delta);

    public abstract void asyncTick(float delta);
}
