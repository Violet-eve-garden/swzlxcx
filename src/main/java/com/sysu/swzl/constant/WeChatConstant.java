package com.sysu.swzl.constant;

/**
 * @author 49367
 * @date 2021/5/24 15:50
 */
public class WeChatConstant {
    /**一个openId一分钟内只允许调用登录接口五次*/
    public final static int LOGIN_LIMIT = 5;
    public final static int LOGIN_EXPIRATION = 60;
    public final static String OPENID_KEY = "wx:openid:";
    public static class WxJwtConstant{
        /**用来验证的key*/
        public final static String VERIFY_KEY = "id";
        /**缓存WxUserInfoVo的key*/
        public final static String USER_INFO_KEY = "user";
        /**微信token缓存前缀*/
        public static final String WX_TOKEN_CACHE_PREFIX = "wx:token:";
    }
}
