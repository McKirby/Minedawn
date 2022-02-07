package net.reindiegames.re2d.core.level;

import net.reindiegames.re2d.core.Tickable;

public class TileEntity extends Tile implements Tickable {
    public final int tileEntityId;

    protected TileEntity(Level level, Chunk chunk, int tx, int ty, TileType type) {
        super(level, chunk, tx, ty, type);
        this.tileEntityId = level.getChunkBase().nextTileEntityId();
        level.getChunkBase().addTileEntity(this);
    }

    @Override
    public void syncTick(long totalTicks, float delta) {
    }

    @Override
    public final void asyncTick(long totalTicks, float delta) {
    }

    @Override
    public void destroy() {
        super.destroy();
        level.getChunkBase().removeTileEntity(this);
    }

    @Override
    public int hashCode() {
        return tileEntityId;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof TileEntity) {
            return ((TileEntity) o).tileEntityId == this.tileEntityId;
        } else {
            return false;
        }
    }
}
