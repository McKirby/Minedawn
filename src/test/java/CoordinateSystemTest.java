import net.reindiegames.re2d.core.level.Chunk;
import net.reindiegames.re2d.core.level.CoordinateSystems;
import org.joml.Random;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.junit.Assert;
import org.junit.Test;

public class CoordinateSystemTest {
    @Test
    public void testLevelToChunk() {
        {
            final Vector2f levelPos1 = new Vector2f(0.0f, 0.0f);
            final Vector2f levelPos2 = new Vector2f(0.0f, -1.0f);
            final Vector2f levelPos3 = new Vector2f(-1.0f, 0.0f);
            final Vector2f levelPos4 = new Vector2f(-1.0f, -1.0f);

            Assert.assertEquals(CoordinateSystems.levelToChunk(levelPos1).x, 0);
            Assert.assertEquals(CoordinateSystems.levelToChunk(levelPos1).y, 0);

            Assert.assertEquals(CoordinateSystems.levelToChunk(levelPos2).x, 0);
            Assert.assertEquals(CoordinateSystems.levelToChunk(levelPos2).y, -1);

            Assert.assertEquals(CoordinateSystems.levelToChunk(levelPos3).x, -1);
            Assert.assertEquals(CoordinateSystems.levelToChunk(levelPos3).y, 0);

            Assert.assertEquals(CoordinateSystems.levelToChunk(levelPos4).x, -1);
            Assert.assertEquals(CoordinateSystems.levelToChunk(levelPos4).y, -1);
        }

        {
            final Vector2f levelPos1 = new Vector2f(0.0f, Chunk.CHUNK_SIZE - 1.0f);
            final Vector2f levelPos2 = new Vector2f(Chunk.CHUNK_SIZE - 1.0f, 0.0f);
            final Vector2f levelPos3 = new Vector2f(0.0f, Chunk.CHUNK_SIZE);
            final Vector2f levelPos4 = new Vector2f(Chunk.CHUNK_SIZE, 0.0f);

            Assert.assertEquals(CoordinateSystems.levelToChunk(levelPos1).x, 0);
            Assert.assertEquals(CoordinateSystems.levelToChunk(levelPos1).y, 0);

            Assert.assertEquals(CoordinateSystems.levelToChunk(levelPos2).x, 0);
            Assert.assertEquals(CoordinateSystems.levelToChunk(levelPos2).y, 0);

            Assert.assertEquals(CoordinateSystems.levelToChunk(levelPos3).x, 0);
            Assert.assertEquals(CoordinateSystems.levelToChunk(levelPos3).y, 1);

            Assert.assertEquals(CoordinateSystems.levelToChunk(levelPos4).x, 1);
            Assert.assertEquals(CoordinateSystems.levelToChunk(levelPos4).y, 0);
        }

        {
            final Vector2f levelPos1 = new Vector2f(0.0f, -Chunk.CHUNK_SIZE - 1.0f);
            final Vector2f levelPos2 = new Vector2f(-Chunk.CHUNK_SIZE - 1.0f, 0.0f);
            final Vector2f levelPos3 = new Vector2f(0.0f, -Chunk.CHUNK_SIZE);
            final Vector2f levelPos4 = new Vector2f(-Chunk.CHUNK_SIZE, 0.0f);

            Assert.assertEquals(CoordinateSystems.levelToChunk(levelPos1).x, 0);
            Assert.assertEquals(CoordinateSystems.levelToChunk(levelPos1).y, -2);

            Assert.assertEquals(CoordinateSystems.levelToChunk(levelPos2).x, -2);
            Assert.assertEquals(CoordinateSystems.levelToChunk(levelPos2).y, 0);

            Assert.assertEquals(CoordinateSystems.levelToChunk(levelPos3).x, 0);
            Assert.assertEquals(CoordinateSystems.levelToChunk(levelPos3).y, -1);

            Assert.assertEquals(CoordinateSystems.levelToChunk(levelPos4).x, -1);
            Assert.assertEquals(CoordinateSystems.levelToChunk(levelPos4).y, 0);
        }

        {
            final Vector2f levelPos1 = new Vector2f(0.0f, -16.1f);
            final Vector2f levelPos2 = new Vector2f(0.0f, -16.0f);
            final Vector2f levelPos3 = new Vector2f(0.0f, 16.0f);
            final Vector2f levelPos4 = new Vector2f(0.0f, 15.9f);

            Assert.assertEquals(CoordinateSystems.levelToChunk(levelPos1).x, 0);
            Assert.assertEquals(CoordinateSystems.levelToChunk(levelPos1).y, -2);

            Assert.assertEquals(CoordinateSystems.levelToChunk(levelPos2).x, 0);
            Assert.assertEquals(CoordinateSystems.levelToChunk(levelPos2).y, -1);

            Assert.assertEquals(CoordinateSystems.levelToChunk(levelPos3).x, 0);
            Assert.assertEquals(CoordinateSystems.levelToChunk(levelPos3).y, 1);

            Assert.assertEquals(CoordinateSystems.levelToChunk(levelPos4).x, 0);
            Assert.assertEquals(CoordinateSystems.levelToChunk(levelPos4).y, 0);
        }

        final Random random = new Random();
        for (int i = 0; i < 1000 * 1000; i++) {
            final Vector2f levelPos = new Vector2f();
            levelPos.x = (int) (random.nextFloat() * (Chunk.CHUNK_SIZE * 10) * (random.nextFloat() - 0.5f));
            levelPos.y = (int) (random.nextFloat() * (Chunk.CHUNK_SIZE * 10) * (random.nextFloat() - 0.5f));

            final Vector2i chunk = CoordinateSystems.levelToChunk(levelPos);
            final Vector2i relative = CoordinateSystems.levelToChunkRelative(levelPos);
            final Vector2f restore = CoordinateSystems.chunkRelativeToLevel(chunk, relative);

            Assert.assertEquals((int) levelPos.x, (int) restore.x);
            Assert.assertEquals((int) levelPos.y, (int) restore.y);
        }
    }
}
