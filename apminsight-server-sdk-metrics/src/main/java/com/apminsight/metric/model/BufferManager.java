package com.apminsight.metric.model;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Buffer
 *
 * @author wangzhe
 */
public class BufferManager<T> {

    private final int capacity;
    private final AtomicInteger bufferSize;

    private final ConcurrentLinkedQueue<T> buffer = new ConcurrentLinkedQueue<>();

    public BufferManager() {
        this.capacity = Integer.MAX_VALUE;
        this.bufferSize = new AtomicInteger(0);
    }

    public BufferManager(int capacity) {
        this.capacity = capacity;
        this.bufferSize = new AtomicInteger(0);
    }

    public boolean add(T t) {
        if (bufferSize.get() < capacity) {
            boolean offer = buffer.offer(t);
            if (offer) {
                bufferSize.incrementAndGet();
            }
            return offer;
        }
        return false;
    }

    public T poll() {
        T t = buffer.poll();
        if (t != null) {
            bufferSize.decrementAndGet();
        }
        return t;
    }

    public boolean isEmpty() {
        return buffer.isEmpty();
    }

    public boolean isFull() {
        return bufferSize.get() >= capacity;
    }


    public int size() {
        return bufferSize.get();
    }


}
