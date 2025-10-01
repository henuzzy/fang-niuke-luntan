package com.yue.nklt.utils;

import java.util.UUID;

/**
 * 社区工具类
 */
public class CommunityUtil {

    /**
     * 生成随机字符串
     */
    public static String generateUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }
}

