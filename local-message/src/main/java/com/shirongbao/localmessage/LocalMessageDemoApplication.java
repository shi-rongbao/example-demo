package com.shirongbao.localmessage;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author: ShiRongbao
 * @date: 2025-03-17
 * @description:
 */
@SpringBootApplication
@MapperScan("com.shirongbao.localmessage.dao")
@EnableScheduling
public class LocalMessageDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(LocalMessageDemoApplication.class, args);
    }

}
