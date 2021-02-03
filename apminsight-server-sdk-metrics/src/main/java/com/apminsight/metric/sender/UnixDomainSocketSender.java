package com.apminsight.metric.sender;

import com.apminsight.metric.exception.ExceptionHandler;
import com.apminsight.metric.exception.UnixDomainSocketInitException;
import com.apminsight.metric.model.MetricConfig;
import com.apminsight.metric.pool.ByteBufferPool;
import com.apminsight.metric.util.ExecutorUtils;
import com.apminsight.metric.util.NamedThreadFactory;
import jnr.unixsocket.UnixDatagramChannel;
import jnr.unixsocket.UnixSocketAddress;
import jnr.unixsocket.UnixSocketOptions;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.concurrent.*;

/**
 * UnixDomainSocketSender
 * <p>
 * send by unix domain socket
 *
 * @author wangzhe
 */
public class UnixDomainSocketSender implements Sender {

    private final UnixSocketAddress address;
    private final DatagramChannel clientChannel;
    private final ConcurrentLinkedQueue<ByteBuffer> packetQueue;
    private final ByteBufferPool byteBufferPool;
    private volatile boolean running;
    private final int workerWaitSleepMs;
    private ExecutorService executor;
    private final MetricConfig metricConfig;
    private final ExceptionHandler exceptionHandler;


    public UnixDomainSocketSender(MetricConfig metricConfig, ByteBufferPool byteBufferPool, ExceptionHandler exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
        this.metricConfig = metricConfig;
        this.byteBufferPool = byteBufferPool;
        this.workerWaitSleepMs = metricConfig.getWorkerWaitSleepMs();
        this.packetQueue = new ConcurrentLinkedQueue<>();
        try {
            if (!metricConfig.isSenderSync()) {
                NamedThreadFactory namedThreadFactory = new NamedThreadFactory("metric-apminsight-sender-%d");
                this.executor = Executors.newFixedThreadPool(metricConfig.getSenderWorkers(), namedThreadFactory);
            }
            File sockFile = new File(metricConfig.getSockAddress());
            if (metricConfig.isCheckSockAddress() && !sockFile.exists()) {
                throw new UnixDomainSocketInitException("sock address file not found:" + metricConfig.getSockAddress());
            }
            this.address = new UnixSocketAddress(sockFile);
            clientChannel = UnixDatagramChannel.open();
            if (metricConfig.getSocketTimeOutMs() > 0) {
                clientChannel.setOption(UnixSocketOptions.SO_SNDTIMEO, metricConfig.getSocketTimeOutMs());
            }
            if (metricConfig.getMaxPacketBytes() > 0) {
                clientChannel.setOption(UnixSocketOptions.SO_SNDBUF, metricConfig.getMaxPacketBytes());
            }
            clientChannel.configureBlocking(false);
            running = true;
        } catch (IOException e) {
            throw new UnixDomainSocketInitException(e);
        }
    }

    @Override
    public void start() {
        if (!metricConfig.isSenderSync()) {
            for (int i = 0; i < metricConfig.getSenderWorkers(); i++) {
                executor.submit(new Processor());
            }
        }
    }

    @Override
    public void sendPacket(ByteBuffer packet) {
        try {
            if (metricConfig.isSenderSync()) {
                packet.flip();
                clientChannel.send(packet, address);
            } else {
                packetQueue.offer(packet);
            }
        } catch (Exception e) {
            exceptionHandler.handleException(e);
        } finally {
            if (metricConfig.isSenderSync()) {
                byteBufferPool.returnObject(packet);
            }
        }
    }

    @Override
    public void close() {
        running = false;
        ExecutorUtils.shutdown(executor);
        if (clientChannel != null) {
            try {
                clientChannel.close();
            } catch (final IOException e) {
                exceptionHandler.handleException(e);
            }
        }
    }

    private class Processor implements Runnable {
        @Override
        public void run() {
            ByteBuffer packet = null;
            while (!(packetQueue.isEmpty()) || running) {
                try {
                    packet = packetQueue.poll();
                    if (packet == null) {
                        Thread.sleep(workerWaitSleepMs);
                        continue;
                    }

                    packet.flip();
                    if (clientChannel != null) {
                        clientChannel.send(packet, address);
                    }
                } catch (InterruptedException e) {
                    if (!running) {
                        return;
                    }
                } catch (Exception e) {
                    exceptionHandler.handleException(e);
                } finally {
                    byteBufferPool.returnObject(packet);
                }
            }
        }
    }


}
