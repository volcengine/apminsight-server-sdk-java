package com.apminsight.metric.pool;

import java.nio.ByteBuffer;

/**
 * BufferPool
 *
 * @author wangzhe
 */
public class ByteBufferPool extends BasePool<ByteBuffer> {
    private int bufferMaxSize = 512;

    public ByteBufferPool(int minIdle, int maxIdle, int bufferMaxSize) {
        super();
        this.minIdle = minIdle;
        this.maxIdle = maxIdle;
        this.bufferMaxSize = bufferMaxSize;
        initPool(this.minIdle);
    }


    @Override
    ByteBuffer create() {
        return ByteBuffer.allocate(bufferMaxSize);
    }

    @Override
    ByteBuffer reset(ByteBuffer e) {
        e.clear();
        return e;
    }

}
