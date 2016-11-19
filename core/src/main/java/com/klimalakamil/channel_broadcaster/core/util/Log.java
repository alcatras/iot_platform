package com.klimalakamil.channel_broadcaster.core.util;

import java.io.PrintStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by ekamkli on 2016-11-03.
 */
public enum Log {

    Verbose(0x3f),
    Info(0x1f),
    Debug(0xf),
    Warning(0x7),
    Error(0x3);

    private static List<LogStream> logStreams;
    private static DateFormat dateFormat;

    static {
        logStreams = new ArrayList<>();
        dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    }

    private int level;

    Log(int level) {
        this.level = level;
    }

    void l(String msg) {
        StringBuilder sb = new StringBuilder();

        sb
                .append('[')
                .append(dateFormat.format(new Date()))
                .append(']')

                .append(toString())
                .append(" : ")
                .append(msg);

        final String logMessage = sb.toString();

        logStreams.stream().filter(logStream -> (logStream.mode & level) >= 1).forEach(logStream -> {
            logStream.printStream.println(logMessage);
            logStream.printStream.flush();
        });
    }

    public int getLevel() {
        return level;
    }

    public static boolean addOutput(PrintStream printStream, int level) {
        return logStreams.add(new LogStream(printStream, level));
    }

    public static void setTimeZone(TimeZone timeZone) {
        dateFormat.setTimeZone(timeZone);
    }

    private static class LogStream {
        PrintStream printStream;
        int mode;

        LogStream(PrintStream printStream, int mode) {
            this.printStream = printStream;
            this.mode = mode;
        }
    }
}
