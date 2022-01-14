package net.reindiegames.re2d.core.meta;

import net.reindiegames.re2d.client.ClientContext;
import net.reindiegames.util.Log;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public abstract class GameContext {
    public static final int TICK_RATE = 20;
    public static final String TITLE = "Re2D";
    public static boolean debug = false;

    protected GameContext() {
    }

    private static void invokeAnnotatedStatic(Class<?> clazz, Class<? extends Annotation> annotation) throws Exception {
        for (final Method method : clazz.getDeclaredMethods()) {
            if (method.isAnnotationPresent(annotation)) {
                method.setAccessible(true);
                method.invoke(null);
            }
        }
    }

    public static void main(String[] args) {
        Class<? extends GameContext> contextImpl = null;
        if (args.length == 0 || args[0].equalsIgnoreCase("client")) {
            contextImpl = ClientContext.class;
        } else {
            Log.error("There is not Context like '" + args[0] + "'!");
            return;
        }

        try {
            Log.info("Initializing...");
            GameContext.invokeAnnotatedStatic(contextImpl, Initializer.class);

            Constructor constructor = contextImpl.getDeclaredConstructor();
            constructor.setAccessible(true);
            GameContext context = (GameContext) constructor.newInstance();
            context.start();
        } catch (Exception e) {
            Log.error("Failed to initialize Context!");
            e.printStackTrace();
        }

        try {
            Log.info("Disposing...");
            GameContext.invokeAnnotatedStatic(contextImpl, Disposer.class);
        } catch (Exception e) {
            Log.error("Failed to dispose the Context properly!");
            e.printStackTrace();
            return;
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
