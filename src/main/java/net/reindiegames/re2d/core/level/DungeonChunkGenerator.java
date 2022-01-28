package net.reindiegames.re2d.core.level;

import org.joml.Vector2f;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Stack;

import static net.reindiegames.re2d.core.level.Chunk.CHUNK_SIZE;

public class DungeonChunkGenerator implements ChunkGenerator {
    private static final byte WALL = 0;
    private static final byte PATH = 1;
    private static final byte CROSS = 2;
    private static final byte T_CROSS = 3;
    private static final byte DEAD_END = 4;
    private static final byte PATHWAY = 5;
    private static final byte ROOM = 6;

    public final int seed;
    public final int width;
    public final int height;

    public final int chunkWidth;
    public final int chunkHeight;

    public int minRoomCount = 5;
    public int maxRoomCount = 25;

    public int minRoomSize = 5;
    public int maxRoomSize = 15;

    private final Random random;
    private final byte[][] variants;

    public DungeonChunkGenerator(int seed, int w, int h) {
        this.seed = seed;
        this.random = new Random(seed);

        if (w <= maxRoomSize * 2)
            throw new IllegalArgumentException("The Width has to be at Minimum double Room Size!");
        if (h <= maxRoomSize * 2)
            throw new IllegalArgumentException("The Height has to be at Minimum double Room Size!");

        this.width = w + (1 - w % 2);
        this.height = h + (1 - h % 2);
        this.chunkWidth = width / CHUNK_SIZE + (width % CHUNK_SIZE != 0 ? 1 : 0);
        this.chunkHeight = height / CHUNK_SIZE + (height % CHUNK_SIZE != 0 ? 1 : 0);

        this.variants = new byte[width][height];

        this.generateMaze();
        this.generateRooms();
        this.characterize();

        this.replaceAll(DEAD_END, WALL);
        this.replaceRandom(20, PATHWAY, WALL);
        this.replaceAll(DEAD_END, WALL);
        this.characterize();
    }

    private boolean isBorder(int x, int y) {
        return x == 0 || x == width - 1 || y == 0 || y == height - 1;
    }

    private boolean isOut(int x, int y) {
        return x < 0 || x > width - 1 || y < 0 || y > height - 1;
    }

    private boolean isBoarderOrBeyond(int x, int y) {
        return this.isBorder(x, y) || this.isOut(x, y);
    }

