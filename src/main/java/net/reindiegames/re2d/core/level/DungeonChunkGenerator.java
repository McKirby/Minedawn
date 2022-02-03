package net.reindiegames.re2d.core.level;

import net.reindiegames.re2d.core.Log;
import org.joml.Vector2f;
import org.joml.Vector2i;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static net.reindiegames.re2d.core.level.Chunk.CHUNK_SIZE;

public class DungeonChunkGenerator implements ChunkGenerator {
    public static final short RED = 0;
    public static final short YELLOW = 1;
    public static final short GREEN = 2;
    public static final short BLUE = 3;
    public static final short ORANGE = 4;
    public static final short GRAY = 5;
    public static final short PINK = 6;

    public static int minRoomCount = 4;
    public static int maxRoomCount = 8;
    public static int minRoomSize = 3;
    public static int maxRoomSize = 9;
    public static float featuresPerChunk = 1.0f / 2.5f;

    public static final byte WALL = 0;
    public static final byte ROOM = 1;
    public static final byte FEATURE = 2;
    public static final byte PATH = 3;
    public static final byte DEAD_END = 6;

    public final long seed;
    public final int width;
    public final int height;
    public final int scale;
    public final int chunkWidth;
    public final int chunkHeight;

    protected final Vector2i spawn;

    private final Random random;
    private final DungeonTile[][] tiles;
    private final DungeonTile[] streamArray;

    private final int rooms;
    private int paths;

    public DungeonChunkGenerator(int w, int h, int scale) {
        this((long) (Math.random() * Integer.MAX_VALUE), w, h, scale);
    }

    public DungeonChunkGenerator(long seed, int w, int h, int scale) {
        Log.info("Generating Dungeon with Seed '" + seed + "'...");
        if (w <= maxRoomSize * 2)
            throw new IllegalArgumentException("The Width has to be at Minimum the double Room Size!");
        if (h <= maxRoomSize * 2)
            throw new IllegalArgumentException("The Height has to be at Minimum the double Room Size!");

        this.seed = seed;
        this.random = new Random(seed);

        this.width = w + (1 - w % 2);
        this.height = h + (1 - h % 2);
        this.scale = scale;
        this.chunkWidth = (width * scale) / CHUNK_SIZE + (width % CHUNK_SIZE != 0 ? 1 : 0);
        this.chunkHeight = (height * scale) / CHUNK_SIZE + (height % CHUNK_SIZE != 0 ? 1 : 0);

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

        this.rooms = minRoomSize + random.nextInt(maxRoomCount - minRoomCount);
        this.paths = 0;

        //Generate Structures
        this.generateMaze();
        this.generateRooms();
        this.characterizePaths();

        //Place Features
        int l = (int) (chunkHeight * chunkWidth * featuresPerChunk);
        this.replace(l / 2, this.stream().filter(t -> t.type == DEAD_END), FEATURE, false);
        this.replace(l, this.stream().filter(t -> t.type == ROOM).filter(t -> t.scan(ROOM) == 0b111111111), FEATURE, false);

        //Remove DeadEnds and homogenize Paths
        this.replaceAll(DEAD_END, WALL, true);
        this.stream().filter(t -> t.type >= PATH).forEach(t -> {
            if (t.between(ROOM, WALL).count() != 0) {
                t.setWall();
            }
        });
        this.replaceAll(DEAD_END, WALL, true);

        this.stream().filter(t -> t.type == WALL).forEach(t -> {
            t.between(ROOM, ROOM).filter(n -> n.type == ROOM).forEach(n -> t.setRoom(n.room));
        });
        this.stream().filter(t -> t.type >= PATH).forEach(t -> {
            t.between(ROOM, ROOM).filter(n -> n.type == ROOM).forEach(n -> t.setRoom(n.room));
        });

        //Remove Paths that only connect the same Room and no Features
        Set<Integer> connections = new HashSet<>();
        for (int path = 0; path < paths; path++) {
            connections.clear();

            this.pathStream(path).forEach(t -> {
                t.neighbours(false).filter(n -> n.type == ROOM || n.type == FEATURE).forEach(n -> {
                    if (n.type == FEATURE) {
                        connections.add(rooms + (n.y * width + n.x));
                    } else {
                        connections.add(n.room);
                    }
                });
            });

            if (connections.size() <= 1) {
                this.pathStream(path).forEach(DungeonTile::setWall);
            }
        }

        this.stream().filter(t -> t.type == WALL && t.neighbours(true).noneMatch(n -> n.type == ROOM)).forEach(t -> {
            t.between(PATH, PATH).forEach(n -> t.setPath(n.path));
        });
        this.characterizePaths();

        final List<DungeonTile> features = this.stream().filter(t -> t.type == PATH).collect(Collectors.toList());
        final DungeonTile spawnTile = features.get(random.nextInt(features.size()));
        this.spawn = new Vector2i(spawnTile.x * scale, spawnTile.y * scale);
    }

