package com.hong.common.utils;

public class CommonUtils {
    public static String getRandomString(int length) {
        String str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int index = (int) (Math.random() * str.length());
            sb.append(str.charAt(index));
        }
        return sb.toString();
    }
}