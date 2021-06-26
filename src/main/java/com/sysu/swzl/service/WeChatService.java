package com.sysu.swzl.service;

import com.sysu.swzl.common.R;
import com.sysu.swzl.pojo.WxUserInfo;
import com.sysu.swzl.vo.WxUserInfoVo;

/**
 * @author 49367
 * @date 2021/5/23 17:24
 */
public interface WeChatService {
    /**
     * 登录功能
     * @param code
     * @return
     */
    R login(String code);

    /**
     * 查询openid对应的用户信息
     * @param openid
     * @return
     */
    WxUserInfoVo getUserInfo(String openid);

    /**
     * 根据openId修改用户信息
     * @param wxUserInfo
     * @return
     */
    R updateUserInfo(WxUserInfo wxUserInfo);

    /**
     * 根据openid查询对应的用户信息
     * @param openId
     * @return
     */
    WxUserInfo searchUserInfo(String openId);

    /**
     * 设置权限信息
     * @param wxUserInfo
     */
    void setAuthorities(WxUserInfoVo wxUserInfo);

    /**
     * 根据openId校验该用户是否可以进行相关操作
     * @param openId
     * @return
     */
    boolean canPush(String openId);
}
