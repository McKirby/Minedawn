package net.reindiegames.re2d.client;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

import static net.reindiegames.re2d.client.ClientParameters.tilePixelSize;

class TerrainShader extends Shader {
    protected static final String VERTEX_FILE = "net/reindiegames/re2d/client/terrain.vert";
    protected static final String FRAGMENT_FILE = "net/reindiegames/re2d/client/terrain.frag";

    private int transformationLocation;
    private int viewLocation;
    private int projectionLocation;
    private int textureSamplerLocation;

    protected TerrainShader() throws IllegalArgumentException {
        super(VERTEX_FILE, FRAGMENT_FILE);
    }

    @Override
    protected void loadUniforms() {
        this.transformationLocation = super.getUniformLocation("transformation");
        this.viewLocation = super.getUniformLocation("view");
        this.projectionLocation = super.getUniformLocation("projection");
        this.textureSamplerLocation = super.getUniformLocation("texture_sampler");
    }

    protected void loadTransformation(Vector2f pos, float rotation, Vector2f scale) {
        final Matrix4f matrix = new Matrix4f();
        matrix.translate(pos.x, pos.y, 0.0f);
        matrix.rotate(rotation, new Vector3f(0.0f, 0.0f, 1.0f));
        matrix.scale(scale.x, scale.y, 0.0f);

        super.loadMatrix4f(transformationLocation, matrix);
    }

    protected void loadProjectionView(float ctx, float cty, float width, float height) {
        final Matrix4f projection = new Matrix4f();
        projection.ortho(0.0f, width, 0.0f, height, -1.0f, 1.0f);
        super.loadMatrix4f(projectionLocation, projection);

        final Matrix4f view = new Matrix4f();
        view.translate(width / 2.0f + ctx * tilePixelSize, height / 2.0f + cty * tilePixelSize, 0.0f);
        super.loadMatrix4f(viewLocation, view);
    }

    protected void loadTextureBank(int textureBank) {
        super.loadInteger(textureSamplerLocation, textureBank);
    }
}
