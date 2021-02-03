package com.apminsight.metric.model;

/**
 * MetricConfig
 *
 * @author wangzhe
 */
public class MetricConfig {

    /**
     * common prefix of metric name
     */
    private String prefix;

    /**
     * use unix domain socket to send metric packet
     */
    private boolean unixDomainSocket = true;
    /**
     * domain unix socket address
     */
    private String sockAddress;

    /**
     * check domain unix socket address file exists
     * if checkSockAddress is true ,but sock address file not exists,client init will throw
     * {@link com.apminsight.metric.exception.UnixDomainSocketInitException}
     */
    private boolean checkSockAddress = true;

    /**
     * send metric to agent socket timeout in milliseconds
     */
    private int socketTimeOutMs = DEFAULT_SOCKET_TIMEOUT_MS;

    /**
     * send metric to agent socket max packet bytes limit
     */
    private int maxPacketBytes = DEFAULT_MAX_PACKET_BYTES;

    /**
     * whether to use metric pool
     */
    private boolean useMetricPool = false;

    /**
     * metric object pool min idle
     */
    private int metricPoolMinIdle = DEFAULT_METRIC_POOL_MIN_IDLE;
    /**
     * metric object pool max idle
     */
    private int metricPoolMaxIdle = DEFAULT_METRIC_POOL_MAX_IDLE;

    /**
     * byte buffer object pool min idle
     */
    private int byteBufferPoolMinIdle = DEFAULT_BYTEBUFFER_POOL_MIN_IDLE;
    /**
     * byte buffer object pool max idle
     */
    private int byteBufferPoolMaxIdle = DEFAULT_BYTEBUFFER_POOL_MAX_IDLE;


    /**
     * reporter buffer queue capacity.
     * <p>
     * metric will be discarded when the capacity is exceeded, you can increase this queue capacity or
     * {@link #reporterWorkers} or  {@link #senderWorkers} to avoid
     */
    private int reporterQueueCapacity = DEFAULT_REPORTER_QUEUE_CAPACITY;

    /**
     * number of reporter queue processor workers
     */
    private int reporterWorkers = DEFAULT_PROCESSOR_WORKERS;


    /**
     * send packet sync
     */
    private boolean senderSync = true;

    /**
     * number of sender queue processor workers if  {@link #senderSync} is false
     */
    private int senderWorkers = DEFAULT_SENDER_WORKERS;

    /**
     * reporter and sender processor waiting for empty poll
     */
    private int workerWaitSleepMs = 10;


    public static final String DEFAULT_SOCK_ADDRESS = "/var/run/apminsight/metrics.sock";

    public static final int DEFAULT_SOCKET_TIMEOUT_MS = 100;
    public static final int DEFAULT_MAX_PACKET_BYTES = 8192;

    public static final int DEFAULT_METRIC_POOL_MIN_IDLE = 0;
    public static final int DEFAULT_METRIC_POOL_MAX_IDLE = 8192;
    public static final int DEFAULT_BYTEBUFFER_POOL_MIN_IDLE = 128;
    public static final int DEFAULT_BYTEBUFFER_POOL_MAX_IDLE = 512;

    public static final int DEFAULT_PROCESSOR_WORKERS = 4;
    public static final int DEFAULT_SENDER_WORKERS = 4;

    public static final int DEFAULT_REPORTER_QUEUE_CAPACITY = Integer.MAX_VALUE;


    public MetricConfig() {
    }

    public MetricConfig(String prefix, String sockAddress) {
        this.prefix = prefix;
        this.sockAddress = sockAddress;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }


    public boolean isUnixDomainSocket() {
        return unixDomainSocket;
    }

    public void setUnixDomainSocket(boolean unixDomainSocket) {
        this.unixDomainSocket = unixDomainSocket;
    }

    public String getSockAddress() {
        return sockAddress;
    }

    public void setSockAddress(String sockAddress) {
        this.sockAddress = sockAddress;
    }

    public boolean isCheckSockAddress() {
        return checkSockAddress;
    }

    public void setCheckSockAddress(boolean checkSockAddress) {
        this.checkSockAddress = checkSockAddress;
    }

    public int getSocketTimeOutMs() {
        return socketTimeOutMs;
    }

    public void setSocketTimeOutMs(int socketTimeOutMs) {
        this.socketTimeOutMs = socketTimeOutMs;
    }

    public int getMaxPacketBytes() {
        return maxPacketBytes;
    }

    public void setMaxPacketBytes(int maxPacketBytes) {
        this.maxPacketBytes = maxPacketBytes;
    }

    public boolean isUseMetricPool() {
        return useMetricPool;
    }

    public void setUseMetricPool(boolean useMetricPool) {
        this.useMetricPool = useMetricPool;
    }

    public int getMetricPoolMinIdle() {
        return metricPoolMinIdle;
    }

    public void setMetricPoolMinIdle(int metricPoolMinIdle) {
        this.metricPoolMinIdle = metricPoolMinIdle;
    }

    public int getMetricPoolMaxIdle() {
        return metricPoolMaxIdle;
    }

    public void setMetricPoolMaxIdle(int metricPoolMaxIdle) {
        this.metricPoolMaxIdle = metricPoolMaxIdle;
    }

    public int getByteBufferPoolMinIdle() {
        return byteBufferPoolMinIdle;
    }

    public void setByteBufferPoolMinIdle(int byteBufferPoolMinIdle) {
        this.byteBufferPoolMinIdle = byteBufferPoolMinIdle;
    }

    public int getByteBufferPoolMaxIdle() {
        return byteBufferPoolMaxIdle;
    }

    public void setByteBufferPoolMaxIdle(int byteBufferPoolMaxIdle) {
        this.byteBufferPoolMaxIdle = byteBufferPoolMaxIdle;
    }


    public int getWorkerWaitSleepMs() {
        return workerWaitSleepMs;
    }

    public void setWorkerWaitSleepMs(int workerWaitSleepMs) {
        this.workerWaitSleepMs = workerWaitSleepMs;
    }


    public int getReporterWorkers() {
        return reporterWorkers;
    }

    public void setReporterWorkers(int reporterWorkers) {
        this.reporterWorkers = reporterWorkers;
    }

    public int getSenderWorkers() {
        return senderWorkers;
    }

    public void setSenderWorkers(int senderWorkers) {
        this.senderWorkers = senderWorkers;
    }

    public int getReporterQueueCapacity() {
        return reporterQueueCapacity;
    }

    public void setReporterQueueCapacity(int reporterQueueCapacity) {
        this.reporterQueueCapacity = reporterQueueCapacity;
    }

    public boolean isSenderSync() {
        return senderSync;
    }

    public void setSenderSync(boolean senderSync) {
        this.senderSync = senderSync;
    }

}
