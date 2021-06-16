package com.sysu.swzl.service.Impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.sysu.swzl.constant.MessageConstant;
import com.sysu.swzl.dao.GoodsMessageMapper;
import com.sysu.swzl.dao.WxUserInfoMapper;
import com.sysu.swzl.pojo.GoodsMessage;
import com.sysu.swzl.pojo.WxUserInfo;
import com.sysu.swzl.service.SearchService;
import com.sysu.swzl.vo.GoodsMessageRespVo;
import com.sysu.swzl.vo.GoodsSpecificInfoRespVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author 49367
 * @date 2021/6/2 13:57
 */
@Service
@Slf4j
public class SearchServiceImpl implements SearchService {

    @Autowired
    private GoodsMessageMapper goodsMessageMapper;

    @Autowired
    private WxUserInfoMapper wxUserInfoMapper;

//    @Autowired
//    private CardsMessageMapper cardsMessageMapper;

    @Autowired
    private StringRedisTemplate redisTemplate;


    /**
     * 查询最新的失物招领信息
     * @return
     */
    @Override
    public List<GoodsMessageRespVo> searchLatest() {
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        String data = ops.get(MessageConstant.LATEST_INFO_REDIS_KEY);

        // 如果缓存中有数据，则直接返回缓存中的数据
        if (StringUtils.hasText(data)) {
            log.info("返回缓存中的数据");
            List<GoodsMessage> goodsMessages = JSON.parseObject(data, new TypeReference<List<GoodsMessage>>(){});
            return GoodsMessageRespVo.copyFromGoodMessagesList(goodsMessages);
        }
        // 双重检查锁
        synchronized (this){
            // 如果拿到锁的时候缓存中已有数据，则直接返回即可
            data = ops.get(MessageConstant.LATEST_INFO_REDIS_KEY);
            if (StringUtils.hasText(data)){
                log.info("返回缓存中的数据");
                List<GoodsMessage> goodsMessages = JSON.parseObject(data, new TypeReference<List<GoodsMessage>>(){});
                return GoodsMessageRespVo.copyFromGoodMessagesList(goodsMessages);
            }
            // 没有则到数据库中查询，并保存到缓存中, 并发情况下只需要一个线程查询并保存即可
            List<GoodsMessage> goodsMessages = goodsMessageMapper.selectGoodsMessageInTimeOrder(10, false);
            ops.set(MessageConstant.LATEST_INFO_REDIS_KEY, JSON.toJSONString(goodsMessages), MessageConstant.LATEST_INFO_REDIS_EX, TimeUnit.SECONDS);

            log.info("返回数据库中的数据");
            return GoodsMessageRespVo.copyFromGoodMessagesList(goodsMessages);
        }
    }

    /**
     * 根据id查询对应的失物的详细信息
     * @param id
     * @return
     */
    @Override
    public GoodsSpecificInfoRespVo searchGoodsSpecificInfoById(Long id) {
        GoodsMessage goodsMessage = goodsMessageMapper.selectByPrimaryKey(id);
        String openId = goodsMessage.getOpenId();
        WxUserInfo wxUserInfo = wxUserInfoMapper.selectByOpenId(openId);
        return new GoodsSpecificInfoRespVo(goodsMessage, wxUserInfo);
    }

    /**
     * 查询分类对应的失物信息列表
     * @param type
     * @return
     */
    @Override
    public List<GoodsMessageRespVo> searchGoodsMessageListByType(String type) {
        List<GoodsMessage> goodsMessages = goodsMessageMapper.selectByType(type);
        return GoodsMessageRespVo.copyFromGoodMessagesList(goodsMessages);
    }


}
