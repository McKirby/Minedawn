package net.reindiegames.re2d.core.level;

import org.joml.Vector2f;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Stack;

import static net.reindiegames.re2d.core.level.Chunk.CHUNK_SIZE;

public class DungeonChunkGenerator implements ChunkGenerator {
    public final int seed;
    public final int chunkWidth;
    public final int tileWidth;
    public final int chunkHeight;
    public final int tileHeight;
    private final Random random;
    private final int[][] tiles;

    public DungeonChunkGenerator(int seed, int width, int height) {
        this.seed = seed;
        this.random = new Random(seed);

        this.tileWidth = width + (1 - width % 2);
        this.tileHeight = height + (1 - height % 2);

        this.chunkWidth = tileWidth / CHUNK_SIZE + 1;
        this.chunkHeight = tileHeight / CHUNK_SIZE + 1;

        this.tiles = new int[tileWidth][tileHeight];
        this.generateMaze();
    }

    private boolean isBoarder(int tx, int ty) {
        return tx == 0 || tx == tileWidth - 1 || ty == 0 || ty == tileHeight - 1;
    }

    private boolean isOut(int tx, int ty) {
        return tx < 0 || tx > tileWidth - 1 || ty < 0 || ty > tileHeight - 1;
    }

    private boolean isBoarderOrBeyond(int tx, int ty) {
        return this.isBoarder(tx, ty) || this.isOut(tx, ty);
    }

    public void generateMaze() {
        for (int tx = 0; tx < tileWidth; tx++) {
            for (int ty = 0; ty < tileHeight; ty++) {
                tiles[tx][ty] = TileType.WATER.id;
            }
        }

        int startX = 1 + random.nextInt(tileWidth - 1);
        startX += (1 - startX % 2);
        int startY = 1 + random.nextInt(tileHeight - 1);
        startY += (1 - startY % 2);

        final Stack<Integer> tileStack = new Stack<Integer>();
        final List<Integer> neighbours = new ArrayList<>(8);
        tileStack.push(startY * tileWidth + startX);

        while (!tileStack.empty()) {
            int t = tileStack.pop();
            int tx = t % tileWidth;
            int ty = t / tileWidth;
            tiles[tx][ty] = TileType.GRASS.id;

            int neighbour, nx, ny, dx, dy;
            neighbours.clear();
            for (int rx = -2; rx <= 2; rx += 2) {
                for (int ry = -2; ry <= 2; ry += 2) {
                    if (rx != 0 ^ ry != 0) {
                        nx = tx + rx;
                        ny = ty + ry;
                        if (this.isBoarderOrBeyond(nx, ny)) continue;
                        if (tiles[nx][ny] != TileType.WATER.id) continue;
                        neighbours.add(ny * tileWidth + nx);
                    }
                }
            }

            if (!neighbours.isEmpty()) {
                tileStack.push(ty * tileWidth + tx);

                neighbour = neighbours.get(random.nextInt(neighbours.size()));
                nx = neighbour % tileWidth;
                ny = neighbour / tileWidth;
                tiles[nx][ny] = TileType.GRASS.id;

                dx = nx - tx;
                if (dx == -2) tiles[tx - 1][ty] = TileType.GRASS.id;
                if (dx == 2) tiles[tx + 1][ty] = TileType.GRASS.id;

                dy = ny - ty;
                if (dy == -2) tiles[tx][ty - 1] = TileType.GRASS.id;
                if (dy == 2) tiles[tx][ty + 1] = TileType.GRASS.id;

                tileStack.push(neighbour);
            }
        }
    }

    @Override
    public void populate(Chunk chunk, int[][] tiles) {
        Vector2f levelPos;
        int tx, ty;
        for (byte rx = 0; rx < CHUNK_SIZE; rx++) {
            for (byte ry = 0; ry < CHUNK_SIZE; ry++) {
                levelPos = CoordinateSystems.chunkRelativeToLevel(chunk.cx, chunk.cy, rx, ry);
                tx = (int) levelPos.x;
                ty = (int) levelPos.y;
                if (this.isOut(tx, ty)) continue;

                tiles[rx][ry] = this.tiles[tx][ty];
            }
        }
    }

    @Override
    public void initialize(GeneratedLevel generatedLevel) {
        for (int cx = 0; cx < chunkWidth; cx++) {
            for (int cy = 0; cy < chunkHeight; cy++) {
                generatedLevel.getChunkBase().getChunk(cx, cy, true, true);
            }
        }
    }
}
