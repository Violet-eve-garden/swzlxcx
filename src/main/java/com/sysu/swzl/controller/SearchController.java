package com.sysu.swzl.controller;

import com.sysu.swzl.common.R;
import com.sysu.swzl.service.GoodsMessageService;
import com.sysu.swzl.service.SearchService;
import com.sysu.swzl.vo.GoodsMessageRespVo;
import com.sysu.swzl.vo.GoodsSpecificInfoRespVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author 49367
 * @date 2021/6/2 13:44
 */
@RestController
@RequestMapping(path = "/search", produces = {"application/json;charset=UTF-8"})
public class SearchController {

    @Autowired
    private SearchService searchService;

    /**
     * 查询最新十条失物招领信息
     * @return
     */
    @GetMapping("/searchLatest")
    public R searchLatestInfoList() {
        List<GoodsMessageRespVo> respVos = searchService.searchLatest();

        return R.ok().put("latestInfoList", respVos);
    }

    /**
     * 查询失物的详细信息
     * @param id
     * @return
     */
    @GetMapping("/searchDescribe")
    public R searchSpecificInformation(@RequestParam("id") Long id){
        GoodsSpecificInfoRespVo respVo = searchService.searchGoodsSpecificInfoById(id);
        return R.ok().put("data", respVo);
    }

    /**
     * 查询分类对应的失物信息
     */
    @GetMapping("/searchGoods")
    public R searchGoodsByType(@RequestParam("type") String type){
        List<GoodsMessageRespVo> respVos = searchService.searchGoodsMessageListByType(type);

        return R.ok().put("list", respVos);
    }

    /**
     * 查询用户发布的失物信息
     * @param openId
     * @return
     */
    @GetMapping("searchMyGoodsInfo")
    public R searchMyGoodsInfo(@RequestParam("openId") String openId) {
        List<GoodsMessageRespVo> goodsMessageRespVos = searchService.searchGoodsInfoListByOpenId(openId);
        return R.ok().put("list", goodsMessageRespVos);
    }
}
