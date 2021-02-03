# APMInsight Server SDK Java


## Metrics 

### Installation

maven dependency

```xml
<dependency>
    <groupId>com.volcengine</groupId>
    <artifactId>apminsight-server-sdk-metrics</artifactId>
    <version>{latest-version}</version>
</dependency>
```


### Usage

see [CODE EXAMPLE](./apminsight-server-sdk-metrics/src/main/java/com/apminsight/metric/example/MetricClientExample.java) 

#### Build Client

```java
MetricClient metricClient = MetricClientBuilder.newBuilder()
        .unixDomainSocket(true)
        .prefix("your application metric name common prefix")
        .maxPacketBytes(8192)
        .build();

metricClient.close();
```

#### Emit Metrics


```java
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
```

#### Configuration

you can config client with `MetricClientBuilder`,there are some config items available in `MetricConfig` such as:

- `prefix` the common prefix of metric name
- `unixDomainSocket` use unix domain socket to send metric packet
- `sockAddress` domain unix socket address
- `socketTimeOutMs` send metric to agent socket timeout in milliseconds
- `maxPacketBytes` send metric to agent socket max packet bytes limit
- `useMetricPool` whether to use metric object pool
- `metricPoolMinIdle` metric object pool min idle
- `metricPoolMaxIdle` metric object pool max idle
- `byteBufferPoolMinIdle` byte buffer object pool min idle
- `byteBufferPoolMaxIdle` byte buffer object pool max idle
- `reporterQueueCapacity` reporter buffer queue capacity.metric will be discarded when the capacity is exceeded, you can increase this queue capacity or `reporterWorkers` or  `senderWorkers` to avoid
- `reporterWorkers` number of reporter queue processor workers
- `senderSync` send packet sync or async
- `senderWorkers` number of sender queue processor workers if  `senderSync` is false
- `workerWaitSleepMs` reporter and sender processor waiting for empty poll
