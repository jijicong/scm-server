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

    @Value("${mall.redis.host}")
    private String host;
    @Value("${mall.redis.port}")
    private Integer port;
    @Value("${redis.pool.maxTotal}")
    private Integer maxToal;
    @Value("${redis.pool.maxIdle}")
    private Integer maxIdle;
    @Value("${mall.redis.timeout}")
    private Integer timeout;
    @Value("${redis.pool.testOnBorrow}")
    private Boolean testOnBorrow;
    @Value("${mall.redis.password}")
    private String password;
    @Value("${mall.redis.database}")
    private Integer database;


    @PostConstruct
    public void init(){
        JedisPoolConfig config = new JedisPoolConfig();
        // 设置最大连接数
        config.setMaxTotal(maxToal);
        // 设置最大空闲数
        config.setMaxIdle(maxIdle);
        // 设置最大等待时间
        config.setMaxWaitMillis(timeout);
        // 在borrow一个jedis实例时，是否需要验证，若为true，则所有jedis实例均是可用的
        config.setTestOnBorrow(true);
        pool = new JedisPool(config, host, port, timeout, password, database);
    }

   /* @Autowired
    private RedisDistrbuteLockUtil redisDistrbuteLockUtil;*/

    /*static {
        jedisPool = (JedisPool) SpringContextHolder.getBean("jedisPool");
    }

    DistributedLock lock = new DistributedLock(jedisPool);*/

    int n = 500;

    /*public void seckill() {
        // 返回锁的value值，供释放锁时候进行判断
        String indentifier = redisDistrbuteLockUtil.lockWithTimeout("resource", 5000, 1000);
        System.out.println(Thread.currentThread().getName() + "获得了锁");
        System.out.println(--n);
        if(StringUtils.isNotBlank(indentifier)){
            redisDistrbuteLockUtil.releaseLock("resource", indentifier);
        }
    }*/
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
