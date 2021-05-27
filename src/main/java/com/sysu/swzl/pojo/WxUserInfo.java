package com.sysu.swzl.pojo;

import com.sysu.swzl.validate.AddGroup;
import com.sysu.swzl.validate.UpdateGroup;
import org.hibernate.validator.constraints.Length;
import org.springframework.data.annotation.Transient;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

public class WxUserInfo  implements Serializable {
    @Null
    @Transient
    private Long id;

    @NotNull(groups = {AddGroup.class})
    @Null(groups = {UpdateGroup.class})
    @Length(max = 200, message = "昵称过长")
    private String nickName;

    @NotNull
    private String openId;


    private String qq;

    private String weixin;

    private String phone;

    @Null
    @Transient
    private Byte type;

    private String other;

    public WxUserInfo(Long id, String nickName, String openId, String qq, String weixin, String phone, Byte type, String other) {
        this.id = id;
        this.nickName = nickName;
        this.openId = openId;
        this.qq = qq;
        this.weixin = weixin;
        this.phone = phone;
        this.type = type;
        this.other = other;
    }

    public WxUserInfo() {
        super();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName == null ? null : nickName.trim();
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId == null ? null : openId.trim();
    }

    public String getQq() {
        return qq;
    }

    public void setQq(String qq) {
        this.qq = qq == null ? null : qq.trim();
    }

    public String getWeixin() {
        return weixin;
    }

    public void setWeixin(String weixin) {
        this.weixin = weixin == null ? null : weixin.trim();
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone == null ? null : phone.trim();
    }

    public Byte getType() {
        return type;
    }

    public void setType(Byte type) {
        this.type = type;
    }

    public String getOther() {
        return other;
    }

    public void setOther(String other) {
        this.other = other == null ? null : other.trim();
    }
}