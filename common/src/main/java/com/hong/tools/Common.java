package com.hong.tools;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Common {

    public static String check() {
        log.info("Common module is invoked.");
        return String.valueOf(System.currentTimeMillis());
    }
}
