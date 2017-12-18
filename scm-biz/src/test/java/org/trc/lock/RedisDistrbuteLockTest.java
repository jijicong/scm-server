package org.trc.lock;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.trc.service.BaseTest;

public class RedisDistrbuteLockTest extends BaseTest {

    @Autowired
    private Service service;

    @Test
    public void lockTest(){
        for (int i = 0; i < 50; i++) {
            ThreadA threadA = new ThreadA(service);
            threadA.start();
        }
    }

}
