package org.trc.util.lock;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.trc.util.RedisDistrbuteLockUtil;

public class ThreadB extends Thread{

    private RedisDistrbuteLockUtil redisDistrbuteLockUtil;

    public ThreadB(RedisDistrbuteLockUtil redisDistrbuteLockUtil){
        this.redisDistrbuteLockUtil = redisDistrbuteLockUtil;
    }

    @Override
    public void run() {
        // 返回锁的value值，供释放锁时候进行判断
        String indentifier = redisDistrbuteLockUtil.lockWithTimeout("resource", 5000, 1000);
        System.out.println(Thread.currentThread().getName() + "获得了锁");
        if(StringUtils.isNotBlank(indentifier)){
            redisDistrbuteLockUtil.releaseLock("resource", indentifier);
        }
    }

}
