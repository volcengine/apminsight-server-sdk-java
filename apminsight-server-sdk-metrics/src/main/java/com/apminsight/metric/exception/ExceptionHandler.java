package com.apminsight.metric.exception;

/**
 * ExceptionHandler
 * handle metric client exception
 *
 * @author wangzhe
 */
public interface ExceptionHandler {

    /**
     * exception handler
     * @param e exception happened in client
     */
    void handleException(Exception e);

    /**
     * some error without exception
     * @param message error message
     */
    void handleError(String message);


}