    public Stream<DungeonTile> stream() {
        return Stream.of(streamArray).filter(t -> !this.isBorderOrBeyond(t.x, t.y));
    }

    private List<DungeonTile> getAllTiles(int type) {
        return this.stream().filter(t -> t.type == type).collect(Collectors.toList());
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
        int roomWidth, roomHeight;
        int sx, sy, x, y;
        for (int room = 0; room < rooms; room++) {
            roomWidth = minRoomSize + random.nextInt(maxRoomSize - minRoomSize);
            roomHeight = minRoomSize + random.nextInt(maxRoomSize - minRoomSize);

            sx = 1 + random.nextInt((width - 1) - roomWidth);
            sy = 1 + random.nextInt((height - 1) - roomHeight);

            for (x = sx; x < (sx + roomWidth); x++) {
                for (y = sy; y < (sy + roomHeight); y++) {
                    tiles[x][y].type = ROOM;
                    tiles[x][y].room = room;
                }
            }
        }
    }

    private void characterizePaths() {
        int nm = 0b000101010;
        int em = 0b010100010;
        int sm = 0b010101000;
        int wm = 0b010001010;

        this.stream().filter(t -> t.type >= PATH).forEach(tile -> {
            tile.path = -1;

            int mask = tile.scan(WALL);
            if (mask < 0) return;

            if ((mask & nm) == nm || (mask & em) == em || (mask & sm) == sm || (mask & wm) == wm) {
                tile.type = DEAD_END;
            } else {
                tile.type = PATH;
            }
        });

        //Create Paths
        paths = 0;
        this.stream().filter(t -> t.type >= PATH).forEach(t -> {
            if (this.spreadPath(t, paths)) {
                paths++;
            }
        });
    }

    private boolean spreadPath(DungeonTile start, int path) {
        if (start.path >= 0) return false;
        start.path = path;

        start.neighbours(false).filter(n -> n.type >= PATH).forEach(n -> {
            this.spreadPath(n, path);
        });
        return true;
    }

    private Stream<DungeonTile> pathStream(int path) {
        return this.stream().filter(t -> t.type >= PATH && t.path == path);
    }

    private void replace(int count, Stream<DungeonTile> stream, byte newType, boolean overwrite) {
        final List<DungeonTile> found = stream.collect(Collectors.toList());

        int left = count;
        DungeonTile tile;
        while (!found.isEmpty() && left > 0) {
            tile = found.remove(random.nextInt(found.size()));
            tile.type = newType;
            left--;

            if (overwrite) {
                tile.room = -1;
                tile.path = -1;
            }
        }
    }

    private void replaceAll(byte type, byte newType, boolean overwrite) {
        int found;
        do {
            this.characterizePaths();

            found = 0;
            for (DungeonTile tile : this.getAllTiles(type)) {
                tile.type = newType;
                found++;

                if (overwrite) {
                    tile.path = -1;
                    tile.room = -1;
                }
            }
        } while (found > 0);
    }

