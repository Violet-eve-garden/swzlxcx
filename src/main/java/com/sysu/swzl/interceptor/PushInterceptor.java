package com.sysu.swzl.interceptor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sysu.swzl.common.R;
import com.sysu.swzl.exception.BizCodeException;
import com.sysu.swzl.service.WeChatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.CharsetEncoder;

/**
 * @author 49367
 * @date 2021/6/22 12:41
 */
@Component
@Slf4j
public class PushInterceptor implements HandlerInterceptor {
    @Autowired
    private WeChatService weChatService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/json;charset=UTF-8");

        String openId = request.getParameter("openId");
        boolean flag = weChatService.canPush(openId);

        if (!flag) {
            response.setStatus(HttpStatus.FORBIDDEN.value());
            response.getWriter().write(JSONObject.toJSONString(R.error(BizCodeException.USER_INFO_EXCEPTION.getCode(),
                    BizCodeException.USER_INFO_EXCEPTION.getMessage())));
            return false;
        }

        return true;
    }
}
