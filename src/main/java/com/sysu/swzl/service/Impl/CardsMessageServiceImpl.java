package com.sysu.swzl.service.Impl;

import com.sysu.swzl.common.R;
import com.sysu.swzl.dao.CardsMessageMapper;
import com.sysu.swzl.dao.TypeMapper;
import com.sysu.swzl.exception.BizCodeException;
import com.sysu.swzl.pojo.CardsMessage;
import com.sysu.swzl.pojo.Type;
import com.sysu.swzl.service.CardsMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Date;

/**
 * @author 49367
 * @date 2021/5/30 11:10
 */
@Service
public class CardsMessageServiceImpl implements CardsMessageService {

    @Autowired
    private CardsMessageMapper cardsMessageMapper;

    @Autowired
    private TypeMapper typeMapper;

    /**
     * 添加卡片失物信息
     * @param cardsMessage
     * @return
     */
    @Override
    @Transactional
    public R addCardMessage(CardsMessage cardsMessage) {
        String type = cardsMessage.getType();
        if (typeMapper.selectByName(type) == null)
            return R.error(BizCodeException.TYPE_EXCEPTION.getCode(), BizCodeException.TYPE_EXCEPTION.getMessage());
        cardsMessage.setCreateTime(new Date());
        cardsMessageMapper.insertSelective(cardsMessage);

        return R.ok();
    }
}
