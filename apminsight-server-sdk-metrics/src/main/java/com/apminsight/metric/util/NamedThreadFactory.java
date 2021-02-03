package com.apminsight.metric.util;

import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;

/**
 * NamedThreadFactory
 *
 * @author wangzhe
 */
public class NamedThreadFactory implements ThreadFactory {
    private String nameFormat;

    private final AtomicLong count = new AtomicLong(0);

    public NamedThreadFactory(String nameFormat) {

        this.nameFormat = nameFormat;
    }

    @Override
    public Thread newThread(Runnable runnable) {
        Thread thread = Executors.defaultThreadFactory().newThread(runnable);
        if (nameFormat != null) {
            thread.setName(format(nameFormat, count.getAndIncrement()));
        }

        thread.setDaemon(true);
        return thread;
    }

    private static String format(String format, Object... args) {
        return String.format(Locale.ROOT, format, args);
    }
}
