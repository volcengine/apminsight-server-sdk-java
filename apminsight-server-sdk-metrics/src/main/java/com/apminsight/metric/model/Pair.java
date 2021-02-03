package com.apminsight.metric.model;

/**
 * Pair
 *
 * @author wangzhe
 */
public class Pair<L,R> {

    private final L left;
    private final R right;
    public Pair(L k, R v) {
        this.left = k;
        this.right = v;
    }

    public static <L, R> Pair<L, R> of(final L left, final R right) {
        return new Pair<>(left, right);
    }

    public L getLeft() {
        return left;
    }

    public R getRight() {
        return right;
    }
}
