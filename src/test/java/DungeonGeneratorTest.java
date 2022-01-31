import net.reindiegames.re2d.core.level.DungeonChunkGenerator;
import org.junit.Assert;
import org.junit.Test;

import static net.reindiegames.re2d.core.level.DungeonChunkGenerator.*;

public class DungeonGeneratorTest {
    @Test
    public void integrity() {
        for (int i = 0; i < 100; i++) {
            try {
                final DungeonChunkGenerator generator = new DungeonChunkGenerator(63, 63, 1);
                generator.stream().forEach(t -> {
                    switch (t.getType()) {
                        case WALL:
                            Assert.assertEquals(-1, t.getPath());
                            Assert.assertEquals(-1, t.getRoom());
                            break;

                        case ROOM:
                            Assert.assertEquals(-1, t.getPath());
                            Assert.assertNotEquals(-1, t.getRoom());
                            break;

                        case FEATURE:
                            Assert.assertTrue(t.getPath() != -1 ^ t.getRoom() != -1);
                            break;

                        case PATH:
                            Assert.assertNotEquals(-1, t.getPath());
                            Assert.assertEquals(-1, t.getRoom());
                            break;

                        case DEAD_END:
                            Assert.fail("There is a DeadEnd!");
                            break;

                        default:
                            Assert.fail("We should not be here ('" + t.getType() + "')!");
                            break;
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
