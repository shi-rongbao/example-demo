package com.shirongbao.localmessage.entity;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * @author: ShiRongbao
 * @date: 2025-03-17
 * @description:
 */
@Data
@Builder
public class InvokeCtx {

    // 类名
    private String className;

    // 方法名
    private String methodName;

    // 参数类型
    private String paramTypes;

    // 参数
    private String args;
}
