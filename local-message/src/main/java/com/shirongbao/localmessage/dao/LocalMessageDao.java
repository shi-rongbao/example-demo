package com.shirongbao.localmessage.dao;

import com.shirongbao.localmessage.entity.LocalMessageDo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author: ShiRongbao
 * @date: 2025-03-17
 * @description:
 */
public interface LocalMessageDao {

    List<LocalMessageDo> loadWaitRetryRecords(
            @Param("status") List<String> status,
            @Param("nextRetryTime") Long nextRetryTime,
            @Param("retryIntervalMinutes") Integer retryIntervalMinutes
    );

    void updateById(@Param("updateDo") LocalMessageDo updateDo);

    void insert(@Param("localMessageDo") LocalMessageDo localMessageDo);
}
