package com.sysu.swzl.controller.weChat;

import com.alibaba.fastjson.JSONObject;
import com.sysu.swzl.common.R;
import com.sysu.swzl.config.weChat.WeChatProperties;
import com.sysu.swzl.service.WeChatService;
import io.jsonwebtoken.impl.Base64Codec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 49367
 * @date 2021/5/23 15:15
 */
@RestController
@RequestMapping(produces = {"application/json;charset=UTF-8"})
public class WeChatLoginController {

    @Autowired
    private WeChatService weChatService;

    @PostMapping("/login")
    public R weChatLogin(String code) {
        if (!StringUtils.hasText(code))
            return R.error("需要code");
        return weChatService.login(code);
    }

    @GetMapping("/hello")
    public String hello() {
        return "hello world";
    }
}
