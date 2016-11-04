package evan.wang.util;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisUtil {
	
	 //Redis服务器IP
    private static String ADDR = "127.0.0.1";
    
    //Redis的端口号
    private static int PORT = 6379;
    
    //访问密码
    private static String AUTH = "123456";
    
    //可用连接实例的最大数目，默认值为8；
    //如果赋值为-1，则表示不限制；如果pool已经分配了maxActive个jedis实例，则此时pool的状态为exhausted(耗尽)。
    private static int MAX_ACTIVE = 1024;
    
    //控制一个pool最多有多少个状态为idle(空闲的)的jedis实例，默认值也是8。
    private static int MAX_IDLE = 200;
    
    //等待可用连接的最大时间，单位毫秒，默认值为-1，表示永不超时。如果超过等待时间，则直接抛出JedisConnectionException；
    private static int MAX_WAIT = 10000;
    
    private static int TIMEOUT = 10000;
    
    //在borrow一个jedis实例时，是否提前进行validate操作；如果为true，则得到的jedis实例均是可用的；
    private static boolean TEST_ON_BORROW = true;
    
    private static JedisPool jedisPool = null;
    
    /**
     * 初始化Redis连接池
     */
    static {
        try {
        	//读取参数配置文件
        	initConfig();
            JedisPoolConfig config = new JedisPoolConfig();
            config.setMaxTotal(MAX_ACTIVE);
            config.setMaxIdle(MAX_IDLE);
            config.setMaxWaitMillis(MAX_WAIT);
            config.setTestOnBorrow(TEST_ON_BORROW);
            jedisPool = new JedisPool(config, ADDR, PORT, TIMEOUT, AUTH);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void initConfig(){
    	if(PropertiesUtil.getValue("redis.host")!=null){
    		ADDR = PropertiesUtil.getValue("redis.host");
    	}
    	if(PropertiesUtil.getValue("redis.port")!=null){
    		PORT = Integer.parseInt(PropertiesUtil.getValue("redis.port"));
    	}
    	if(PropertiesUtil.getValue("redis.password")!=null){
    		AUTH = PropertiesUtil.getValue("redis.password");
    	}
    	if(PropertiesUtil.getValue("redis.maxIdle")!=null){
    		MAX_IDLE = Integer.parseInt(PropertiesUtil.getValue("redis.maxIdle"));
    	}
    	if(PropertiesUtil.getValue("redis.maxActive")!=null){
    		MAX_ACTIVE = Integer.parseInt(PropertiesUtil.getValue("redis.maxActive"));
    	}
    	if(PropertiesUtil.getValue("redis.maxWait")!=null){
    		TIMEOUT = Integer.parseInt(PropertiesUtil.getValue("redis.maxWait"));
    	}
    	if(PropertiesUtil.getValue("redis.testOnBorrow")!=null){
    		TEST_ON_BORROW = Boolean.parseBoolean(PropertiesUtil.getValue("redis.testOnBorrow"));
    	}
    }
    
    /**
     * 获取Jedis实例
     * @return
     */
    public synchronized static Jedis getJedis() {
        try {
            if (jedisPool != null) {
                Jedis resource = jedisPool.getResource();
                return resource;
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * 释放jedis资源
     * @param jedis
     */
    public static void returnResource(final Jedis jedis) {
        if (jedis != null) {
        	jedis.close();
        }
    }


}
