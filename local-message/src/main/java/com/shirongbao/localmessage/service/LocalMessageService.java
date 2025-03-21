package com.shirongbao.localmessage.service;

import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.shirongbao.localmessage.constant.BasicConstant;
import com.shirongbao.localmessage.constant.TaskStatusEnum;
import com.shirongbao.localmessage.dao.LocalMessageDao;
import com.shirongbao.localmessage.entity.InvokeCtx;
import com.shirongbao.localmessage.entity.LocalMessageDo;
import com.shirongbao.localmessage.utils.InvokeStatusHolder;
import com.shirongbao.localmessage.utils.LocalMessageThreadPool;
import com.shirongbao.localmessage.utils.TransactionUtils;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.util.Lists;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author: ShiRongbao
 * @date: 2025-03-17
 * @description:
 */
@Service("localMessageService")
@Slf4j
public class LocalMessageService implements ApplicationContextAware {

    @Resource
    private LocalMessageDao localMessageDao;

    @Resource
    private LocalMessageThreadPool localMessageThreadPool;

    private ApplicationContext applicationContext;

    private static final List<String> needRetryTaskStatus = Lists.newArrayList(
            TaskStatusEnum.INIT.getDesc(),
            TaskStatusEnum.RETRY.getDesc(),
            TaskStatusEnum.FAIL.getDesc()
    );

    // 每30秒执行一次
    @Scheduled(cron = "0/30 * * * * ?")
    public void compensation() {
        log.info("[local message] compensation start");
        List<LocalMessageDo> localMessageDos = loadWaitRetryRecords();
        localMessageDos.forEach(this::doAsyncInvoke);
    }

    private List<LocalMessageDo> loadWaitRetryRecords() {
        return localMessageDao.loadWaitRetryRecords(
                needRetryTaskStatus,
                System.currentTimeMillis(),
                // RETRY_INTERVAL_MINUTES,
                0 // for test
        );
    }

    public void invoke(LocalMessageDo localMessageDo, boolean async) {
        save(localMessageDo);
        boolean inTx = TransactionSynchronizationManager.isSynchronizationActive();
        if (inTx) {
            // 事物中，延迟到事物提交后再执行
            TransactionUtils.doAfterTransaction(() -> execute(localMessageDo, async));
        } else {
            // 非事物中，立即执行
            execute(localMessageDo, async);
        }
    }

    private void execute(LocalMessageDo localMessageDo, boolean async) {
        if (async) {
            doAsyncInvoke(localMessageDo);
        } else {
            doInvoke(localMessageDo);
        }
    }

    private void doInvoke(LocalMessageDo localMessageDo) {
        String snapshot = localMessageDo.getReqSnapshot();
        if (StrUtil.isBlank(snapshot)) {
            log.warn("Request snapshot is blank, recordId: {}", localMessageDo.getId());
            invokeFail(localMessageDo, "Request snapshot is blank");
            return;
        }

        InvokeCtx ctx = JSON.parseObject(snapshot, InvokeCtx.class);

        try {
            InvokeStatusHolder.startInvoke();

            Class<?> target = Class.forName(ctx.getClassName());
            Object bean = applicationContext.getBean(target);

            List<Class<?>> paramTypes = getParamTypes(JSON.parseArray(ctx.getParamTypes(), String.class));
            Method method = ReflectUtil.getMethod(target, ctx.getMethodName(), paramTypes.toArray(new Class[0]));
            Object[] args = getArgs(ctx.getArgs(), paramTypes);
            method.invoke(bean, args);
            invokeSuccess(localMessageDo);
        } catch (ClassNotFoundException e) {
            log.error("Class not found, className: {}, recordId: {}", ctx.getClassName(), localMessageDo.getId(), e);
            invokeFail(localMessageDo, e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("Illegal argument, className: {}, methodName: {}, args: {}, recordId: {}", ctx.getClassName(), ctx.getMethodName(), ctx.getArgs(), localMessageDo.getId(), e);
            invokeFail(localMessageDo, e.getMessage());
        } catch (Throwable e) {
            log.error("Invoke error, className: {}, methodName: {}, args: {}, recordId: {}", ctx.getClassName(), ctx.getMethodName(), ctx.getArgs(), localMessageDo.getId(), e);
            retry(localMessageDo, e.getMessage());
        } finally {
            InvokeStatusHolder.endInvoke();
        }
    }

    private void retry(LocalMessageDo localMessageDo, String errorMsg) {
        int retryTimes = localMessageDo.getRetryTimes() + 1;
        LocalMessageDo updateDo = new LocalMessageDo();
        updateDo.setId(localMessageDo.getId());
        updateDo.setFailReason(errorMsg);
        updateDo.setNextRetryTime(getNextRetryTime(retryTimes));
        if (retryTimes >= localMessageDo.getMaxRetryTimes()) {
            updateDo.setStatus(TaskStatusEnum.FAIL.getDesc());
        } else {
            updateDo.setRetryTimes(retryTimes);
            updateDo.setStatus(TaskStatusEnum.RETRY.getDesc());
        }
        updateDo.setRetryTimes(retryTimes);
        localMessageDao.updateById(updateDo);
    }

    private void invokeSuccess(LocalMessageDo localMessageDo) {
        localMessageDo.setStatus(TaskStatusEnum.SUCCESS.getDesc());
        localMessageDao.updateById(localMessageDo);
    }

    private void invokeFail(LocalMessageDo localMessageDo, String message) {
        localMessageDo.setStatus(TaskStatusEnum.FAIL.getDesc());
        localMessageDo.setFailReason(message);
        localMessageDao.updateById(localMessageDo);
    }

    private void doAsyncInvoke(LocalMessageDo localMessageDo) {
        localMessageThreadPool.execute(() -> doInvoke(localMessageDo));
    }

    private void save(LocalMessageDo localMessageDo) {
        localMessageDo.setStatus(TaskStatusEnum.INIT.getDesc());
        localMessageDo.setRetryTimes(0);
        localMessageDao.insert(localMessageDo);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    private long getNextRetryTime(int retryTimes) {
        return System.currentTimeMillis() + (retryTimes * BasicConstant.RETRY_INTERVAL_MINUTES * 60 * 1000L);
    }

    private List<Class<?>> getParamTypes(List<String> paramTypeNames) {
        return paramTypeNames.stream()
                .map(className -> {
                    try {
                        return Class.forName(className);
                    } catch (ClassNotFoundException e) {
                        log.error("Class not found: {}", className, e);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private Object[] getArgs(String argsJson, List<Class<?>> paramTypes) {
        return JSON.parseArray(argsJson, Object.class)
                .stream()
                .map(arg -> {
                    int index = JSON.parseArray(argsJson).indexOf(arg);
                    return JSON.parseObject(JSON.toJSONString(arg), paramTypes.get(index));
                })
                .toArray();
    }
}
