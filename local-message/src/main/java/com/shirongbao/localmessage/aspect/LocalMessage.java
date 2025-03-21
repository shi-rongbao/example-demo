package com.shirongbao.localmessage.aspect;

import java.lang.annotation.*;

/**
 * @author: ShiRongbao
 * @date: 2025-03-17
 * @description:
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LocalMessage {

    // 最大重试次数（包括第一次正常执行），默认3次
    int maxRetryTimes() default 3;

    // 是否异步执行，默认异步执行，先入库，后续异步执行，不影响主线程快速返回结果
    boolean async() default true;
}
