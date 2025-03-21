package com.shirongbao.localmessage.utils;

import org.springframework.transaction.support.TransactionSynchronization;

/**
 * @author: ShiRongbao
 * @date: 2025-03-17
 * @description:
 */
public class DoTransactionCompletion implements TransactionSynchronization {

    private Runnable runnable;

    public DoTransactionCompletion(Runnable runnable) {
        this.runnable = runnable;
    }

    @Override
    public void afterCompletion(int status) {
        if (status == TransactionSynchronization.STATUS_COMMITTED) {
            this.runnable.run();
        }
    }
}
