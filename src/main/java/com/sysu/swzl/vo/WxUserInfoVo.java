package com.sysu.swzl.vo;

import com.sysu.swzl.pojo.WxUserInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.data.annotation.Transient;
import org.springframework.security.core.GrantedAuthority;

import java.io.Serializable;
import java.util.Set;

/**
 * @author 49367
 * @date 2021/5/24 15:55
 */
@EqualsAndHashCode(callSuper = false)
@Data
@ToString
public class WxUserInfoVo extends WxUserInfo  implements Serializable  {
    private Set<GrantedAuthority> authorities;
}
