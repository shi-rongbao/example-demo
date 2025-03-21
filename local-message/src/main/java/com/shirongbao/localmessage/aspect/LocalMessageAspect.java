package com.shirongbao.localmessage.aspect;

import com.alibaba.fastjson.JSON;
import com.shirongbao.localmessage.constant.BasicConstant;
import com.shirongbao.localmessage.constant.TaskStatusEnum;
import com.shirongbao.localmessage.entity.InvokeCtx;
import com.shirongbao.localmessage.entity.LocalMessageDo;
import com.shirongbao.localmessage.service.LocalMessageService;
import com.shirongbao.localmessage.utils.InvokeStatusHolder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

/**
 * @author: ShiRongbao
 * @date: 2025-03-17
 * @description:
 */
@Aspect
@Component
@RequiredArgsConstructor
@Order(Ordered.HIGHEST_PRECEDENCE)
@Slf4j
public class LocalMessageAspect {

    private final LocalMessageService localMessageService;

    @Around("@annotation(com.shirongbao.localmessage.aspect.LocalMessage)")
    public Object doAspect(ProceedingJoinPoint joinPoint) throws Throwable {
        log.info("切面执行了,当前时间:{}", System.currentTimeMillis());
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        LocalMessage localMessage = AnnotationUtils.findAnnotation(method, LocalMessage.class);
        if (ObjectUtils.isEmpty(localMessage)) {
            return joinPoint.proceed();
        }

        if (InvokeStatusHolder.inInvoke()) {
            return joinPoint.proceed();
        }

        boolean async = localMessage.async();
        List<String> params = Arrays.stream(method.getParameterTypes())
                .map(Class::getName)
                .toList();

        // 记录执行上下文
        InvokeCtx ctx = InvokeCtx.builder()
                .className(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .paramTypes(JSON.toJSONString(params))
                .args(JSON.toJSONString(joinPoint.getArgs()))
                .build();
        LocalMessageDo localMessageDo = new LocalMessageDo();
        localMessageDo.setReqSnapshot(JSON.toJSONString(ctx));
        localMessageDo.setMaxRetryTimes(localMessage.maxRetryTimes());
        localMessageDo.setNextRetryTime(offsetTimestamp(BasicConstant.RETRY_INTERVAL_MINUTES));
        localMessageService.invoke(localMessageDo, async);
        return null;
    }

    // 计算下次重试的时间戳
    private long offsetTimestamp(int minutes) {
        return System.currentTimeMillis() + (minutes * 60 * 1000L);
    }

}
