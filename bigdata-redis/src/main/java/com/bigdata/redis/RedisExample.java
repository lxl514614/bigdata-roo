package com.bigdata.redis;

import redis.clients.jedis.Jedis;

/**
 * RedisExample
 *
 * @author Lee
 * @date 2018/4/2
 * description:
 * Created by Lee on 2018/4/2.
 */
public class RedisExample {

    private static Jedis jedis = null;

    public static Jedis getInstance() {

        if(null == jedis) {
            jedis = new Jedis("localhost", 6379);
        }

        return jedis;
    }


    public static void main(String[] args) {

        // 测试客户端是否联通.
//        String ping = getInstance().ping();
//        System.out.println(ping);

        // 设置String类型数据
//        String test_string = getInstance().set("TEST_STRING", "this is a String info");
//        System.out.println(test_string);

        // 获取String类型护具
        String value = getInstance().get("TEST_STRING");
        System.out.println(value);


    }

}
