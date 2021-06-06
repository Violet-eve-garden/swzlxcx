package com.sysu.swzl.service;

import com.sysu.swzl.common.R;
import com.sysu.swzl.vo.GoodsMessageRespVo;
import com.sysu.swzl.vo.GoodsSpecificInfoRespVo;

import java.util.List;

/**
 * @author 49367
 * @date 2021/6/2 13:51
 */
public interface SearchService {

    /**
     * 查询最新的失物招领消息
     * @return
     */
    List<GoodsMessageRespVo> searchLatest();

    /**
     * 根据id查询对应的失物的详细信息
     */
    GoodsSpecificInfoRespVo searchGoodsSpecificInfoById(Long id);

    /**
     * 查询分类对应的失物信息列表
     * @param type
     * @return
     */
    List<GoodsMessageRespVo> searchGoodsMessageListByType(String type);
}
