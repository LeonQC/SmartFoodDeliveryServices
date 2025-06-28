package com.chris.context;

public class UserContext {

    private static final ThreadLocal<Long> CURRENT_LOCAL = new ThreadLocal<>();

    public static void setCurrentId(Long userId) {
        CURRENT_LOCAL.set(userId);
    }

    public static Long getCurrentId() {
        return CURRENT_LOCAL.get();
    }

    public static void remove() {
        CURRENT_LOCAL.remove();
    }
}
