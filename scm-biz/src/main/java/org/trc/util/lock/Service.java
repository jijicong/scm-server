package org.trc.util.lock;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.trc.framework.core.spring.SpringContextHolder;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import javax.annotation.PostConstruct;

/**
 * Created by liuyang on 2017/4/20.
 */
@Component
public class Service {

    private static JedisPool pool = null;

    static {
    	pool = (JedisPool) SpringContextHolder.getBean("jedisPool");
    }


    int n = 500;

    public void seckill() {
        DistributedLock lock = new DistributedLock(pool);
        // 返回锁的value值，供释放锁时候进行判断
        String indentifier = lock.lockWithTimeout("resource", 5000, 1000);
        System.out.println(Thread.currentThread().getName() + "获得了锁");
        System.out.println(--n);
        if(StringUtils.isNotBlank(indentifier)){
            lock.releaseLock("resource", indentifier);
        }
    }
}
