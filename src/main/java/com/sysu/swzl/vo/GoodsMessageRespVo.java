package com.sysu.swzl.vo;

import com.sysu.swzl.pojo.GoodsMessage;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * @author 49367
 * @date 2021/6/4 21:17
 */
@Data
@AllArgsConstructor
public class GoodsMessageRespVo {
    private Long id;
    private String img;
    private String title;
    private String describe;
    private Date time;

    public static List<GoodsMessageRespVo> copyFromGoodMessagesList(List<GoodsMessage> goodsMessages){
        List<GoodsMessageRespVo> goodsMessageRespVos = new LinkedList<>();
        goodsMessages.forEach(goodsMessage -> {
            GoodsMessageRespVo goodsMessageRespVo = new GoodsMessageRespVo(goodsMessage.getId(), goodsMessage.getFileName(), goodsMessage.getTitle(), goodsMessage.getDescribe(),
                    goodsMessage.getCreateTime());
            goodsMessageRespVos.add(goodsMessageRespVo);
        });
        return goodsMessageRespVos;
    }
}
