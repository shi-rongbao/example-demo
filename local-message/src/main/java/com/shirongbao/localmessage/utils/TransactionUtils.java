package com.shirongbao.localmessage.utils;

import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * @author: ShiRongbao
 * @date: 2025-03-17
 * @description:
 */
public class TransactionUtils {

    public static void doAfterTransaction(Runnable action) {
        if (TransactionSynchronizationManager.isActualTransactionActive()) {
            TransactionSynchronizationManager.registerSynchronization(new DoTransactionCompletion(action) {
            });
        }
    }
}
