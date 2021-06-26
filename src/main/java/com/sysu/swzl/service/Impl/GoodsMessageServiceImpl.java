package com.sysu.swzl.service.Impl;

import com.sun.media.jfxmedia.logging.Logger;
import com.sysu.swzl.common.R;
import com.sysu.swzl.constant.MessageConstant;
import com.sysu.swzl.dao.GoodsMessageMapper;
import com.sysu.swzl.dao.TypeMapper;
import com.sysu.swzl.dao.UploadFileMapper;
import com.sysu.swzl.exception.BizCodeException;
import com.sysu.swzl.pojo.GoodsMessage;
import com.sysu.swzl.pojo.Type;
import com.sysu.swzl.pojo.UploadFile;
import com.sysu.swzl.pojo.WxUserInfo;
import com.sysu.swzl.service.GoodsMessageService;
import com.sysu.swzl.service.WeChatService;
import com.sysu.swzl.vo.WxUserInfoVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.concurrent.*;

/**
 * 物品失物信息的
 * @author 49367
 * @date 2021/5/31 16:59
 */
@Service
public class GoodsMessageServiceImpl implements GoodsMessageService {

    @Autowired
    private GoodsMessageMapper goodsMessageMapper;

    @Autowired
    private UploadFileMapper uploadFileMapper;

    @Autowired
    private WeChatService weChatService;

    @Autowired
    private TypeMapper typeMapper;

    @Autowired
    private ThreadPoolExecutor executor;

    @Autowired
    private StringRedisTemplate redisTemplate;


    /**
     * 添加物品失物信息
     * @param goodsMessage
     * @return
     */
    @Override
    @Transactional
    public R addGoodsMessage(GoodsMessage goodsMessage) {
        CompletableFuture<UploadFile> fileFuture = null;
        String fileName = goodsMessage.getFileName();
        // 是否有上传文件
        boolean haveFile = StringUtils.hasText(fileName) && !"null".equals(fileName);

        System.out.println("addGoodsMessage have file:" + haveFile + fileName);

        if (haveFile && StringUtils.hasText(goodsMessage.getFileName()))
            // 异步获取文件信息
            fileFuture = CompletableFuture.supplyAsync(() ->
                    uploadFileMapper.selectByName(goodsMessage.getFileName()), executor);

        // 异步获取类型信息
        CompletableFuture<Type> typeFuture = CompletableFuture.supplyAsync(() ->
                typeMapper.selectByName(goodsMessage.getType()), executor);

        try {
            if (haveFile && fileFuture != null){
                UploadFile uploadFile = fileFuture.get(2, TimeUnit.SECONDS);
                if (uploadFile == null)
                    return R.error(BizCodeException.FILENAME_EXCEPTION.getCode(), BizCodeException.FILENAME_EXCEPTION.getMessage());
            }

            Type type = typeFuture.get(2, TimeUnit.SECONDS);
            if (type == null)
                return R.error(BizCodeException.TYPE_EXCEPTION.getCode(), BizCodeException.TYPE_EXCEPTION.getMessage());

            // 异步删除缓存中的最近失物招领消息列表
            CompletableFuture.runAsync(() -> redisTemplate.delete(MessageConstant.LATEST_INFO_REDIS_KEY));

            // 保存物品失物信息
            Date now = new Date();
            goodsMessage.setCreateTime(now);
            goodsMessage.setUpdateTime(now);
            goodsMessage.setState(MessageConstant.INIT_STATE);
            goodsMessageMapper.insertSelective(goodsMessage);

            return R.ok();
        } catch (Exception e) {
            e.printStackTrace();
            // 回滚事务
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return R.error(BizCodeException.UNKNOWN_EXCEPTION.getCode(), "添加物品失物信息失败");
        }
    }

    /**
     * 修改物品失物信息
     * @param goodsMessage
     * @return
     */
    @Override
    @Transactional
    public R updateGoodsMessage(GoodsMessage goodsMessage) {
        GoodsMessage oldGoods = goodsMessageMapper.selectByPrimaryKey(goodsMessage.getId());
        if (oldGoods == null || !oldGoods.getOpenId().equals(goodsMessage.getOpenId()))
            return R.error(BizCodeException.UPDATE_GOODS_EXCEPTION.getCode(), BizCodeException.UPDATE_GOODS_EXCEPTION.getMessage());

        CompletableFuture<UploadFile> fileFuture = null;

        String fileName = goodsMessage.getFileName();

        // 是否有上传文件
        boolean haveFile = StringUtils.hasText(fileName) && !"null".equals(fileName);

        System.out.println("updateGoodsMessage have file:" + haveFile + fileName);

        if (haveFile && StringUtils.hasText(goodsMessage.getFileName()))
            // 异步获取文件信息
            fileFuture = CompletableFuture.supplyAsync(() ->
                    uploadFileMapper.selectByName(goodsMessage.getFileName()), executor);

        // 异步获取类型信息
        CompletableFuture<Type> typeFuture = CompletableFuture.supplyAsync(() ->
                typeMapper.selectByName(goodsMessage.getType()), executor);

        try {
            if (haveFile && fileFuture != null){
                UploadFile uploadFile = fileFuture.get(2, TimeUnit.SECONDS);
                if (uploadFile == null)
                    return R.error(BizCodeException.FILENAME_EXCEPTION.getCode(), BizCodeException.FILENAME_EXCEPTION.getMessage());
            }

            Type type = typeFuture.get(2, TimeUnit.SECONDS);
            if (type == null)
                return R.error(BizCodeException.TYPE_EXCEPTION.getCode(), BizCodeException.TYPE_EXCEPTION.getMessage());

            // 异步删除redis里的缓存数据，是否删除成功没影响
            CompletableFuture.runAsync(() -> redisTemplate.delete(MessageConstant.LATEST_INFO_REDIS_KEY));

            // 修改更改时间
            goodsMessage.setUpdateTime(new Date());
            // 保存修改的物品失物信息
            goodsMessageMapper.updateByPrimaryKeySelective(goodsMessage);

            return R.ok();
        } catch (Exception e) {
            e.printStackTrace();
            // 回滚事务
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return R.error(BizCodeException.UNKNOWN_EXCEPTION.getCode(), "修改物品失物信息失败");
        }
    }

    /**
     * 根据id修改失物状态
     * @param id
     * @return
     */
    @Override
    @Transactional
    public R updateGoodsStateById(Long id, String openId) {
        GoodsMessage goodsMessage = goodsMessageMapper.selectByPrimaryKey(id);
        // id对应的失物消息不存在或者openId不一致，返回错误信息
        if (goodsMessage == null || goodsMessage.getOpenId() == null ||!goodsMessage.getOpenId().equals(openId))
            return R.error(BizCodeException.GOODS_INFO_NOTFOUND_EXCEPTION.getCode(), BizCodeException.GOODS_INFO_NOTFOUND_EXCEPTION.getMessage());

        goodsMessage.setState(MessageConstant.CHANGED_STATE);
        int res = goodsMessageMapper.updateByPrimaryKeySelective(goodsMessage);
        if (res > 0)
            return R.ok();
        return R.error(BizCodeException.UNKNOWN_EXCEPTION.getCode(), BizCodeException.UNKNOWN_EXCEPTION.getMessage());
    }
}
