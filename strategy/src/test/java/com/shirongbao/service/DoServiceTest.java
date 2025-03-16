package com.shirongbao.service;

import com.shirongbao.StrategyApplication;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;


/**
 * @author: ShiRongbao
 * @date: 2025-03-16
 * @description:
 */
@SpringBootTest(classes = StrategyApplication.class)
class DoServiceTest {

    // @Spy
    @Mock
    private DoService doService;

    @BeforeEach
    void setUp() {
        AutoCloseable autoCloseable = MockitoAnnotations.openMocks(this);
        System.out.println(autoCloseable);
    }

    @Test
    void doSomething() {
        Mockito.when(doService.doSomething(1)).thenReturn(2);
        Assertions.assertEquals(2, doService.doSomething(1));
    }
}