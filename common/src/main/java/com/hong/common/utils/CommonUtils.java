package com.hong.common.utils;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;

public class CommonUtils {

    /**
     * 使用雪花算法生成不重复的字符串
     * @return 不重复的字符串
     */
    public static String generateUniqueString() {
        return String.valueOf(IdWorker.getId());
    }
}
