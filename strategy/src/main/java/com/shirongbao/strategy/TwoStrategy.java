package com.shirongbao.strategy;

import com.shirongbao.constant.StrategyEnum;
import org.springframework.stereotype.Component;

/**
 * @author: ShiRongbao
 * @date: 2025-03-14
 * @description:
 */
@Component
public class TwoStrategy implements StrategyInterface{
    @Override
    public Integer getCode() {
        return StrategyEnum.TWO.getCode();
    }

    @Override
    public Integer doSomething() {
        System.out.println("策略2 doSomething");
        return StrategyEnum.TWO.getCode();
    }
}
