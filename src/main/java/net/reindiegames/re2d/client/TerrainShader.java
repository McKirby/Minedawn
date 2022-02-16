package net.reindiegames.re2d.client;

class TerrainShader extends LevelShader {
    protected static final String VERTEX_FILE = "net/reindiegames/re2d/client/terrain.vert";
    protected static final String FRAGMENT_FILE = "net/reindiegames/re2d/client/terrain.frag";

    protected TerrainShader() throws IllegalArgumentException {
        super(VERTEX_FILE, FRAGMENT_FILE);
    }
}
