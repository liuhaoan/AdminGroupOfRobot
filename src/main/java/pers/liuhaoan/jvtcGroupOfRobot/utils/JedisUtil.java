package pers.liuhaoan.jvtcGroupOfRobot.utils;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.io.IOException;
import java.util.Properties;

public class JedisUtil {
    private static JedisPool jedisePool;

    static {
        Properties jedis = new Properties();
        Properties global = new Properties();
        try {
            jedis.load(JedisUtil.class.getClassLoader().getResourceAsStream("jedis.properties"));
            global.load(JedisUtil.class.getClassLoader().getResourceAsStream("global.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        //获取数据，放到jedis的配置对象中
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxIdle(Integer.parseInt(jedis.getProperty("maxIdle")));
        jedisPoolConfig.setMaxTotal(Integer.parseInt(jedis.getProperty("maxTotal")));

        String host = global.getProperty("jedisHost");
        int port = Integer.parseInt(global.getProperty("jedisPort"));
        jedisePool = new JedisPool(jedisPoolConfig, host, port);
    }

    public static JedisPool getJedisePool() {
        return jedisePool;
    }

    public static Jedis getJedis() {
        try {
            return jedisePool.getResource();
        }catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
