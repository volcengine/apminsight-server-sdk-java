package com.apminsight.metric.model;

import com.apminsight.metric.util.UnsignedNumberUtils;

/**
 * MetricType
 *
 * @author wangzhe
 */
public enum MetricType {

    /**
     * Usually used to measure the quantity, such as the amount of error QPS , etc.
     */
    COUNTER((short) 1),
    /**
     * Usually used to measure the code execute time duration
     * The input value will be summarized, and finally the avg and quantile values will be displayed
     */
    TIMER((short) 2),
    /**
     * Usually used to measure internal system status, such as queue length, etc.
     */
    GAUGE((short) 3);

    /**
     * id of metric type
     */
    private final byte id;

    MetricType(short id) {
        this.id = UnsignedNumberUtils.toUnsignedByte(id);
    }

    public byte getId() {
        return id;
    }
}
