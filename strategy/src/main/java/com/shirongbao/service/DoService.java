package com.shirongbao.service;

import com.shirongbao.strategy.StrategyFactory;
import com.shirongbao.strategy.StrategyInterface;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author: ShiRongbao
 * @date: 2025-03-14
 * @description:
 */
@Service("doService")
public class DoService {

    @Resource
    private StrategyFactory strategyFactory;

    public Integer doSomething(Integer typeCode) {
        System.out.println("执行了很多东西");

        StrategyInterface strategy = strategyFactory.getStrategy(typeCode);
        return strategy.doSomething();
    }

}
