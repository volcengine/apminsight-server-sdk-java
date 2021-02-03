package com.apminsight.metric.reporter;

import com.apminsight.metric.model.MetricType;
import com.apminsight.metric.exception.ExceptionHandler;
import com.apminsight.metric.model.BufferManager;
import com.apminsight.metric.model.Metric;
import com.apminsight.metric.model.MetricConfig;
import com.apminsight.metric.model.Pair;
import com.apminsight.metric.pool.ByteBufferPool;
import com.apminsight.metric.pool.MetricPool;
import com.apminsight.metric.sender.Sender;
import com.apminsight.metric.util.ExecutorUtils;
import com.apminsight.metric.util.MetricParser;
import com.apminsight.metric.util.NamedThreadFactory;
import com.apminsight.metric.util.UnsignedNumberUtils;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Reporter
 *
 * @author wangzhe
 */
public class MetricReporter implements Reporter<Metric> {

    private final MetricConfig metricConfig;

    private final boolean useMetricPool;

    /**
     * reporter is shutdown
     */
    protected volatile boolean running;

    /**
     * sender of reporter
     */
    private final Sender sender;
    /**
     * metric buffer,scheduled task refresh
     */
    private final BufferManager<Metric> bufferManager;
    /**
     * parser of metric prefix、name、tags
     */
    private final MetricParser parser;
    /**
     * object pool of ByteBuffer
     */
    private final ByteBufferPool byteBufferPool;
    /**
     * object pool of metric
     */
    private final MetricPool metricPool;

    private final ExecutorService executor;

    private final ExceptionHandler exceptionHandler;

    public MetricReporter(MetricConfig metricConfig, MetricPool metricPool, ByteBufferPool byteBufferPool, Sender sender,ExceptionHandler exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
        this.bufferManager = new BufferManager<>(metricConfig.getReporterQueueCapacity());
        this.metricConfig = metricConfig;
        this.useMetricPool = metricConfig.isUseMetricPool();

        this.parser = new MetricParser(metricConfig.getPrefix());

        this.metricPool = metricPool;
        this.byteBufferPool = byteBufferPool;
        this.sender = sender;
        this.running = true;
        this.executor = Executors.newFixedThreadPool(metricConfig.getReporterWorkers(), new NamedThreadFactory("metric-apminsight-processor-%d"));
    }

    @Override
    public void start() {
        // n workers poll metric from buffer
        for (int i = 0; i < metricConfig.getReporterWorkers(); i++) {
            executor.submit(new Processor());
        }
    }

    @Override
    public void report(Metric e) {
        bufferManager.add(e);
    }

    /**
     * processor poll metric from buffer
     *
     * 1. poll metric, send buffer if poll empty
     * 2. encode and write to buffer
     * 3. send buffer if buffer full
     */
    private class Processor implements Runnable{
        EncodedMetric encodedMetric = new EncodedMetric();
        @Override
        public void run() {
            Metric metric = null;
            ByteBuffer byteBuffer = byteBufferPool.borrow();
            while (!bufferManager.isEmpty() || running) {
                try {
                    if (bufferManager.isEmpty()) {
                        if (byteBuffer != null && byteBuffer.position() > 0) {
                            sender.sendPacket(byteBuffer);
                            byteBuffer = byteBufferPool.borrow();
                        }
                        Thread.sleep(metricConfig.getWorkerWaitSleepMs());
                        continue;
                    }

                    metric = bufferManager.poll();

                    if (metric != null) {
                        byteBuffer = sendMetric(byteBuffer, encodedMetric, metric);
                    }
                    metric = null;
                } catch (InterruptedException e) {
                    if (metric != null) {
                        //return to buffer
                        bufferManager.add(metric);
                    }
                    exceptionHandler.handleException(e);
                } catch (Exception e) {
                    exceptionHandler.handleException(e);
                }
            }
            if (byteBuffer != null && byteBuffer.position() > 0) {
                sender.sendPacket(byteBuffer);
            }
        }
    }

    private ByteBuffer sendMetric(ByteBuffer byteBuffer, EncodedMetric encodedMetric, Metric metric) throws InterruptedException {
        MetricType metricType;
        encodedMetric.reset();
        try {
            if (!encodeMetric(metric, encodedMetric)) {
                return byteBuffer;
            }
            metricType = metric.getMetricType();
        } finally {
            if (useMetricPool) {
                metricPool.returnObject(metric);
            }
        }

        //packet too big
        if (byteBuffer.capacity() < encodedMetric.getTotalLength()) {
            return byteBuffer;
        }
        //current buffer not enough
        if (byteBuffer.remaining() < encodedMetric.getTotalLength()) {
            sender.sendPacket(byteBuffer);
            byteBuffer = byteBufferPool.borrow();
        }

        writeToBuffer(byteBuffer, encodedMetric, metricType);

        return byteBuffer;
    }

