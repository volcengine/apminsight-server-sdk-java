package com.apminsight.metric.util;

import com.apminsight.metric.client.ApmInsightMetricClient;
import com.apminsight.metric.client.MetricClient;
import com.apminsight.metric.exception.ExceptionHandler;
import com.apminsight.metric.model.Constants;
import com.apminsight.metric.model.MetricConfig;

/**
 * MetricClientBuilder
 *
 * builder of MetricClient
 *
 * @author wangzhe
 */
public final class MetricClientBuilder {

    private final MetricConfig metricConfig;
    private ExceptionHandler exceptionHandler;

    private MetricClientBuilder() {
        this.metricConfig = new MetricConfig();
    }

    private MetricClientBuilder(MetricConfig metricConfig) {
        this.metricConfig = metricConfig;
    }

    public static MetricClientBuilder newBuilder() {
        return new MetricClientBuilder();
    }

    public static MetricClientBuilder newBuilder(MetricConfig metricConfig) {
        return new MetricClientBuilder(metricConfig);
    }

    public MetricClientBuilder exceptionHandler(ExceptionHandler exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
        return this;
    }

    public MetricClientBuilder prefix(String prefix) {
        metricConfig.setPrefix(prefix);
        return this;
    }

    public MetricClientBuilder unixDomainSocket(boolean unixDomainSocket) {
        metricConfig.setUnixDomainSocket(unixDomainSocket);
        return this;
    }

    public MetricClientBuilder sockAddress(String sockAddress) {
        metricConfig.setSockAddress(sockAddress);
        return this;
    }

    public MetricClientBuilder checkSockAddress(boolean checkSockAddress) {
        metricConfig.setCheckSockAddress(checkSockAddress);
        return this;
    }

    public MetricClientBuilder useMetricPool(boolean useMetricPool) {
        metricConfig.setUseMetricPool(useMetricPool);
        return this;
    }

    public MetricClientBuilder metricPoolMinIdle(int metricPoolMinIdle) {
        metricConfig.setMetricPoolMinIdle(metricPoolMinIdle);
        return this;
    }

    public MetricClientBuilder metricPoolMaxIdle(int metricPoolMaxIdle) {
        metricConfig.setMetricPoolMaxIdle(metricPoolMaxIdle);
        return this;
    }

    public MetricClientBuilder byteBufferPoolMinIdle(int byteBufferPoolMinIdle) {
        metricConfig.setByteBufferPoolMinIdle(byteBufferPoolMinIdle);
        return this;
    }

    public MetricClientBuilder byteBufferPoolMaxIdle(int byteBufferPoolMaxIdle) {
        metricConfig.setByteBufferPoolMaxIdle(byteBufferPoolMaxIdle);
        return this;
    }

    public MetricClientBuilder socketTimeOutMs(int socketTimeOutMs) {
        metricConfig.setSocketTimeOutMs(socketTimeOutMs);
        return this;
    }

    public MetricClientBuilder maxPacketBytes(int maxPacketBytes) {
        metricConfig.setMaxPacketBytes(maxPacketBytes);
        return this;
    }


    public MetricClientBuilder processorWorkers(int processorWorkers) {
        metricConfig.setReporterWorkers(processorWorkers);
        return this;
    }

    public MetricClientBuilder senderWorkers(int senderWorkers) {
        metricConfig.setSenderWorkers(senderWorkers);
        return this;
    }

    public MetricClientBuilder bufferQueueCapacity(int bufferQueueCapacity) {
        metricConfig.setReporterQueueCapacity(bufferQueueCapacity);
        return this;
    }

    public MetricClientBuilder senderSync(boolean sync) {
        metricConfig.setSenderSync(sync);
        return this;
    }

    public MetricClient build() {
        if (metricConfig.isUnixDomainSocket()) {
            if (Strings.isEmpty(metricConfig.getSockAddress())) {
                String sockAddressEnv = System.getenv(Constants.SOCK_ADDRESS_ENV);
                metricConfig.setSockAddress(Strings.isEmpty(sockAddressEnv) ? MetricConfig.DEFAULT_SOCK_ADDRESS : sockAddressEnv);
            }
        }
        return new ApmInsightMetricClient(metricConfig, exceptionHandler);
    }

}
