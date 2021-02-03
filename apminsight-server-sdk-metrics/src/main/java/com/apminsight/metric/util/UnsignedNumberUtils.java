package com.apminsight.metric.util;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * UnsignedNumberUtils
 *
 * @author wangzhe
 */
public class UnsignedNumberUtils {

    /**
     * transfer number to 1 byte
     * @param number number of short
     * @return byte of number
     */
    public static byte toUnsignedByte(short number) {
        return (byte) number;
    }

    public static byte[] toUnsignedBytes(Number number) {
        byte[] buffer = new byte[8];
        long value = Double.doubleToRawLongBits(number.doubleValue());
        buffer[0] = (byte) (value & 0xFF);
        buffer[1] = (byte) ((value >> 8) & 0xFF);
        buffer[2] = (byte) ((value >> 16) & 0xFF);
        buffer[3] = (byte) ((value >> 24) & 0xFF);
        buffer[4] = (byte) ((int) (value >> 32) & 0xFF);
        buffer[5] = (byte) ((int) (value >> 40) & 0xFF);
        buffer[6] = (byte) ((int) (value >> 48) & 0xFF);
        buffer[7] = (byte) ((int) (value >> 56) & 0xFF);
        return buffer;
    }

}
