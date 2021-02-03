package com.apminsight.metric.client;

import java.io.Closeable;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * MetricClient
 *
 * @author wangzhe
 */
public interface MetricClient extends Closeable {

    /**
     * Usually used to measure the quantity
     * Default count value is 1
     * @param name the name of the metric
     */
    void emitCounter(String name);

    /**
     * Usually used to measure the quantity
     * @param name the name of the metric
     * @param value the value of the count
     */
    void emitCounter(String name, Number value);

    /**
     * Usually used to measure the quantity with tags
     * Default count value is 1
     * @param name the name of the metric
     * @param tags the tags of the metric
     */
    void emitCounter(String name, Map<String, String> tags);

    /**
     * Usually used to measure the quantity with tags
     * Default count value is 1
     * @param name the name of the metric
     * @param value the count of the metric
     * @param tags the tags of the metric
     */
    void emitCounter(String name, Number value, Map<String, String> tags);

    /**
     * Usually used to measure the code execute time duration
     * Default time unit is milliseconds
     * @param name the name of the metric
     * @param mills time in milliseconds
     */
    void emitTimer(String name, Number mills);

    /**
     * Usually used to measure the code execute time duration
     * @param name the name of the metric
     * @param value the time of the metric
     * @param timeUnit the time unit of the metric
     */
    void emitTimer(String name, Number value, TimeUnit timeUnit);

    /**
     * Usually used to measure the code execute time duration with tags
     * Default time unit is milliseconds
     * @param name the name of the metric
     * @param mills time in milliseconds
     * @param tags the tags of the metric
     */
    void emitTimer(String name, Number mills, Map<String, String> tags);

    /**
     * Usually used to measure the code execute time duration with tags
     * @param name the name of the metric
     * @param value the time of the metric
     * @param timeUnit the time unit of the metric
     * @param tags the tags of the metric
     */
    void emitTimer(String name, Number value, TimeUnit timeUnit, Map<String, String> tags);

    /**
     * Usually used to measure internal system status, such as queue length, etc.
     * @param name the name of the metric
     * @param value the gauge value of the metric
     */
    void emitGauge(String name, Number value);

    /**
     * Usually used to measure internal system status with tags, such as queue length, etc.
     * @param name the name of the metric
     * @param value the gauge value of the metric
     * @param tags the tags of the metric
     */
    void emitGauge(String name, Number value, Map<String, String> tags);

}
