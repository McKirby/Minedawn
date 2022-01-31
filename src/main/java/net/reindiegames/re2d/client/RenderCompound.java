package net.reindiegames.re2d.client;

import net.reindiegames.re2d.core.GameResource;

class RenderCompound {
    protected final GameResource source;

    protected TextureAtlas atlas;
    protected Mesh[][] sprites;
    protected AnimationParameters[] animation;

    RenderCompound(GameResource source) {
        this.source = source;
    }

    static class AnimationParameters {
        final int frames;
        final int ticks;
        final int duration;

        protected AnimationParameters(int frames, int ticks) {
            this.frames = frames;
            this.ticks = ticks;
            this.duration = frames * ticks;
        }
    }
}
