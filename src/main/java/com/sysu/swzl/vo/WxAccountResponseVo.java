package com.sysu.swzl.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author 49367
 * @date 2021/5/23 16:49
 */
@Data
public class WxAccountResponseVo implements Serializable {
    private String openid;
    private String session_key;
    private String unionid;
    private Integer errcode;
    private String errmsg;
}
