package com.apminsight.metric.model;

import java.util.Map;

/**
 * Metrics
 *
 * @author wangzhe
 */
public interface Metric {

    /**
     * 获取指标类型
     * get type of this metric
     *
     * @return type of this metric
     * @see MetricType
     */
    MetricType getMetricType();

    String getName();

    Number getValue();

    Map<String, String> getTags();

    Metric reset();

    Metric reset(MetricType metricType, String name, Number value, Map<String, String> tags);


}
