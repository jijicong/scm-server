package org.trc.lock;

import java.io.IOException;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.trc.service.BaseTest;
import org.trc.util.lock.Service;
import org.trc.util.lock.ThreadA;

public class RedisDistrbuteLockTest extends BaseTest {

    @Autowired
    private Service service;
    /*@Autowired
    private RedisDistrbuteLockUtil redisDistrbuteLockUtil;*/

    @Test
    public void lockTest() throws IOException, InterruptedException{
        for (int i = 0; i < 500; i++) {
            ThreadA threadA = new ThreadA(service);
            threadA.start();
            threadA.join();
            /*ThreadB threadB = new ThreadB(redisDistrbuteLockUtil);
            threadB.start();*/
        }
//        System.in.read();
    }

}
