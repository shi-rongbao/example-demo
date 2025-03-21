package com.shirongbao.localmessage.demo;

import com.alibaba.fastjson.JSON;
import com.shirongbao.localmessage.aspect.LocalMessage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.framework.AopContext;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * @author: ShiRongbao
 * @date: 2025-03-17
 * @description:
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class LocalMessageDemo implements ApplicationListener<ApplicationReadyEvent> {

    private int retryTimes = 0;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
        testMethod();
    }

    public void testMethod() {
        try {
            // 获取当前类的代理对象
            LocalMessageDemo demo = (LocalMessageDemo) AopContext.currentProxy();
            demo.testRetryMethod(2000L, new ModelData("nihao", 18));
        } catch (Exception e) {
            log.error("testMethod error", e);
        }
    }

    @LocalMessage
    public void testRetryMethod(Long time, ModelData modelData) {
        try {
            Thread.sleep(time);
            log.info("当前方法执行了, 当前时间:{}", System.currentTimeMillis());
        } catch (InterruptedException e) {
            log.error("sleep error", e);
        }
        log.info("retryTimes: {}, data: {}", retryTimes, JSON.toJSONString(modelData));
        if (retryTimes ++ < 2) {
            throw new RuntimeException("retry");
        }
    }

    @Data
    @AllArgsConstructor
    static class ModelData{

        private String name;

        private Integer age;
    }
}
