package com.yuncj.redis;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import evan.wang.util.RedisUtil;
import redis.clients.jedis.Jedis;

public class TestJedis {
	private Jedis jedis;

	@Before
	public void setup() {
		jedis = new Jedis("127.0.0.1", 6379);
		jedis.auth("123456"); // 权限认证
	}

	@Test
	public void redisInfo() {
		String res = jedis.info();
		System.out.println(res);
	}

	/**
	 * redis存储字符串
	 */
	@Test
	public void testString() {
		// -----添加数据----------
		jedis.set("name", "xinxin");// 向key-->name中放入了value-->xinxin
		System.out.println(jedis.get("name"));// 执行结果：xinxin

		jedis.append("name", " is my lover"); // 拼接
		System.out.println(jedis.get("name"));

		jedis.del("name"); // 删除某个键
		System.out.println(jedis.get("name"));
		// 设置多个键值对
		jedis.mset("name", "liuling", "age", "23", "qq", "476777XXX");
		jedis.incr("age"); // 进行加1操作
		System.out.println(jedis.get("name") + "-" + jedis.get("age") + "-"
				+ jedis.get("qq"));
	}

	/**
	 * redis操作Map
	 */
	@Test
	public void testMap() {
		// -----添加数据----------
		Map<String, String> map = new HashMap<String, String>();
		map.put("name", "xinxin");
		map.put("age", "22");
		map.put("qq", "123456");
		jedis.hmset("user", map);
		// 取出user中的name，执行结果:[minxr]-->注意结果是一个泛型的List
		// 第一个参数是存入redis中map对象的key，后面跟的是放入map中的对象的key，后面的key可以跟多个，是可变参数
		List<String> rsmap = jedis.hmget("user", "name", "age", "qq");
		System.out.println(rsmap);

		// 删除map中的某个键值
		jedis.hdel("user", "age");
		System.out.println(jedis.hmget("user", "age")); // 因为删除了，所以返回的是null
		System.out.println(jedis.hlen("user")); // 返回key为user的键中存放的值的个数2
		System.out.println(jedis.exists("user"));// 是否存在key为user的记录 返回true
		System.out.println(jedis.hexists("user", "age"));// 是否存在key为user,
															// field为age的记录，返回false

		System.out.println(jedis.hkeys("user"));// 返回map对象中的所有key
		System.out.println(jedis.hvals("user"));// 返回map对象中的所有value

		Iterator<String> iter = jedis.hkeys("user").iterator();
		while (iter.hasNext()) {
			String key = iter.next();
			System.out.println(key + ":" + jedis.hmget("user", key));
		}
	}

	/**
	 * jedis操作List
	 */
	@Test
	public void testList() {
		// 开始前，先移除所有的内容
		jedis.del("java framework");
		// 返回列表java framework中指定区间内的元素, 0表示第一个元素，-1表示最后一个
		System.out.println(jedis.lrange("java framework", 0, -1));
		// 先向key java framework中存放三条数据
		jedis.lpush("java framework", "spring");
		jedis.lpush("java framework", "struts");
		jedis.lpush("java framework", "hibernate");
		// 返回列表的长度
		System.out.println("list length: " + jedis.llen("java framework"));

		// 再取出所有数据jedis.lrange是按范围取出，
		// 第一个是key，第二个是起始位置，第三个是结束位置-1表示取得所有
		System.out.println(jedis.lrange("java framework", 0, -1));

		jedis.del("java framework");
		jedis.rpush("java framework", "spring");
		jedis.rpush("java framework", "struts");
		jedis.rpush("java framework", "hibernate");
		System.out.println(jedis.lrange("java framework", 0, -1));
	}

	/**
	 * jedis操作Set
	 */
	@Test
	public void testSet() {
		// 添加
		jedis.sadd("username", "liuling");
		jedis.sadd("username", "liuling"); // 添加重复元素，失败。
		jedis.sadd("username", "xinxin");
		jedis.sadd("username", "ling");
		jedis.sadd("username", "zhangxinxin");
		jedis.sadd("username", "who");
		// 移除who元素
		jedis.srem("username", "who");
		System.out.println(jedis.smembers("username"));// 获取所有加入的value
		System.out.println(jedis.sismember("username", "who"));// 判断 who
																// 是否是user集合的元素
		System.out.println(jedis.srandmember("username")); // 从Set中随机获取一个元素
		System.out.println(jedis.scard("username"));// 返回集合的元素个数
	}

	/**
	 * jedis操作sortedSet
	 */
	@Test
	public void testSortedSet() {
		// 添加, 第二个参数为在集合中的顺序
		jedis.zadd("zname", 4, "liuling");
		jedis.zadd("zname", 5, "liuling"); // 添加重复元素，失败。
		jedis.zadd("zname", 8, "xinxin");
		jedis.zadd("zname", 7, "ling");
		jedis.zadd("zname", 6, "zhangxinxin");
		jedis.zadd("zname", 2, "1000");
		jedis.zadd("zname", 3, "2000");
		jedis.zadd("zname", 1, "5000");
		jedis.zadd("zname", 9, "who");

		System.out.println(jedis.zcard("zname"));// 返回集合的元素个数
		System.out.println(jedis.zrange("zname", 0, -1));// 获取所有加入的value,
															// 按score进行排序

	}

	@Test
	public void test() throws InterruptedException {
		// jedis 排序
		// 注意，此处的rpush和lpush是List的操作。是一个双向链表（但从表现来看的）
		jedis.del("a");// 先清除数据，再加入数据进行测试
		jedis.rpush("a", "1");
		jedis.lpush("a", "6");
		jedis.lpush("a", "3");
		jedis.lpush("a", "9");
		System.out.println(jedis.lrange("a", 0, -1));// [9, 3, 6, 1]
		System.out.println(jedis.sort("a")); // [1, 3, 6, 9] //输入排序后结果
		System.out.println(jedis.lrange("a", 0, -1));
		jedis.del("a");
	}

	@Test
	public void testRedisPool() {
		RedisUtil.getJedis().set("newname", "中文测试");
		System.out.println(RedisUtil.getJedis().get("newname"));
	}

	@After
	public void after() {
		jedis.close();
	}

}
