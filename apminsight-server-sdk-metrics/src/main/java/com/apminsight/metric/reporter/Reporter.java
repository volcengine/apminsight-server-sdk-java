package com.apminsight.metric.reporter;

import java.io.Closeable;

/**
 * Reporter
 *
 * report metric data
 *
 * @author wangzhe
 */
public interface Reporter<T>  extends Closeable {

    /**
     * start report
     */
    void start();

    /**
     * report entity of T
     * @param e entity
     */
    void report(T e);
}
