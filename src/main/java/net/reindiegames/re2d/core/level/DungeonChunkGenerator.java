package net.reindiegames.re2d.core.level;

import org.joml.Vector2f;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Stack;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static net.reindiegames.re2d.core.level.Chunk.CHUNK_SIZE;

public class DungeonChunkGenerator implements ChunkGenerator {
    public static int minRoomCount = 5;
    public static int maxRoomCount = 25;
    public static int minRoomSize = 5;
    public static int maxRoomSize = 15;

    private static final byte WALL = 0;
    private static final byte ROOM = 1;
    private static final byte PATH = 2;
    private static final byte CROSS = 3;
    private static final byte T_CROSS = 4;
    private static final byte DEAD_END = 5;

    public final long seed;
    public final int width;
    public final int height;
    public final int chunkWidth;
    public final int chunkHeight;

    private final Random random;
    private final DungeonTile[][] tiles;
    private final DungeonTile[] streamArray;

    public DungeonChunkGenerator(int w, int h) {
        this((long) (Math.random() * Integer.MAX_VALUE), w, h);
    }

    public DungeonChunkGenerator(long seed, int w, int h) {
        if (w <= maxRoomSize * 2)
            throw new IllegalArgumentException("The Width has to be at Minimum the double Room Size!");
        if (h <= maxRoomSize * 2)
            throw new IllegalArgumentException("The Height has to be at Minimum the double Room Size!");

        this.seed = seed;
        this.random = new Random(seed);

        this.width = w + (1 - w % 2);
        this.height = h + (1 - h % 2);
        this.chunkWidth = width / CHUNK_SIZE + (width % CHUNK_SIZE != 0 ? 1 : 0);
        this.chunkHeight = height / CHUNK_SIZE + (height % CHUNK_SIZE != 0 ? 1 : 0);

        this.tiles = new DungeonTile[width][height];
        this.streamArray = new DungeonTile[width * height];

        DungeonTile tile;
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                tile = new DungeonTile(x, y, WALL);
                tiles[x][y] = tile;
                streamArray[y * width + x] = tile;
            }
        }

        this.generateMaze();
        this.generateRooms();
        this.characterizePaths();

        this.replaceAll(DEAD_END, WALL);
    }

    private Stream<DungeonTile> stream() {
        return Stream.of(streamArray);
    }

    private Stream<DungeonTile> streamWithoutBorder() {
        return this.stream().filter(t -> !this.isBorderOrBeyond(t.x, t.y));
    }

    private List<DungeonTile> getAllTiles(int type) {
        return this.streamWithoutBorder().filter(t -> t.type == type).collect(Collectors.toList());
    }

    private boolean isBorder(int x, int y) {
        return x == 0 || x == width - 1 || y == 0 || y == height - 1;
    }

    private boolean isBeyond(int x, int y) {
        return x < 0 || x > width - 1 || y < 0 || y > height - 1;
    }

    private boolean isBorderOrBeyond(int x, int y) {
        return this.isBorder(x, y) || this.isBeyond(x, y);
    }

    private void generateMaze() {
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
            if (this.isBeyond(x, y)) continue;
            tiles[x][y].type = PATH;

            neighbours.clear();
            for (rx = -2; rx <= 2; rx += 2) {
                for (ry = -2; ry <= 2; ry += 2) {
                    if (rx != 0 ^ ry != 0) {
                        nx = x + rx;
                        ny = y + ry;

                        if (this.isBorderOrBeyond(nx, ny)) continue;
                        if (tiles[nx][ny].type != WALL) continue;
                        neighbours.add(ny * width + nx);
                    }
                }
            }

            if (!neighbours.isEmpty()) {
                stack.push(y * width + x);

                neighbour = neighbours.get(random.nextInt(neighbours.size()));
                nx = neighbour % width;
                ny = neighbour / width;
                tiles[nx][ny].type = PATH;

                dx = nx - x;
                dy = ny - y;
                if (dx == -2) tiles[x - 1][y].type = PATH;
                if (dx == 2) tiles[x + 1][y].type = PATH;
                if (dy == -2) tiles[x][y - 1].type = PATH;
                if (dy == 2) tiles[x][y + 1].type = PATH;

                stack.push(neighbour);
            }
        }
    }

    private void generateRooms() {
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
                    tiles[x][y].type = ROOM;
                }
            }
        }
    }

    private void characterizePaths() {
        int nm = 0b000101010;
        int em = 0b010100010;
        int sm = 0b010101000;
        int wm = 0b010001010;

        this.streamWithoutBorder().filter(t -> t.type >= PATH).forEach(tile -> {
            int mask = tile.scan(WALL);
            if (mask < 0) return;

            if ((mask & nm) == nm || (mask & em) == em || (mask & sm) == sm || (mask & wm) == wm) {
                tile.type = DEAD_END;
                return;
            }

            switch (mask) {
                case 0b101000101 -> tile.type = CROSS;
                case 0b111000101, 0b101001101, 0b101000111, 0b101100101 -> tile.type = T_CROSS;
                default -> tile.type = PATH;
            }
        });
    }

    private void replaceRandom(int count, byte type, byte newType) {
        final List<DungeonTile> found = this.getAllTiles(type);

        int left = count;
        while (!found.isEmpty() && left > 0) {
            found.remove(random.nextInt(found.size())).type = newType;
            left--;
        }
    }

    private void replaceAll(byte type, byte newType) {
        int found;
        do {
            this.characterizePaths();

            found = 0;
            for (DungeonTile tile : this.getAllTiles(type)) {
                tile.type = newType;
                found++;
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
                if (this.isBeyond(x, y)) continue;

                if (this.tiles[x][y].type == DEAD_END) continue;
                tiles[rx][ry] = (this.tiles[x][y].type == WALL ? TileType.WATER : TileType.GRASS).id;
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

    private class DungeonTile {
        final int x;
        final int y;
        byte type;

        DungeonTile(int x, int y, byte type) {
            this.x = x;
            this.y = y;
            this.type = type;
        }

        int scan(int type) {
            if (DungeonChunkGenerator.this.isBorder(x, y)) return -1;

            int mask = 0b000000000;
            mask |= (tiles[x - 1][y + 1].type == type ? 0b100000000 : 0b000000000);
            mask |= (tiles[x + 0][y + 1].type == type ? 0b010000000 : 0b000000000);
            mask |= (tiles[x + 1][y + 1].type == type ? 0b001000000 : 0b000000000);

            mask |= (tiles[x - 1][y + 0].type == type ? 0b000100000 : 0b000000000);
            mask |= (tiles[x + 0][y + 0].type == type ? 0b000010000 : 0b000000000);
            mask |= (tiles[x + 1][y + 0].type == type ? 0b000001000 : 0b000000000);

            mask |= (tiles[x - 1][y - 1].type == type ? 0b000000100 : 0b000000000);
            mask |= (tiles[x + 0][y - 1].type == type ? 0b000000010 : 0b000000000);
            mask |= (tiles[x + 1][y - 1].type == type ? 0b000000001 : 0b000000000);

            return mask;
        }
    }
}
