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
    /**最新失物招领消息在redis中的过期时间，24小时*/
    public final static int LATEST_INFO_REDIS_EX = 24 * 60 * 60;
    /**消息的初始状态，表示未被认领或未找到*/
    public final static Integer INIT_STATE = 0;
    /**表示被认领或被找到*/
    public final static Integer CHANGED_STATE = 1;
}
