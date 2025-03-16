package com.shirongbao.strategy;

import com.shirongbao.constant.StrategyEnum;
import org.springframework.stereotype.Component;

/**
 * @author: ShiRongbao
 * @date: 2025-03-14
 * @description:
 */
@Component
public class OneStrategy implements StrategyInterface{
    @Override
    public Integer getCode() {
        return StrategyEnum.ONE.getCode();
    }

    @Override
    public Integer doSomething() {
        System.out.println("策略1 do something");
        return StrategyEnum.ONE.getCode();
    }
}
