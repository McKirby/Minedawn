package net.reindiegames.re2d.core;

import java.io.PrintStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Log {
    public static DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy-HH:mm:ss");
    public static boolean printThread = true;
    public static PrintStream out = System.out;
    public static PrintStream error = System.err;

    private static void print(PrintStream target, String prefix, String padding, String msg) {
        final String timePrefix = "[" + LocalDateTime.now().format(timeFormatter) + "]";
        final String threadPrefix = printThread ? ("[" + Thread.currentThread().getName() + "]") : ("");
        target.println(timePrefix + threadPrefix + "[" + prefix + "] " + padding + msg);
    }

    public static void debug(String msg) {
        Log.print(out, "Debug", "", msg);
    }

    public static void info(String msg) {
        Log.print(out, "Info", " ", msg);
    }

    public static void warn(String msg) {
        Log.print(error, "WARN", " ", msg);
    }

    public static void error(String msg) {
        Log.print(error, "ERROR", "", msg);
    }
}
