package com.shirongbao.localmessage.constant;

import lombok.Getter;

/**
 * @author: ShiRongbao
 * @date: 2025-03-17
 * @description:
 */
@Getter
public enum TaskStatusEnum {

    INIT(0, "初始化"),
    SUCCESS(1, "成功"),
    FAIL(2, "失败"),
    RETRY(3, "重试"),
    ;

    private final Integer code;
    private final String desc;

    TaskStatusEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

}
