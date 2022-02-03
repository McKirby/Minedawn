package net.reindiegames.re2d.client;

import org.joml.Matrix4f;
import org.joml.Vector2f;

import static net.reindiegames.re2d.client.ClientParameters.tileScale;

class TerrainShader extends Shader {
    protected static final String VERTEX_FILE = "net/reindiegames/re2d/client/terrain.vert";
    protected static final String FRAGMENT_FILE = "net/reindiegames/re2d/client/terrain.frag";

    private int transformationLocation;
    private int viewLocation;
    private int projectionLocation;
    private int depthLocation;
    private int textureSamplerLocation;

    protected TerrainShader() throws IllegalArgumentException {
        super(VERTEX_FILE, FRAGMENT_FILE);
    }

    @Override
    protected void loadUniforms() {
        this.transformationLocation = super.getUniformLocation("transformation");
        this.viewLocation = super.getUniformLocation("view");
        this.projectionLocation = super.getUniformLocation("projection");
        this.depthLocation = super.getUniformLocation("depth");
        this.textureSamplerLocation = super.getUniformLocation("texture_sampler");
    }

    protected void loadDepth(float depth) {
        super.loadFloat(depthLocation, depth);
    }

    protected void loadTransformation(float[] transformation) {
        super.loadMatrixArray(transformationLocation, transformation);
    }

    protected void loadProjectionView(Vector2f c, float width, float height) {
        final Matrix4f projection = new Matrix4f();
        projection.ortho(0.0f, width, 0.0f, height, -1.0f, 1.0f);
        super.loadMatrix4f(projectionLocation, projection);

        final Matrix4f view = new Matrix4f();
        view.translate(width / 2.0f + -c.x * tileScale, height / 2.0f + -c.y * tileScale, 0.0f);
        super.loadMatrix4f(viewLocation, view);
    }

    protected void loadTextureBank(int textureBank) {
        super.loadInteger(textureSamplerLocation, textureBank);
    }
}
