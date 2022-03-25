package net.reindiegames.re2d.core.level.entity;

import net.reindiegames.re2d.core.level.Chunk;
import net.reindiegames.re2d.core.level.Level;
import net.reindiegames.re2d.core.level.Tile;
import org.joml.Vector2f;
import org.joml.Vector2i;

import java.util.*;

public class Navigator {
    public final EntitySentient entity;

    private final Map<Integer, Map<Integer, Node>> nodeMap;
    private final LinkedList<Vector2i> path;
    private int pathIndex;

    protected Navigator(EntitySentient entity) {
        this.entity = entity;
        this.nodeMap = new HashMap<>();
        this.path = new LinkedList<>();
    }

    public boolean isNavigating() {
        return path.size() != 0 && pathIndex < path.size();
    }

    protected float getProgress() {
        if (!this.isNavigating()) return 1.0f;
        return ((float) pathIndex) / ((float) path.size());
    }

    protected Vector2i nextWaypoint() {
        if (pathIndex >= path.size()) return null;
        return path.get(pathIndex);
    }

    protected boolean progressIndex() {
        pathIndex++;
        return pathIndex == path.size();
    }

    public void stopNavigation() {
        nodeMap.clear();
        path.clear();
        pathIndex = 0;
    }

    public boolean navigate(Vector2f goal) {
        return this.navigate(goal, -1);
    }

    public boolean navigate(Vector2f goal, int maxLength) {
        this.stopNavigation();

        final Level level = entity.level;
        final Vector2i start = entity.getCenterTilePosition();
        final PriorityQueue<Node> openList = new PriorityQueue<Node>();

        final Node startNode = this.getNode(level, (int) start.x, (int) start.y);
        if (startNode == null) return false;

        startNode.cost = 0.0f;
        openList.add(startNode);

        Node current;
        Node neighbour;
        while (!openList.isEmpty()) {
            current = openList.poll();

            if (current.x == ((int) goal.x) && current.y == ((int) goal.y)) {
                while (current != null) {
                    path.add(new Vector2i(current.x, current.y));
                    current = current.prev;
                }

                if (maxLength == -1 || path.size() <= maxLength) {
                    Collections.reverse(path);
                    return true;
                } else {
                    path.clear();
                    continue;
                }
            } else {
                for (int rx = -1; rx <= 1; rx++) {
                    for (int ry = -1; ry <= 1; ry++) {
                        if ((rx != 0) == (ry != 0)) continue;

                        neighbour = this.getNode(level, current.x + rx, current.y + ry);
                        if (neighbour == null) continue;

                        float transitionCost = 1.0f;
                        float newCost = current.cost + transitionCost;

                        if (newCost < neighbour.cost) {
                            neighbour.cost = newCost;
                            neighbour.prev = current;

                            openList.add(neighbour);
                        }
                    }
                }
            }
        }
        return false;
    }

    private Node getNode(Level level, int x, int y) {
        final Tile[] tile = level.getTiles(new Vector2f(x, y));
        for (byte layer = 0; layer < Chunk.CHUNK_LAYERS; layer++) {
            if (tile[layer] == null || tile[layer].type.isSolid()) return null;
        }

        Map<Integer, Node> xMap = nodeMap.computeIfAbsent(x, key -> new HashMap<>());
        return xMap.computeIfAbsent(y, key -> new Node(x, y));
    }

    private static class Node implements Comparable<Node> {
        int x;
        int y;
        float cost;
        Node prev;

        private Node(int x, int y) {
            this(x, y, Float.POSITIVE_INFINITY);
        }

        private Node(int x, int y, float cost) {
            this.x = x;
            this.y = y;
            this.cost = cost;
        }

        @Override
        public int compareTo(Node o) {
            return Float.compare(cost, o.cost);
        }
    }
}
