package com.sysu.swzl.service;

import com.sysu.swzl.common.R;
import com.sysu.swzl.pojo.CardsMessage;
import com.sysu.swzl.pojo.GoodsMessage;

import java.util.concurrent.ExecutionException;

/**
 * @author 49367
 * @date 2021/5/31 16:59
 */
public interface GoodsMessageService {

    /**
     * 添加物品失物信息
     * @param goodsMessage
     * @return
     */
    R addGoodsMessage(GoodsMessage goodsMessage);

    /**
     * 修改失物信息
     * @param goodsMessage
     */
    R updateGoodsMessage(GoodsMessage goodsMessage);
}
