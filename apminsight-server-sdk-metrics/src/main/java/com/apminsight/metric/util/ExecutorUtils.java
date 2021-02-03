package com.apminsight.metric.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * ExecutorUtils
 *
 * @author wangzhe
 */
public class ExecutorUtils {

    public static void shutdown(ExecutorService executorService) {
        if (executorService != null) {
            executorService.shutdown();
            try {
                executorService.awaitTermination(10, TimeUnit.SECONDS);
                if (!executorService.isTerminated()) {
                    executorService.shutdownNow();
                }
            } catch (Exception e) {
                if (!executorService.isTerminated()) {
                    executorService.shutdownNow();
                }
            }
        }
    }

}
