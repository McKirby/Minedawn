package net.reindiegames.re2d.core.game;

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
