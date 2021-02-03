package com.apminsight.metric.model;

import java.util.Map;

/**
 * MetricEntity
 *
 * @author wangzhe
 */
public class MetricEntity implements Metric{

    private String name;
    private Number value;
    private Map<String, String> tags;
    private MetricType metricType;

    public MetricEntity() {
    }

    public MetricEntity(MetricType metricType,String name, Number value, Map<String, String> tags) {
        this.name = name;
        this.value = value;
        this.tags = tags;
        this.metricType = metricType;
    }


    @Override
    public String getName() {
        return name;
    }

    @Override
    public Number getValue() {
        return value;
    }

    @Override
    public Map<String, String> getTags() {
        return tags;
    }

    @Override
    public Metric reset(MetricType metricType, String name, Number value, Map<String, String> tags) {
        this.name = name;
        this.value = value;
        this.tags = tags;
        this.metricType = metricType;
        return this;
    }

    @Override
    public MetricEntity reset() {
        name = "";
        value = null;
        tags = null;
        return this;
    }

    @Override
    public MetricType getMetricType() {
        return metricType;
    }

}