    public void generateMaze() {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                variants[x][y] = WALL;
            }
        }

        int sx = 1 + random.nextInt(width - 1);
        sx += (1 - sx % 2);

        int sy = 1 + random.nextInt(height - 1);
        sy += (1 - sy % 2);

        final Stack<Integer> stack = new Stack<>();
        final List<Integer> neighbours = new ArrayList<>(8);
        stack.push(sy * width + sx);

        int current, x, y;
        int rx, ry, neighbour, nx, ny, dx, dy;
        while (!stack.empty()) {
            current = stack.pop();
            x = current % width;
            y = current / width;
            variants[x][y] = PATH;

            neighbours.clear();
            for (rx = -2; rx <= 2; rx += 2) {
                for (ry = -2; ry <= 2; ry += 2) {
                    if (rx != 0 ^ ry != 0) {
                        nx = x + rx;
                        ny = y + ry;
                        if (this.isBoarderOrBeyond(nx, ny)) continue;
                        if (variants[nx][ny] != WALL) continue;
                        neighbours.add(ny * width + nx);
                    }
                }
            }

            if (!neighbours.isEmpty()) {
                stack.push(y * width + x);

                neighbour = neighbours.get(random.nextInt(neighbours.size()));
                nx = neighbour % width;
                ny = neighbour / width;
                variants[nx][ny] = PATH;

                dx = nx - x;
                dy = ny - y;
                if (dx == -2) variants[x - 1][y] = PATH;
                if (dx == 2) variants[x + 1][y] = PATH;
                if (dy == -2) variants[x][y - 1] = PATH;
                if (dy == 2) variants[x][y + 1] = PATH;

                stack.push(neighbour);
            }
        }
    }

    public void generateRooms() {
        int count = minRoomSize + random.nextInt(maxRoomCount - minRoomCount);

        int roomWidth, roomHeight;
        int sx, sy, x, y;
        for (int room = 0; room < count; room++) {
            roomWidth = minRoomSize + random.nextInt(maxRoomSize - minRoomSize);
            roomHeight = minRoomSize + random.nextInt(maxRoomSize - minRoomSize);

            sx = 1 + random.nextInt((width - 1) - roomWidth);
            sy = 1 + random.nextInt((height - 1) - roomHeight);

            for (x = sx; x < (sx + roomWidth); x++) {
                for (y = sy; y < (sy + roomHeight); y++) {
                    variants[x][y] = ROOM;
                }
            }
        }
    }

    public void characterize() {
        int t, e, s, w;
        int mask;

        int i = 0;
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (this.isBorder(x, y) || variants[x][y] == WALL) {
                    variants[x][y] = WALL;
                    continue;
                }

                mask = 0b000000000;
                mask |= (variants[x - 1][y + 1] == WALL ? 0b100000000 : 0b000000000);
                mask |= (variants[x + 0][y + 1] == WALL ? 0b010000000 : 0b000000000);
                mask |= (variants[x + 1][y + 1] == WALL ? 0b001000000 : 0b000000000);

                mask |= (variants[x - 1][y + 0] == WALL ? 0b000100000 : 0b000000000);
                mask |= (variants[x + 0][y + 0] == WALL ? 0b000010000 : 0b000000000);
                mask |= (variants[x + 1][y + 0] == WALL ? 0b000001000 : 0b000000000);

                mask |= (variants[x - 1][y - 1] == WALL ? 0b000000100 : 0b000000000);
                mask |= (variants[x + 0][y - 1] == WALL ? 0b000000010 : 0b000000000);
                mask |= (variants[x + 1][y - 1] == WALL ? 0b000000001 : 0b000000000);

                int nm = 0b000101010;
                int em = 0b010100010;
                int sm = 0b010101000;
                int wm = 0b010001010;
                if ((mask & nm) == nm || (mask & em) == em || (mask & sm) == sm || (mask & wm) == wm) {
                    variants[x][y] = DEAD_END;
                    continue;
                }

                switch (mask) {
                    case 0b101000101 -> variants[x][y] = CROSS;
                    case 0b111000101, 0b101001101, 0b101000111, 0b101100101 -> variants[x][y] = T_CROSS;
                    case 0b111000111, 0b101101101 -> variants[x][y] = PATHWAY;
                    default -> variants[x][y] = PATH;
                }
            }
        }
    }

    private void replaceRandom(int count, byte type, byte newType) {
        final List<Integer> found = new ArrayList<>(width * height);

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (variants[x][y] == type) {
                    found.add(y * width + x);
                }
            }
        }

        int left = count;
        while (!found.isEmpty() && left > 0) {
            int current = found.remove(random.nextInt(found.size()));
            int x = current % width;
            int y = current / width;

            variants[x][y] = newType;
            left--;
        }
    }

    private void replaceAll(byte variant, byte newVariant) {
        int found;
        do {
            this.characterize();

            found = 0;
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    if (variants[x][y] == variant) {
                        variants[x][y] = newVariant;
                        found++;
                    }
                }
            }
        } while (found > 0);
    }

    @Override
    public void populate(Chunk chunk, int[][] tiles) {
        Vector2f levelPos;
        int x, y;
        for (byte rx = 0; rx < CHUNK_SIZE; rx++) {
            for (byte ry = 0; ry < CHUNK_SIZE; ry++) {
                levelPos = CoordinateSystems.chunkRelativeToLevel(chunk.cx, chunk.cy, rx, ry);
                x = (int) levelPos.x;
                y = (int) levelPos.y;
                if (this.isOut(x, y)) continue;

                tiles[rx][ry] = (variants[x][y] == WALL ? TileType.WATER : TileType.GRASS).id;
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
