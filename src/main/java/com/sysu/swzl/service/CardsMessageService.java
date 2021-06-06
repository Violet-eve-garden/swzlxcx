package com.sysu.swzl.service;

import com.sysu.swzl.common.R;
import com.sysu.swzl.pojo.CardsMessage;

/**
 * @author 49367
 * @date 2021/5/30 11:10
 */
public interface CardsMessageService {

    /**
     * 添加卡片失物信息
     * @param cardsMessage
     * @return
     */
    R addCardMessage(CardsMessage cardsMessage);
}
