package net.reindiegames.re2d.core.util;

import net.reindiegames.re2d.core.CoreParameters;
import net.reindiegames.re2d.core.GameContext;

import static net.reindiegames.re2d.core.util.DayNightCircle.DailyPeriod.*;

public class DayNightCircle {
    public static final long TICKS_PER_MINUTE = CoreParameters.TICK_RATE;
    public static final long TICKS_PER_HOUR = TICKS_PER_MINUTE * 60;
    public static final long TICKS_PER_DAY = TICKS_PER_HOUR * 24;

    public long time;

    public DayNightCircle() {
        this.setTime(0, MORNING.startHour, 0);
    }

    public void tick() {
        time++;
    }

    public int getDays() {
        return (int) (time / TICKS_PER_DAY);
    }

    public int getHour() {
        return (int) ((time % TICKS_PER_DAY) / TICKS_PER_HOUR);
    }

    public int getMinute() {
        return (int) (((time % TICKS_PER_DAY) % TICKS_PER_HOUR) / TICKS_PER_MINUTE);
    }

    public String getTimeString() {
        final int day = this.getDays();
        final int hour = this.getHour();
        final int minute = this.getMinute();
        final DayNightCircle.DailyPeriod period = GameContext.dayNightCircle.getPeriod();

        final String clockString = ((hour > 9 ? "" : "0") + hour) + ":" + ((minute > 9 ? "" : "0") + minute);
        return "Day " + day + ", " + period.name + ", " + clockString;
    }

    public DailyPeriod getPeriod() {
        final int hour = this.getHour();

        if (hour >= DUSK.startHour && hour <= DUSK.endHour) return DUSK;
        if (hour >= MORNING.startHour && hour <= MORNING.endHour) return MORNING;
        if (hour >= AFTERNOON.startHour && hour <= AFTERNOON.endHour) return AFTERNOON;
        if (hour >= DAWN.startHour && hour <= DAWN.endHour) return DAWN;
        return NIGHT;
    }

    public void setTime(int day, int hour, int minute) {
        this.time = day * TICKS_PER_DAY + hour * TICKS_PER_HOUR + minute * TICKS_PER_MINUTE;
    }

    public enum DailyPeriod {
        DUSK("Dusk", 6, 7),
        MORNING("Morning", 8, 11),
        AFTERNOON("Afternoon", 12, 18),
        DAWN("Dawn", 19, 20),
        NIGHT("Night", 21, 5);

        public final String name;
        public final int startHour;
        public final int endHour;

        private DailyPeriod(String name, int startHour, int endHour) {
            this.name = name;
            this.startHour = startHour;
            this.endHour = endHour;
        }
    }
}
