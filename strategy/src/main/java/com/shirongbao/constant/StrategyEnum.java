package com.shirongbao.constant;

import lombok.Getter;

/**
 * @author: ShiRongbao
 * @date: 2025-03-14
 * @description:
 */
@Getter
public enum StrategyEnum {

    ONE(1, "策略1"),
    TWO(2, "策略2");

    final Integer code;

    final String desc;

    StrategyEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static StrategyEnum getStrategyEnum(Integer code) {
        for (StrategyEnum strategyEnum : StrategyEnum.values()) {
            if (strategyEnum.getCode().equals(code)) {
                return strategyEnum;
            }
        }
        return null;
    }


}
