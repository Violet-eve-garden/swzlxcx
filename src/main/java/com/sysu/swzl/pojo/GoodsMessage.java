package com.sysu.swzl.pojo;

import org.hibernate.validator.constraints.Length;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.util.Date;

public class GoodsMessage {
    @Null
    private Long id;

    @NotNull
    @Length(max = 100)
    private String openId;

    @NotNull
    @Length(max = 100)
    private String title;

    @NotNull
    @Length(max = 250)
    private String describe;

    @NotNull
    @Length(max = 100)
    private String type;

    @NotNull
    @Max(2)
    private Integer inforType;

    @Length(max = 200)
    private String imgPath;

    @Null
    private Date createTime;

    public GoodsMessage(Long id, String openId, String title, String describe, String type, Integer inforType, String imgPath, Date createTime) {
        this.id = id;
        this.openId = openId;
        this.title = title;
        this.describe = describe;
        this.type = type;
        this.inforType = inforType;
        this.imgPath = imgPath;
        this.createTime = createTime;
    }

    public GoodsMessage() {
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title == null ? null : title.trim();
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe == null ? null : describe.trim();
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type == null ? null : type.trim();
    }

    public Integer getInforType() {
        return inforType;
    }

    public void setInforType(Integer inforType) {
        this.inforType = inforType;
    }

    public String getImgPath() {
        return imgPath;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath == null ? null : imgPath.trim();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }


    public String getFileName() {
        return imgPath;
    }

    /**
     * 前端数据传来 fileName，后端设置为ImgPath
     * @return
     */
    public void setFileName(String fileName) {
        this.imgPath = fileName == null ? null : fileName.trim();
    }
}