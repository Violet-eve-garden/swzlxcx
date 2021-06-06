package com.sysu.swzl.controller.weChat;

import com.alibaba.fastjson.JSONObject;
import com.sysu.swzl.common.R;
import com.sysu.swzl.pojo.WxUserInfo;
import com.sysu.swzl.service.WeChatService;
import com.sysu.swzl.validate.AddGroup;
import com.sysu.swzl.validate.UpdateGroup;
import com.sysu.swzl.vo.WxUserInfoVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 用户信息的Controller
 * @author 49367
 * @date 2021/5/24 21:50
 */
@RestController
@RequestMapping(path = "/my",  produces = {"application/json;charset=UTF-8"})
public class UserInfoController {

    @Autowired
    private WeChatService weChatService;

    @PostMapping("/searchInfo")
    public String searchInfo (String openId){
        if (!StringUtils.hasText(openId))
            return JSONObject.toJSONString(R.error("需要传openId"));

        WxUserInfo userInfo = weChatService.searchUserInfo(openId);
        if (userInfo == null)
            return "{}";

        return JSONObject.toJSONString(R.ok().put("user", userInfo));
    }

    @PostMapping("/addInfo")
    public R addInfo (@Validated({AddGroup.class}) WxUserInfo wxUserInfo){
        return weChatService.updateUserInfo(wxUserInfo);
    }

    @PostMapping("/updateInfo")
    public R updateInfo (@Validated(UpdateGroup.class) WxUserInfo wxUserInfo){
        return weChatService.updateUserInfo(wxUserInfo);
    }
}
