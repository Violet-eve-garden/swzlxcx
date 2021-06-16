package com.sysu.swzl.filter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sysu.swzl.common.R;
import com.sysu.swzl.constant.WeChatConstant;
import com.sysu.swzl.pojo.WxUserInfo;
import com.sysu.swzl.service.WeChatService;
import com.sysu.swzl.utils.JwtUtil;
import com.sysu.swzl.vo.WxUserInfoVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;



/**
 * @author 49367
 * @date 2021/5/23 18:52
 */
@Component
@Slf4j
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private WeChatService weChatService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        //获取header中的token信息
        String authToken = request.getHeader(JwtUtil.AUTHORIZATION);
        log.info("token: " + authToken);

        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");
        /*
            token为空或者格式不正确直接进行过滤操作，
            这种情况下可以访问permitAll的url，
            访问需要权限的会被拦截
         */
        if (null == authToken || !authToken.startsWith(JwtUtil.TOKEN_PREFIX)) {
            // log.info("token格式不正确");
            filterChain.doFilter(request, response);
            return;
        }

        // 校验token (如果token合法会自动续期)
        if (!jwtUtil.weChatVerifyToken(authToken)) {
            response.getWriter().write(JSON.toJSONString(R.error(HttpStatus.FORBIDDEN.value(), "token过期")));
            log.info("token过期");
            return;
        }

        // 检验请求体里的openId和请求头token对应的openId是否一致
        String openId = request.getParameter("openId");
        String openIdFromToken = jwtUtil.getOpenIdFromToken(authToken);

        if (openId == null || !openId.equals(openIdFromToken)){
            response.getWriter().write(JSON.toJSONString(R.error(HttpStatus.FORBIDDEN.value(), "token缺失")));
            log.info("token缺失");
            return;
        }

        // token合法，获取相应的user信息
        BoundHashOperations<String, String, String> ops = redisTemplate.boundHashOps(WeChatConstant.WxJwtConstant.WX_TOKEN_CACHE_PREFIX + authToken);
        WxUserInfoVo user = JSONObject.parseObject(ops.get(WeChatConstant.WxJwtConstant.USER_INFO_KEY), WxUserInfoVo.class);
        if (user != null)
            weChatService.setAuthorities(user);
            // log.info(user.toString());
        if (user == null){
            // 获取权限信息
            user = weChatService.getUserInfo(ops.get(WeChatConstant.WxJwtConstant.VERIFY_KEY));
            if (user == null) {
                response.getWriter().write(JSON.toJSONString(R.error("查询数据库出错")));
                return;
            }

            ops.put(WeChatConstant.WxJwtConstant.USER_INFO_KEY, JSONObject.toJSONString(user));
        }

        log.info("openid: " + user.getOpenId() + " Authorities: " + user.getAuthorities());

        // 将信息交给security
        // 不是使用密码登录，因此credentials为null
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        filterChain.doFilter(request, response);
    }

}