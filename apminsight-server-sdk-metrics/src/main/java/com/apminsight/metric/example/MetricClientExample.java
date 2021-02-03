package com.apminsight.metric.example;

import com.apminsight.metric.client.MetricClient;
import com.apminsight.metric.util.MetricClientBuilder;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * MetricClientExample
 *
 * @author wangzhe
 */
public class MetricClientExample {

    public static void main(String[] args) throws IOException {
        MetricClient metricClient = MetricClientBuilder.newBuilder()
                .prefix("your metric common prefix")
                .build();

        //without tags
        metricClient.emitCounter("example_counter_metric");
        metricClient.emitCounter("example_counter_metric", 3);

        metricClient.emitTimer("example_timer_metric", 200);
        metricClient.emitTimer("example_timer_metric", 200, TimeUnit.MILLISECONDS);

        metricClient.emitGauge("example_gauge_metric", 100);

        //with tags
        Map<String, String> tags = new HashMap<>();
        tags.put("tagKey", "tagValue");
        metricClient.emitCounter("example_counter_metric",  tags);
        metricClient.emitCounter("example_counter_metric", 3, tags);

        metricClient.emitTimer("example_timer_metric", 200, tags);
        metricClient.emitTimer("example_timer_metric", 200, TimeUnit.MILLISECONDS, tags);

        metricClient.emitGauge("example_gauge_metric", 100, tags);

        metricClient.close();
    }

}