    @Override
    public void populate(Chunk chunk, int[][] tiles, short[][] variants) {
        Vector2f levelPos;
        int tx, ty, x, y;
        for (byte rx = 0; rx < CHUNK_SIZE; rx++) {
            for (byte ry = 0; ry < CHUNK_SIZE; ry++) {
                levelPos = CoordinateSystems.chunkRelativeToLevel(chunk.cx, chunk.cy, rx, ry);
                tx = (int) levelPos.x;
                ty = (int) levelPos.y;
                x = tx / scale;
                y = ty / scale;

                if (this.isBeyond(x, y)) continue;
                DungeonTile tile = this.tiles[x][y];

                TileType type;
                short variant;
                switch (tile.type) {
                    case WALL -> {
                        type = TileType.STONE_WALL;
                        variant = TileType.STONE_WALL.defaultVariant;
                    }
                    default -> {
                        type = TileType.COBBLESTONE;
                        variant = TileType.COBBLESTONE.defaultVariant;
                    }
                }

                tiles[rx][ry] = type.id;
                variants[rx][ry] = variant;
            }
        }
    }

    @Override
    public Vector2i getSpawn() {
        return spawn;
    }

    @Override
    public void initialize(GeneratedLevel generatedLevel) {
        for (int cx = 0; cx < chunkWidth; cx++) {
            for (int cy = 0; cy < chunkHeight; cy++) {
                generatedLevel.getChunkBase().getChunk(cx, cy, true, true);
            }
        }
    }

    public class DungeonTile {
        public final int x;
        public final int y;
        int room = 0;
        int path = 0;
        private byte type;

        DungeonTile(int x, int y, byte type) {
            this.x = x;
            this.y = y;
            this.type = type;
            this.room = -1;
            this.path = -1;
        }

        public void setWall() {
            this.type = WALL;
            this.room = -1;
            this.path = -1;
        }

        public byte getType() {
            return type;
        }

        public int getRoom() {
            return room;
        }

        private void setRoom(int room) {
            this.type = ROOM;
            this.room = room;
            this.path = -1;
        }

        public int getPath() {
            return path;
        }

        private void setPath(int path) {
            this.type = PATH;
            this.room = -1;
            this.path = path;
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

        Stream<DungeonTile> between(byte t1, byte t2) {
            if (DungeonChunkGenerator.this.isBorder(x, y)) return Stream.of();

            byte left = tiles[x - 1][y].type;
            byte right = tiles[x + 1][y].type;
            if ((left == t1 && right == t2) || (left == t2 && right == t1)) {
                return Stream.of(tiles[x - 1][y], tiles[x + 1][y]);
            }

            byte top = tiles[x][y + 1].type;
            byte down = tiles[x][y - 1].type;
            if ((top == t1 && down == t2) || (top == t2 && down == t1)) {
                return Stream.of(tiles[x][y + 1], tiles[x][y - 1]);
            } else {
                return Stream.of();
            }
        }

        Stream<DungeonTile> neighbours(boolean nine) {
            if (DungeonChunkGenerator.this.isBorder(x, y)) return Stream.of();

            DungeonTile[] dungeonTiles = null;
            if (nine) {
                dungeonTiles = new DungeonTile[] {
                        tiles[x - 1][y + 1],
                        tiles[x + 0][y + 1],
                        tiles[x + 1][y + 1],

                        tiles[x - 1][y + 0],
                        tiles[x + 1][y + 0],

                        tiles[x - 1][y - 1],
                        tiles[x + 0][y - 1],
                        tiles[x + 1][y - 1]
                };
            } else {
                dungeonTiles = new DungeonTile[] {
                        tiles[x + 0][y - 1],
                        tiles[x + 1][y + 0],
                        tiles[x + 0][y + 1],
                        tiles[x - 1][y + 0],
                };
            }

            return Stream.of(dungeonTiles);
        }
    }
}
