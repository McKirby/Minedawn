import net.reindiegames.re2d.core.game.DayNightCircle;
import org.junit.Assert;
import org.junit.Test;

public class DayNightCircleTest {
    @Test
    public void testPeriods() {
        final DayNightCircle circle = new DayNightCircle();

        for (int day = 0; day < 100; day++) {
            for (int hour = 0; hour < 24; hour++) {
                for (int minute = 0; minute < 60; minute++) {
                    circle.setTime(day, hour, minute);

                    Assert.assertEquals(circle.getDays(), day);
                    Assert.assertEquals(circle.getHour(), hour);
                    Assert.assertEquals(circle.getMinute(), minute);
                }
            }
        }

        circle.setTime(4, 25, 70);
        Assert.assertEquals(circle.getDays(), 5);
        Assert.assertEquals(circle.getHour(), 2);
        Assert.assertEquals(circle.getMinute(), 10);

        circle.setTime(4, 24, 00);
        Assert.assertEquals(circle.getDays(), 5);
        Assert.assertEquals(circle.getHour(), 0);
        Assert.assertEquals(circle.getMinute(), 0);
    }
}
