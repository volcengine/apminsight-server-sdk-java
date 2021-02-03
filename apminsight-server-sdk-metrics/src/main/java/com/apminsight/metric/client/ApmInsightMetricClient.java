package com.apminsight.metric.client;

import com.apminsight.metric.model.MetricType;
import com.apminsight.metric.exception.DefaultExceptionHandler;
import com.apminsight.metric.exception.ExceptionHandler;
import com.apminsight.metric.model.Metric;
import com.apminsight.metric.model.MetricConfig;
import com.apminsight.metric.model.MetricEntity;
import com.apminsight.metric.pool.ByteBufferPool;
import com.apminsight.metric.pool.MetricPool;
import com.apminsight.metric.reporter.MetricReporter;
import com.apminsight.metric.reporter.Reporter;
import com.apminsight.metric.sender.Sender;
import com.apminsight.metric.sender.UnixDomainSocketSender;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * MetricsClient
 *
 * @author wangzhe
 */
public class ApmInsightMetricClient implements MetricClient {

    private volatile boolean running;

    private final MetricPool metricPool;
    private final ByteBufferPool byteBufferPool;

    private final Reporter<Metric> reporter;
    private final Sender sender;
    private final boolean useMetricPool;


    public ApmInsightMetricClient(MetricConfig metricConfig, ExceptionHandler exceptionHandler) {
        exceptionHandler = exceptionHandler == null ? new DefaultExceptionHandler() : exceptionHandler;

        this.useMetricPool = metricConfig.isUseMetricPool();
        this.byteBufferPool = new ByteBufferPool(metricConfig.getByteBufferPoolMinIdle(), metricConfig.getByteBufferPoolMaxIdle(), metricConfig.getMaxPacketBytes());
        this.metricPool = new MetricPool(metricConfig.getMetricPoolMinIdle(), metricConfig.getMetricPoolMaxIdle());

        this.sender = new UnixDomainSocketSender(metricConfig, this.byteBufferPool, exceptionHandler);
        this.reporter = new MetricReporter(metricConfig, metricPool, byteBufferPool, sender, exceptionHandler);

        this.sender.start();
        this.reporter.start();

        this.running = true;
    }

    @Override
    public void emitCounter(String name) {
        this.emitCounter(name, 1, null);
    }

    @Override
    public void emitCounter(String name, Number value) {
        this.emitCounter(name, value, null);
    }

    @Override
    public void emitCounter(String name, Map<String, String> tags) {
        this.emitCounter(name, 1, tags);
    }

    @Override
    public void emitCounter(String name, Number value, Map<String, String> tags) {
        this.emitMetric(MetricType.COUNTER, name, value, tags);
    }

    @Override
    public void emitTimer(String name, Number mills) {
        this.emitTimer(name, mills, TimeUnit.MILLISECONDS);
    }

    @Override
    public void emitTimer(String name, Number value, TimeUnit timeUnit) {
        this.emitTimer(name, value, timeUnit, null);
    }

    @Override
    public void emitTimer(String name, Number mills, Map<String, String> tags) {
        this.emitMetric(MetricType.TIMER, name, mills, tags);
    }

    @Override
    public void emitTimer(String name, Number value, TimeUnit timeUnit, Map<String, String> tags) {
        this.emitTimer(name, timeUnit.toNanos(value.longValue()), tags);
    }

    @Override
    public void emitGauge(String name, Number value) {
        this.emitGauge(name, value, null);
    }

    @Override
    public void emitGauge(String name, Number value, Map<String, String> tags) {
        this.emitMetric(MetricType.GAUGE, name, value, tags);

    }

    private void emitMetric(MetricType metricType, String name, Number value, Map<String, String> tags) {
        if (running) {
            if (!useMetricPool) {
                reporter.report(new MetricEntity(metricType, name, value, tags));
            } else {
                reporter.report(metricPool.borrow().reset(metricType, name, value, tags));
            }
        }
    }

    @Override
    public void close() throws IOException {
        this.running = false;
        this.reporter.close();
        this.metricPool.close();
        this.sender.close();
        this.byteBufferPool.close();
    }
}
