package com.apminsight.metric.pool;

import java.io.IOException;
import java.util.Collection;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * BasePool
 *
 * @author wangzhe
 */
public abstract class BasePool<T> implements Pool<T> {

    protected final ConcurrentLinkedQueue<T> pool;

    protected int minIdle;
    protected int maxIdle;

    private volatile boolean running;

    public BasePool() {
        this.minIdle = 0;
        this.maxIdle = 1000;
        this.pool = new ConcurrentLinkedQueue<>();
    }

    public BasePool(int minIdle, int maxIdle) {
        this.minIdle = minIdle;
        this.maxIdle = maxIdle;
        this.pool = initPool(minIdle);

        this.running = true;
    }

    protected ConcurrentLinkedQueue<T> initPool(int size) {
        final ConcurrentLinkedQueue<T> pool = new ConcurrentLinkedQueue<>();
        for (int i = 0; i < size; i++) {
            pool.add(create());
        }
        return pool;
    }

    /**
     * create object
     * @return new object
     */
    abstract T create();

    /**
     * reset given object
     * @param e object
     * @return reset object
     */
    abstract T reset(T e);

    @Override
    public T borrow() {
        T t = pool.poll();
        return t == null ? reset(create()) : t;
    }

    @Override
    public void returnObject(T t) {
        if (t != null && running && pool.size() < maxIdle) {
            pool.add(reset(t));
        }
    }

    @Override
    public void returnObjects(Collection<T> t) {
        if (t != null && t.size() + pool.size() < maxIdle) {
            pool.addAll(t);
        }
    }

    @Override
    public void close() throws IOException {
        running = false;
    }
}
