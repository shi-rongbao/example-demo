package com.shirongbao.localmessage.entity;

import lombok.Data;

/**
 * @author: ShiRongbao
 * @date: 2025-03-17
 * @description:
 */
@Data
public class LocalMessageDo {

    private Long id;

    private String reqSnapshot;

    private String failReason;

    private String context;

    private String status;

    private Integer retryTimes;

    private Integer maxRetryTimes;

    private Long nextRetryTime;

}
