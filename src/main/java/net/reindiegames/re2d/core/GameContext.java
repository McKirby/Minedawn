package net.reindiegames.re2d.core;

import net.reindiegames.re2d.client.ClientContext;
import net.reindiegames.re2d.core.level.ResourceLevel;
import net.reindiegames.re2d.core.level.TileType;
import net.reindiegames.re2d.core.level.entity.EntityType;
import net.reindiegames.re2d.core.util.Disposer;
import net.reindiegames.re2d.core.util.Initializer;
import net.reindiegames.re2d.core.util.ReflectionUtil;

import java.lang.reflect.Constructor;

import static net.reindiegames.re2d.core.CoreParameters.TICK_RATE;

public abstract class GameContext {
    protected GameContext() {
    }

    public static void main(String[] args) {
        Class<? extends GameContext> contextImpl = null;
        if (args.length == 0 || args[0].equalsIgnoreCase("client")) {
            contextImpl = ClientContext.class;
        } else {
            Log.error("There is not Context like '" + args[0] + "'!");
            return;
        }

        Log.info("Loading Libraries...");

        Log.info("Loading Core...");
        if (!GameResource.loadAll(TileType.class)) throw new IllegalArgumentException("Cannot load Tiles!");
        if (!GameResource.loadAll(EntityType.class)) throw new IllegalArgumentException("Cannot load Entities!");
        if (!GameResource.loadAll(ResourceLevel.class)) throw new IllegalArgumentException("Cannot load Levels!");

        try {
            Log.info("Initializing...");
            ReflectionUtil.invokeAnnotatedStatics(contextImpl, Initializer.class);

            Log.info("Finalizing Context...");
            Constructor constructor = contextImpl.getDeclaredConstructor();
            constructor.setAccessible(true);
            GameContext context = (GameContext) constructor.newInstance();
            context.start();
        } catch (Exception e) {
            Log.error("Failed to initialize Context (" + e.getMessage() + ")!");
            e.printStackTrace();
        }

        try {
            Log.info("Disposing...");
            ReflectionUtil.invokeAnnotatedStatics(contextImpl, Disposer.class);
        } catch (Exception e) {
            Log.error("Failed to dispose the Context properly (" + e.getMessage() + ")!");
            e.printStackTrace();
        }

        Log.info("Bye!");
    }

    protected abstract void syncTick(long totalTicks, float delta);

    protected abstract void asyncTick(long totalTicks, float delta);

    protected abstract boolean shouldClose();

    protected abstract String debugInfo(int syncTicks, int asyncTicks);

    private final void start() {
        Log.info("Starting...");

        final long nsPerTick = (1000 * 1000 * 1000) / TICK_RATE;
        long now;
        long last = System.nanoTime();
        long diff;
        float syncDelta = 0.0f;
        float asyncDelta = 0.0f;

        long lastDebug = System.currentTimeMillis();
        long totalTicks = 0L;
        int syncTicks = 0;
        int asyncTicks = 0;

        while (!this.shouldClose()) {
            now = System.nanoTime();
            diff = now - last;
            last = now;
            asyncDelta = ((float) diff) / nsPerTick;
            syncDelta += asyncDelta;

            while (syncDelta >= 1.0f) {
                this.syncTick(totalTicks, Math.min(syncDelta, 1.0f));
                syncDelta--;
                syncTicks++;
                totalTicks++;
            }
            this.asyncTick(totalTicks, asyncDelta);
            asyncTicks++;

            if (System.currentTimeMillis() - lastDebug >= 1000) {
                Log.info(this.debugInfo(syncTicks, asyncTicks));
                syncTicks = 0;
                asyncTicks = 0;
                lastDebug = System.currentTimeMillis();
            }
        }
    }
}
