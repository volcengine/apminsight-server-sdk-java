package com.apminsight.metric.exception;

/**
 * DefaultExceptionHandler
 *
 * @author wangzhe
 */
public class DefaultExceptionHandler implements ExceptionHandler{


    @Override
    public void handleException(Exception e) {
        //do nothing
    }

    @Override
    public void handleError(String message) {
        //do nothing
    }
}