    /**
     * write encoded metric to byte buffer
     */
    private boolean writeToBuffer(ByteBuffer byteBuffer, EncodedMetric encodedMetric, MetricType metricType) {
        if (encodedMetric.getTotalLength() > byteBuffer.capacity()) {
            // message longer than size of packet
            return false;
        }
        //write [1byte metric type][metrics prefix.name byte][8byte value][tags byte]
        byteBuffer.put(metricType.getId());

        byteBuffer.put((byte) encodedMetric.nameLength);
        byteBuffer.put(encodedMetric.name);
        //fixed 8 byte
        byteBuffer.put(encodedMetric.value);
        byteBuffer.put((byte) encodedMetric.tagSize);
        //header+tag k or header+tag v
        if (encodedMetric.tagSize > 0) {
            for (byte[] bytes : encodedMetric.tags) {
                byteBuffer.put((byte) bytes.length);
                byteBuffer.put(bytes);
            }
        }
        return true;
    }

    private int getTagSize(Map<String, String> tags) {
        if (tags == null) {
            return 0;
        }
        return tags.size();
    }

    private boolean encodeMetric(Metric metric, EncodedMetric encodedMetric) {

        encodedMetric.reset();
        //name with prefix
        encodedMetric.name = parser.getBytes(metric.getName(), true);
        encodedMetric.nameLength = encodedMetric.name.length;
        if (isTooLong(encodedMetric.nameLength)) {
            exceptionHandler.handleError("metric string length must less than 256");
            return false;
        }

        //tag
        encodedMetric.tagSize = getTagSize(metric.getTags());
        if (encodedMetric.tagSize > 0) {
            if (!isTooLong(encodedMetric.tagSize)) {
                Pair<List<byte[]>, Integer> encodedTags = getEncodedTags(metric.getTags());
                if (encodedTags != null) {
                    encodedMetric.tags = encodedTags.getLeft();
                    encodedMetric.tagLength = encodedTags.getRight();
                } else {
                    //get encoded tags error
                    exceptionHandler.handleError("get encoded tags error");
                    return false;
                }
            } else {
                //tag map must smaller than 256
                exceptionHandler.handleError("tag map must smaller than 256");
                return false;
            }
        }
        //value
        encodedMetric.value = UnsignedNumberUtils.toUnsignedBytes(metric.getValue());
        return true;
    }

    private Pair<List<byte[]>, Integer> getEncodedTags(Map<String, String> tags) {
        List<byte[]> encodedTags = new ArrayList<>(tags.size());

        int totalLength = 0;
        for (Map.Entry<String, String> entry : tags.entrySet()) {
            byte[] keyBytes = parser.getBytes(entry.getKey());
            byte[] valueBytes = parser.getBytes(entry.getValue());

            if (!isTooLong(keyBytes.length) && !isTooLong(valueBytes.length)) {
                totalLength += keyBytes.length;
                totalLength += valueBytes.length;
                encodedTags.add(keyBytes);
                encodedTags.add(valueBytes);
            } else {
                //string length must less than 256
                exceptionHandler.handleError("tag string length must less than 256");
                return null;
            }
        }

        return Pair.of(encodedTags, totalLength);
    }

    private boolean isTooLong(int length) {
        return length > 255;
    }

    @Override
    public void close() throws IOException {
        stop();
    }

    private void stop() {
        this.running = false;
        ExecutorUtils.shutdown(executor);
    }


    public static class EncodedMetric {
        private int nameLength;
        //size of tag map
        private int tagSize;
        //length of tag k v
        private int tagLength;

        private byte[] name;
        private byte[] value;
        //all writeString k writeString v
        private List<byte[]> tags;


        int getTotalLength() {
            //(metric type) +  (header + name length)+(value 8 byte) + (header + (tag k+tag v).size+(tag k+tag v).length)
            return 1 + (1 + nameLength) + 8 + (1 + (tagSize << 1) + tagLength);
        }

        void reset() {
            nameLength = 0;
            tagSize = 0;
            tagLength = 0;
            name = null;
            value = null;
            tags = null;
        }

    }
}
