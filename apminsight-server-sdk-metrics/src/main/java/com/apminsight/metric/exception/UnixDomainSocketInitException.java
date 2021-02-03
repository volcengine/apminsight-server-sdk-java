package com.apminsight.metric.exception;

/**
 * MetricInitException
 *
 * @author wangzhe
 */
public class UnixDomainSocketInitException extends RuntimeException{
    public UnixDomainSocketInitException() {
    }

    public UnixDomainSocketInitException(String message) {
        super(message);
    }

    public UnixDomainSocketInitException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnixDomainSocketInitException(Throwable cause) {
        super(cause);
    }
}
