package com.sysu.swzl.vo;

import com.sysu.swzl.pojo.GoodsMessage;
import com.sysu.swzl.pojo.WxUserInfo;
import com.sysu.swzl.validate.AddGroup;
import com.sysu.swzl.validate.UpdateGroup;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.springframework.data.annotation.Transient;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.util.Date;

/**
 * 失物详细信息数据的前端展示类
 * @author 49367
 * @date 2021/6/6 16:59
 */
@Data
@NoArgsConstructor
public class GoodsSpecificInfoRespVo {
    private GoodsInfo goodsInfo;
    private UserInfo userInfo;

    @Data
    @AllArgsConstructor
    static class GoodsInfo{
        private Long id;
        private String img;
        private String title;
        private String describe;
        private Date time;
        private Integer inforType;
        private String goodsType;

        public static GoodsSpecificInfoRespVo.GoodsInfo copyFromGoodsMessage(GoodsMessage goodsMessage){
            return new GoodsInfo(goodsMessage.getId(), goodsMessage.getImgPath(), goodsMessage.getTitle(),
                    goodsMessage.getDescribe(), goodsMessage.getCreateTime(), goodsMessage.getInforType(), goodsMessage.getType());
        }
    }

    @Data
    @AllArgsConstructor
    static class UserInfo{
        private String nickName;
        private String qq;
        private String wechat;
        private String phone;
        private String other;

        public static GoodsSpecificInfoRespVo.UserInfo copyFromWxUserInfo(WxUserInfo wxUserInfo){
            return new UserInfo(wxUserInfo.getNickName(), wxUserInfo.getQq(), wxUserInfo.getWeixin(),
                    wxUserInfo.getPhone(), wxUserInfo.getOther());
        }
    }

    /**
     * 构造函数，参数为GoodsMessage和WxUserInfo类型的
     * @param goodsMessage
     * @param wxUserInfo
     */
    public GoodsSpecificInfoRespVo(GoodsMessage goodsMessage, WxUserInfo wxUserInfo){
        this.setGoodsInfo(GoodsInfo.copyFromGoodsMessage(goodsMessage));
        this.setUserInfo(UserInfo.copyFromWxUserInfo(wxUserInfo));
    }
}
