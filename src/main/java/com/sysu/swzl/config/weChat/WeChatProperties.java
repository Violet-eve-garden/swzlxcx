package com.sysu.swzl.config.weChat;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author 49367
 * @date 2021/5/23 15:28
 */
@Data
@Component
@ConfigurationProperties(prefix = "wechat")
public class WeChatProperties {
    private String appid;
    private String secret;
    private String code2SessionUrl;
}
