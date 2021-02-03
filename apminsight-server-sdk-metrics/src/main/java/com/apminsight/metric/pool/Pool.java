package com.apminsight.metric.pool;

import com.apminsight.metric.model.Metric;

import java.io.Closeable;
import java.util.Collection;

/**
 * Pool
 *
 * @author wangzhe
 */
public interface Pool<T> extends Closeable {


    T borrow();

    void returnObject(T t);

    void returnObjects(Collection<T> t);

}
