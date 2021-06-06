package com.sysu.swzl.pojo;

import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.util.Date;

public class CardsMessage {
    @Null
    private Long id;

    @NotNull
    private String openId;

    @NotNull
    @Length(max = 50)
    private String cardNum;

    @NotNull
    private String type;

    @Null
    private Date createTime;

    @NotNull
    @Max(2)
    @Min(1)
    private Integer inforType;

    public CardsMessage(Long id, String openId, String cardNum, String type, Date createTime, Integer inforType) {
        this.id = id;
        this.openId = openId;
        this.cardNum = cardNum;
        this.type = type;
        this.createTime = createTime;
        this.inforType = inforType;
    }

    public CardsMessage() {
        super();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId == null ? null : openId.trim();
    }

    public String getCardNum() {
        return cardNum;
    }

    public void setCardNum(String cardNum) {
        this.cardNum = cardNum == null ? null : cardNum.trim();
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type == null ? null : type.trim();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Integer getInforType() {
        return inforType;
    }

    public void setInforType(Integer inforType) {
        this.inforType = inforType;
    }
}