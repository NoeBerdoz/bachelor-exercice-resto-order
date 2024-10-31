package ch.hearc.ig.orderresto.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class SimpleLogger {

    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_MAGENTA = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";
    public static final String ANSI_RESET = "\u001B[0m";

    public enum LogLevel {
        INFO, WARNING, ERROR
    }

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private static void log(LogLevel level, String message) {
        String timestamp = LocalDateTime.now().format(DATE_FORMAT);
        String formattedMessage = String.format("%s [%s] %s", timestamp, level, message);

        switch (level) {
            case INFO:
                System.out.println(ANSI_GREEN + formattedMessage + ANSI_RESET);
                break;
            case WARNING:
                System.out.println(ANSI_YELLOW + formattedMessage + ANSI_RESET);
                break;
            case ERROR:
                System.out.println(ANSI_RED + formattedMessage + ANSI_RESET);
                break;
            default:
                System.out.println(formattedMessage);
        }
    }

    public static void info(String message) {
        log(LogLevel.INFO, message);
    }

    public static void warning(String message) {
        log(LogLevel.WARNING, message);
    }

    public static void error(String message) {
        log(LogLevel.ERROR, message);
    }
}
