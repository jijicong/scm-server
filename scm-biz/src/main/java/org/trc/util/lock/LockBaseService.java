package org.trc.util.lock;

import org.trc.framework.core.spring.SpringContextHolder;
import redis.clients.jedis.JedisPool;

public abstract class LockBaseService {

    public static JedisPool pool = null;

    static {
        pool = (JedisPool) SpringContextHolder.getBean("jedisPool");
    }

    abstract void updateStock();

}
