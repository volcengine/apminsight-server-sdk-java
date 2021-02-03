package com.apminsight.metric.pool;

import com.apminsight.metric.model.Metric;
import com.apminsight.metric.model.MetricEntity;

/**
 * MetricPool
 *
 * @author wangzhe
 */
public final class MetricPool extends BasePool<Metric> {

    public MetricPool(int minIdle, int maxIdle) {
        super(minIdle, maxIdle);
    }

    @Override
    Metric create() {
        return new MetricEntity();
    }

    @Override
    Metric reset(Metric e) {
        return e.reset();
    }

}
