package com.sysu.swzl.constant;

/**
 * @author 49367
 * @date 2021/6/2 14:12
 */
public class MessageConstant {
    /**最新失物招领消息展示的数目*/
    public final static int LATEST_INFO_NUM = 10;
    /**最新失物招领消息的缓存前缀*/
    public final static String LATEST_INFO_REDIS_KEY = "message_info:latest";
    /**最新失物招领消息在redis中的过期时间，1小时*/
    public final static int LATEST_INFO_REDIS_EX = 60 * 60;
}
