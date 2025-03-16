package com.shirongbao.strategy;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: ShiRongbao
 * @date: 2025-03-14
 * @description:
 */
@Component
public class StrategyFactory implements InitializingBean {

    Map<Integer, StrategyInterface> strategyInterfaceMap = new HashMap<>();

    @Resource
    List<StrategyInterface> strategyInterfaces;

    public StrategyInterface getStrategy(Integer code) {
        return strategyInterfaceMap.get(code);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        for (StrategyInterface strategyInterface : strategyInterfaces) {
            strategyInterfaceMap.put(strategyInterface.getCode(), strategyInterface);
        }
    }

}
