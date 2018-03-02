package org.trc.util;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

import java.lang.reflect.Method;


@Configuration
@EnableCaching
public class RedisCacheConfig extends CachingConfigurerSupport {

  private JedisConnectionFactory jedisConnectionFactory;
  private RedisTemplate redisTemplate;
  private CacheManager cacheManager;

  public RedisCacheConfig() {
    super();
  }

  public RedisCacheConfig(JedisConnectionFactory jedisConnectionFactory, RedisTemplate<String, String> redisTemplate, RedisCacheManager redisCacheManager) {
    super();
    this.jedisConnectionFactory = jedisConnectionFactory;
    this.redisTemplate = redisTemplate;
    this.cacheManager = redisCacheManager;
  }

  public JedisConnectionFactory redisConnectionFactory() {
    return jedisConnectionFactory;
  }

  public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory cf) {
    return redisTemplate;
  }

  public CacheManager cacheManager(RedisTemplate<?, ?> redisTemplate) {
    return cacheManager;
  }

  @Bean
  public KeyGenerator keyGenerator() {
    return new KeyGenerator() {
      @Override
      public Object generate(Object target, Method method,
                             Object... params) {
        //规定  本类名+方法名+参数名 为key
        StringBuilder sb = new StringBuilder();
        sb.append(target.getClass().getName()+"_");
        sb.append(method.getName()+"_");
        for (Object obj : params) {
          if(null != obj){
            if(obj instanceof Pagenation){
              Pagenation pagenation = (Pagenation)obj;
              sb.append(getPagenationKey(pagenation));
            }else {
              sb.append(obj.toString()+",");
            }
          }
        }
        return sb.toString();
      }
    };
  }

  private String getPagenationKey(Pagenation pagenation){
    StringBuilder sb = new StringBuilder();
    sb.append("Pagenation[");
    sb.append("start=").append(pagenation.getStart()).append(",");
    sb.append("pageSize=").append(pagenation.getPageSize()).append(",");
    sb.append("pageNo=").append(pagenation.getPageNo());
    sb.append("],");
    return sb.toString();
  }



}
