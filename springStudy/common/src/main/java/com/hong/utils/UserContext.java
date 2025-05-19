package com.hong.utils;

public class UserContext {

    private static final ThreadLocal<Long> tl = new ThreadLocal<>();

    /**
     * 保存当前登陆用户信息到 ThreadLocal
     *
     * @param userId
     */
    public static void setUser(Long userId) {
        tl.set(userId);
    }

    /**
     * 获取当前登陆用户信息
     *
     * @return
     */
    public static Long getUser() {
        return tl.get();
    }

    /**
     * 移除当前登陆用户信息
     */
    public static void removeUser() {
        tl.remove();
    }

}
