package com.apminsight.metric.sender;

import java.io.Closeable;
import java.nio.ByteBuffer;

/**
 * Sender
 *
 * send packet to agent
 *
 * @author wangzhe
 */
public interface Sender extends Closeable {

    /**
     * start sender
     */
    void start();

    /**
     * send packet
     * @param packet encoded packet
     */
    void sendPacket(ByteBuffer packet);

}
