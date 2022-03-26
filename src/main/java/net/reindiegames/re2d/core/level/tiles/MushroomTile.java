package net.reindiegames.re2d.core.level.tiles;

import net.reindiegames.re2d.core.CoreParameters;
import net.reindiegames.re2d.core.level.Chunk;
import net.reindiegames.re2d.core.level.Level;
import net.reindiegames.re2d.core.level.TileStack;
import net.reindiegames.re2d.core.level.TileType;

public class MushroomTile extends GrowableTile {
    protected MushroomTile(TileStack stack, Byte layer) {
        super(stack, layer, TileType.MUSHROOM, 5.0f, 60.0f);
    }
}
