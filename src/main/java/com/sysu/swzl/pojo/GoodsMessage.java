package com.sysu.swzl.pojo;

import com.sysu.swzl.validate.AddGroup;
import com.sysu.swzl.validate.UpdateGroup;
import org.hibernate.validator.constraints.Length;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.util.Date;

public class GoodsMessage {
    @Null(groups = AddGroup.class)
    @NotNull(groups = UpdateGroup.class)
    private Long id;

    @NotNull(groups = {AddGroup.class, UpdateGroup.class})
    @Length(max = 100, groups = {AddGroup.class, UpdateGroup.class})
    private String openId;

    @NotNull(groups = {AddGroup.class, UpdateGroup.class})
    @Length(max = 100, groups = {AddGroup.class, UpdateGroup.class})
    private String title;

    @NotNull(groups = {AddGroup.class, UpdateGroup.class})
    @Length(max = 250, groups = {AddGroup.class, UpdateGroup.class})
    private String describe;

    @NotNull(groups = {AddGroup.class, UpdateGroup.class})
    @Length(max = 100, groups = {AddGroup.class, UpdateGroup.class})
    private String type;

    @NotNull(groups = {AddGroup.class, UpdateGroup.class})
    @Max(value = 2, groups = {AddGroup.class, UpdateGroup.class})
    private Integer inforType;

    @Length(max = 200, groups = {AddGroup.class, UpdateGroup.class})
    private String imgPath;

    @Null(groups = {AddGroup.class, UpdateGroup.class})
    private Date createTime;

    @Null(groups = {AddGroup.class, UpdateGroup.class})
    private Date updateTime;

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    @Null(groups = {AddGroup.class, UpdateGroup.class})
    private Integer state;

    public GoodsMessage(Long id, String openId, String title, String describe, String type, Integer inforType, String imgPath, Date createTime, Date updateTime, Integer state) {
        this.id = id;
        this.openId = openId;
        this.title = title;
        this.describe = describe;
        this.type = type;
        this.inforType = inforType;
        this.imgPath = imgPath;
        this.createTime = createTime;
        this.updateTime = updateTime;
        this.state = state;
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

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}