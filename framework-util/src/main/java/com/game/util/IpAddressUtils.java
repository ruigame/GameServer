package com.game.util;

/**
 * IP地址工具
 * @Author: liguorui
 * @Date: 2020/9/20 9:10 下午
 */
public class IpAddressUtils {

    /**
     * 将127.0.0.1 格式的地址转换为长整形HASH值
     * @param ipAddress
     * @return
     */
    public static long hashCode(String ipAddress) {
        String[] splits = ipAddress.split("\\.");
        long ipValue = 0;
        int offset = 24;
        for (String item : splits) {
            long part = Long.valueOf(item);
            ipValue += (part << offset);
            offset -= 8;
        }
        return ipValue;
    }

    /**
     * 将127.0.0.1 格式的地址和端口转换为长整形HASH值
     * @param ipAddress
     * @return
     */
    public static long hashCode(String ipAddress, int port) {
        return hashCode(ipAddress) + port;
    }
}
