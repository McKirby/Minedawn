package net.reindiegames.re2d.core.level.tiles;

import net.reindiegames.re2d.core.CoreParameters;
import net.reindiegames.re2d.core.level.Chunk;
import net.reindiegames.re2d.core.level.Level;
import net.reindiegames.re2d.core.level.TileEntity;
import net.reindiegames.re2d.core.level.TileType;

import java.util.Random;

public class GrowableTile extends TileEntity {
    protected static final Random growRandom = new Random();

    protected long ticksExisted;
    protected long lastGrow;
    protected final long minGrowTicks;
    protected final long maxGrowTicks;

    private short growState;
    private final short maxGrowState;

    protected GrowableTile(Level level, Chunk chunk, int tx, int ty, TileType type, float minSec, float maxSec) {
        super(level, chunk, tx, ty, type);

        this.maxGrowState = (short) (type.getVariants() - 1);

        this.minGrowTicks = (long) (minSec * CoreParameters.TICK_RATE);
        this.maxGrowTicks = (long) (maxSec * CoreParameters.TICK_RATE);
        if (maxGrowTicks <= minGrowTicks)
            throw new IllegalArgumentException("MinGrowTicks has to be less than MaxGrowTicks!");

        this.growState = 0;
        this.ticksExisted = 0L;
        this.lastGrow = 0L;
    }

    public void grow() {
        if (this.isHarvestable()) return;

        this.growState = (short) Math.min(growState + 1, maxGrowState);
        lastGrow = ticksExisted;

        super.variant = (short) (super.type.getDefaultVariant() + growState);
        super.chunk.changed = true;
    }

    public boolean isHarvestable() {
        return growState == maxGrowState;
    }

    public short getGrowState() {
        return growState;
    }

    @Override
    public void syncTick(long totalTicks, float delta) {
        super.syncTick(totalTicks, delta);

        ticksExisted++;
        if (ticksExisted % CoreParameters.TICK_RATE != 0) return;

        long deltaGrowTicks = ticksExisted - lastGrow;
        if (deltaGrowTicks <= minGrowTicks) return;

        float ratio = ((float) (deltaGrowTicks - minGrowTicks)) / ((float) (maxGrowTicks - minGrowTicks));
        if (growRandom.nextFloat() <= ratio) {
            this.grow();
        }
    }
}
