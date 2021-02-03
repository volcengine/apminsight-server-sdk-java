package com.apminsight.metric.util;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;

/**
 * MetricParser
 *
 * Parser of metric name、prefix、tags
 *
 * @author wangzhe
 */
public class MetricParser {

    private final boolean hasPrefix;
    private byte[] prefixWithDotBytes;
    private final HashMap<String, byte[]> parsedCache = new HashMap<>();
    private final HashMap<String, byte[]> parsedWithPrefixCache = new HashMap<>();

    public MetricParser(String prefix) {
        this.hasPrefix = prefix != null && prefix.length() > 0;
        if (hasPrefix) {
            this.prefixWithDotBytes = Bytes.concat(prefix.getBytes(StandardCharsets.UTF_8), new byte[]{(byte) '.'});
        }
    }

    public byte[] getBytes(String name) {
        return getBytes(name, false);
    }

    public byte[] getBytes(String name, boolean withPrefix) {
        boolean usePrefix = withPrefix && hasPrefix;
        final HashMap<String, byte[]> cache = usePrefix ? parsedWithPrefixCache : parsedCache;

        byte[] bytes = cache.get(name);
        if (bytes != null) {
            return bytes;
        }

        //make bytes cache
        if (usePrefix) {
            //prefix.name
            bytes = Bytes.concat(prefixWithDotBytes, name.getBytes(StandardCharsets.UTF_8));
        } else {
            bytes = name.getBytes(StandardCharsets.UTF_8);
        }
        synchronized (this) {
            cache.put(name, bytes);
        }

        return bytes;
    }
}
