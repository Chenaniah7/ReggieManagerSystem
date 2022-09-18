package com.gcc.reggie.common;

public class BaseContext {

    private static final ThreadLocal<Long> THREAD_LOCAL = new ThreadLocal<>();

    public static Long getCurrentId(){
        return THREAD_LOCAL.get();
    }

    public static void setCurrentId(Long currentId){
        THREAD_LOCAL.set(currentId);
    }
}
