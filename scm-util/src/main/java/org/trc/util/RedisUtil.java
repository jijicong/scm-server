package org.trc.util;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.trc.framework.core.spring.SpringContextHolder;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisConnectionException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.SocketTimeoutException;
import java.util.List;
import java.util.Set;

/**
 * @author huyan
 * @description
 * @time 2016/12/7
 * @modifier
 */
public class RedisUtil {

	private static JedisPool jedisPool;
	private static Logger logger = LoggerFactory.getLogger(RedisUtil.class);

	static {
		jedisPool = (JedisPool) SpringContextHolder.getBean("jedisPool");
	}

	public static Jedis getJedis() {
		return jedisPool.getResource();
	}

	public static boolean exists(String key){
		Jedis jedis = null;
		try{
			jedis = jedisPool.getResource();
			if (StringUtils.isNotEmpty(key)) {
				return jedis.exists(key);
			}
		}catch (Exception e){
			logger.error("RedisUtil调用exists异常!",e);
		}finally {
			jedisPool.returnResource(jedis);
		}
		return false;
	}

	public static boolean delObject(String key) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			if (StringUtils.isNotEmpty(key)) {
				jedis.del(key.getBytes());
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			returnResource(jedis);
		}
		return false;
	}

	public static boolean setObject(String key, Object obj, int expireTime) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			if (StringUtils.isNotEmpty(key)) {
				jedis.setex(key.getBytes(), expireTime, serialize(obj));
				return true;
			}
		} catch (Exception e) {
			logger.error("添加缓存对象失败", e);
		} finally {
			returnResource(jedis);
		}
		return false;
	}

	public static Object getObject(String key) throws RedisConnectionFailureException {
		byte[] result = null;
		Jedis jedis = null;
		Object obj = null;
		try {
			jedis = jedisPool.getResource();
			if (StringUtils.isNotEmpty(key)) {
				result = jedis.get(key.getBytes());
				if (result != null) {
					obj = unserizlize(result);
				}
			}
		}catch (Exception e) {
			if (e instanceof JedisConnectionException || e instanceof SocketTimeoutException)
			{
				throw new RedisConnectionFailureException("redis连接超时异常");
			}
			logger.error("获取缓存对象失败", e);
		} finally {
			returnResource(jedis);
		}
		return obj;
	}

	public static boolean hset(String key, String field, Object obj, int expire) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			if (StringUtils.isNotEmpty(key)) {
				boolean exist = jedis.exists(key.getBytes());
				jedis.hset(key.getBytes(), field.getBytes(), serialize(obj));
				if (!exist) {
					jedis.expire(key.getBytes(), expire);
				}
				return true;
			}
		} catch (Exception e) {
			logger.error("添加缓存对象失败", e);
		} finally {
			returnResource(jedis);
		}
		return false;
	}

	public static Object hget(String key, String field) {
		Object obj = null;
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			if (StringUtils.isNotEmpty(key)) {
				byte[] result = jedis.hget(key.getBytes(), field.getBytes());
				if(result != null){
					obj = unserizlize(result);
				}
				// if(obj instanceof T){
				//
				// }
			}
		} catch (Exception e) {
			logger.error("获取缓存对象失败", e);
		} finally {
			returnResource(jedis);
		}
		return obj;
	}

	public static boolean set(String key, String value) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			if (StringUtils.isNotEmpty(key)) {
				jedis.set(key, value);
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			returnResource(jedis);
		}
		return false;
	}

	public static String get(String key){
		String result = null;
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			if (StringUtils.isNotEmpty(key)) {
				result = jedis.get(key);
			}
		}catch (Exception e) {
			e.printStackTrace();
		} finally {
			returnResource(jedis);
		}
		return result;
	}

	public static boolean del(String key) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			if (StringUtils.isNotEmpty(key)) {
				jedis.del(key);
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			returnResource(jedis);
		}
		return false;
	}

	public static boolean sadd(String key, String value) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			if (StringUtils.isNotEmpty(key)) {
				jedis.sadd(key, value);
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			returnResource(jedis);
		}
		return false;
	}

	public static Long scard(String key) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			if (StringUtils.isNotEmpty(key)) {
				return jedis.scard(key);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			returnResource(jedis);
		}
		return 0L;
	}

	public static boolean sismember(String key, String member) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			if (StringUtils.isNotEmpty(key) && StringUtils.isNotEmpty(member)) {
				return jedis.sismember(key, member);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			returnResource(jedis);
		}
		return false;
	}

	public static Set<String> smembers(String key) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			if (StringUtils.isNotEmpty(key)) {
				return jedis.smembers(key);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			returnResource(jedis);
		}
		return null;
	}

	public static String spop(String key) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			if (StringUtils.isNotEmpty(key)) {
				return jedis.spop(key);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			returnResource(jedis);
		}
		return null;
	}

	public static void lpush(String key, String[] values) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			if (StringUtils.isNotEmpty(key)) {
				jedis.lpush(key, values);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			returnResource(jedis);
		}
	}

	/**
	 * 返回列表 key 中指定区间内的元素，区间以偏移量 start 和 stop 指定
	 *
	 * @param key
	 * @param start
	 * @param stop
	 * @return
	 */
	public static List<String> lrange(String key, int start, int stop) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			if (StringUtils.isNotEmpty(key)) {
				return jedis.lrange(key, start, stop);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			returnResource(jedis);
		}
		return null;
	}

	public static String srandmember(String key) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			if (StringUtils.isNotEmpty(key)) {
				return jedis.srandmember(key);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			returnResource(jedis);
		}
		return null;
	}

	/**
	 * 将 key 中储存的数字值增一
	 */
	public static long incr(String key) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			if (StringUtils.isNotEmpty(key)) {
				return jedis.incr(key);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			returnResource(jedis);
		}
		return 0;
	}

	/**
	 * 将key 中储存的数字值增N
	 */
	public static long incrBy(String key, int count) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.incrBy(key, count);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			returnResource(jedis);
		}
		return 0;
	}

	/**
	 * 将 key 中储存的数字值减一
	 */
	public static long decr(String key) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			if (StringUtils.isNotEmpty(key)) {
				return jedis.decr(key);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			returnResource(jedis);
		}
		return 0;
	}

	/**
	 * 将key 中储存的数字值减N
	 */
	public static long decrBy(String key, int count) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.decrBy(key, count);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			returnResource(jedis);
		}
		return 0;
	}

	// 返回到连接池
	protected static void returnResource(Jedis jedis) {
		if (jedis != null) {
			try {
				jedisPool.returnResource(jedis);
			} catch (Exception e) {
				logger.error("returnResource exception:", e);
			}
		}
	}

	// 序列化
	public static byte[] serialize(Object object) {
		ObjectOutputStream oos = null;
		ByteArrayOutputStream baos = null;
		try {
			// 序列化
			baos = new ByteArrayOutputStream();
			oos = new ObjectOutputStream(baos);
			oos.writeObject(object);
			byte[] bytes = baos.toByteArray();
			return bytes;
		} catch (Exception e) {
			logger.error("序列化失败", e);
		}
		return null;
	}

	// 反序列化
	public static Object unserizlize(byte[] byt) {
		ObjectInputStream oii = null;
		ByteArrayInputStream bis = null;
		bis = new ByteArrayInputStream(byt);
		try {
			oii = new ObjectInputStream(bis);
			Object obj = oii.readObject();
			return obj;
		} catch (Exception e) {
			logger.error("反序列化失败", e);
		}
		return null;
	}

	public static void main(String[] args) {

		String goodsCode = "E07AA42A098F463DB9F0BDBF38C7D9AFsss";
		try {
			System.out.println(get(goodsCode));
		} catch (Exception e) {
			e.printStackTrace();
		}
		long s = RedisUtil.incrBy(goodsCode, 10);
		System.out.println(s);
	}
}
