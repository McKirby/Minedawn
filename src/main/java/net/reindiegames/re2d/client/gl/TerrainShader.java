package net.reindiegames.re2d.client.gl;

public class TerrainShader extends Shader {
    public static final String VERTEX_FILE = "net/reindiegames/re2d/client/gl/terrain.vert";
    public static final String FRAGMENT_FILE = "net/reindiegames/re2d/client/gl/terrain.frag";

    public TerrainShader() throws IllegalArgumentException {
        super(VERTEX_FILE, FRAGMENT_FILE);
    }

    @Override
    public void loadUniforms() {
    }
}
