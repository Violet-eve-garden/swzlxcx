package com.sysu.swzl.service.Impl;

import com.alibaba.fastjson.JSONObject;
import com.sysu.swzl.common.R;
import com.sysu.swzl.config.weChat.WeChatProperties;
import com.sysu.swzl.constant.WeChatConstant;
import com.sysu.swzl.dao.WxUserInfoMapper;
import com.sysu.swzl.exception.BizCodeException;
import com.sysu.swzl.pojo.WxUserInfo;
import com.sysu.swzl.service.WeChatService;
import com.sysu.swzl.utils.JwtUtil;
import com.sysu.swzl.vo.WxAccountResponseVo;
import com.sysu.swzl.vo.WxUserInfoVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author 49367
 * @date 2021/5/23 17:24
 */
@Service
@Slf4j
public class WeChatServiceImpl implements WeChatService {
    @Autowired
    private WeChatProperties weChatProperties;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private WxUserInfoMapper wxUserInfoMapper;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 登录功能的实现
     * @param code
     * @return
     */
    @Override
    @Transactional
    public R login(String code) {
        String url = weChatProperties.getCode2SessionUrl();
        Map<String, String> requestMap = new HashMap<>();
        requestMap.put("appid", weChatProperties.getAppid());
        requestMap.put("secret", weChatProperties.getSecret());
        requestMap.put("code", code);

        // 调用"https://api.weixin.qq.com/sns/jscode2session?appid={appid}&secret={secret}&js_code={code}&grant_type=authorization_code"
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(url, String.class, requestMap);
        WxAccountResponseVo respVo = JSONObject.parseObject(responseEntity.getBody(), WxAccountResponseVo.class);
        if (respVo == null || respVo.getErrcode() != null)
            return respVo == null ? R.error() : R.error(respVo.getErrcode(), respVo.getErrmsg());

        // 生成token，存用户信息到缓存中
        String token = jwtUtil.generateTokenForWxAccount(respVo.getOpenid(), respVo.getSession_key());
        // 先根据openid，查询近期登录次数，若次数在允许范围内，则查数据库有没有该用户信息，没有则添加该用户到数据库
        String openid = respVo.getOpenid();
        String openidKey = WeChatConstant.OPENID_KEY + openid;
        String countStr = redisTemplate.opsForValue().get(openidKey);
        if (countStr != null && Integer.parseInt(countStr) >= WeChatConstant.LOGIN_LIMIT) {
            redisTemplate.expire(openidKey, WeChatConstant.LOGIN_EXPIRATION, TimeUnit.SECONDS);
            log.debug("该openId: " + openidKey + " 的登录次数为" + countStr);
            return R.error(HttpStatus.FORBIDDEN.value(), "请求登录次数过多");
        }

        WxUserInfo wxUserInfo = wxUserInfoMapper.selectByOpenId(openid);
        System.out.println(openid);
        if (wxUserInfo == null){
            wxUserInfo = new WxUserInfoVo();
            wxUserInfo.setOpenId(openid);
            wxUserInfo.setType((byte) 0);
            wxUserInfoMapper.insertSelective(wxUserInfo);
        }
        // 保存到与token相对应的hash里
        redisTemplate.opsForHash().put(WeChatConstant.WxJwtConstant.WX_TOKEN_CACHE_PREFIX + token,
                WeChatConstant.WxJwtConstant.USER_INFO_KEY, JSONObject.toJSONString(wxUserInfo));
        // 该openid调用登录接口的次数加1
        redisTemplate.opsForValue().increment(openidKey);
        redisTemplate.expire(openidKey, WeChatConstant.LOGIN_EXPIRATION, TimeUnit.SECONDS);
        log.debug("登录的openId为 " + openid);
        return R.ok().put("openId", openid).put("token", token);
    }


    /**
     * 获取用户信息
     * @param openid
     * @return
     */
    @Override
    public WxUserInfoVo getUserInfo(String openid) {
        WxUserInfoVo wxUserInfoVo = new WxUserInfoVo();
        WxUserInfo wxUserInfo = wxUserInfoMapper.selectByOpenId(openid);
        // 如果没有nickname则表明该用户还没添加过自己的信息
        if (wxUserInfo == null || !StringUtils.hasText(wxUserInfo.getNickName()))
            return null;

        BeanUtils.copyProperties(wxUserInfo, wxUserInfoVo);
        setAuthorities(wxUserInfoVo);
        return wxUserInfoVo;
    }

    /**
     * 修改用户信息
     * @param wxUserInfo
     * @return
     */
    @Override
    @Transactional
    public R updateUserInfo(WxUserInfo wxUserInfo) {
        if (!verifyUserInfo(wxUserInfo))
            return R.error("数据不合法").put("isAddSuccess", false);

        wxUserInfoMapper.updateByOpenIdSelective(wxUserInfo);
        return R.ok().put("isAddSuccess", true);
    }

    @Override
    public WxUserInfo searchUserInfo(String openId) {
        return wxUserInfoMapper.selectByOpenId(openId);
    }

    /**
     * 根据openId校验用户信息，可用返回true，否则返回false。
     * @param openId
     * @return
     */
    @Override
    public boolean canPush(String openId) {
        if (!StringUtils.hasText(openId))
            return false;

        WxUserInfoVo userInfo = getUserInfo(openId);
        if (userInfo == null)
            return false;
        return StringUtils.hasText(userInfo.getNickName()) && verifyUserInfo(userInfo);
    }

    /**
     * 校验user数据，
     *      qq, weixin, phone, other至少有一项不为空
     * @param wxUserInfo
     * @return
     */
    private boolean verifyUserInfo(WxUserInfo wxUserInfo){
        String qq = wxUserInfo.getQq();
        String phone = wxUserInfo.getPhone();
        String weixin = wxUserInfo.getWeixin();
        String other = wxUserInfo.getOther();

        return StringUtils.hasText(qq) || StringUtils.hasText(phone)
                || StringUtils.hasText(weixin) || StringUtils.hasText(other);
    }


    /**
     * 设置相应的权限信息
     * @param wxUserInfo
     */
    public void setAuthorities(WxUserInfoVo wxUserInfo) {
        HashSet<GrantedAuthority> authorities = new HashSet<>();
        Byte type = wxUserInfo.getType();
        if (type.intValue() == 0)
            authorities.add(new SimpleGrantedAuthority("common"));
        else if (type.intValue() == 1) {
            authorities.add(new SimpleGrantedAuthority("common"));
            authorities.add(new SimpleGrantedAuthority("admin"));
        }
        log.info(authorities.toString());
        wxUserInfo.setAuthorities(authorities);
    }


}
