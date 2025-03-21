package com.shirongbao.localmessage.utils;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author: ShiRongbao
 * @date: 2025-03-17
 * @description:
 */
@Component
public class LocalMessageThreadPool {

    private final ExecutorService executorService = Executors.newFixedThreadPool(10);

    public void execute(Runnable task) {
        executorService.execute(task);
    }

}
