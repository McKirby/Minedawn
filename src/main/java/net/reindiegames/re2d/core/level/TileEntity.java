package net.reindiegames.re2d.core.level;

import net.reindiegames.re2d.core.Tickable;

public class TileEntity extends Tile implements Tickable {
    public final int tileEntityId;

    protected TileEntity(TileStack stack, byte layer, TileType type) {
        super(stack, layer, type);
        this.tileEntityId = stack.level.getChunkBase().nextTileEntityId();
        stack.level.getChunkBase().addTileEntity(this);
    }

    @Override
    public void syncTick(float delta) {
    }

    @Override
    public final void asyncTick(float delta) {
    }

    @Override
    public void destroy() {
        super.destroy();
        stack.level.getChunkBase().removeTileEntity(this);
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
