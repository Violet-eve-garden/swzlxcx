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
import com.sysu.swzl.service.GoodsMessageService;
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
            goodsMessage.setCreateTime(new Date());
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
        System.out.println(oldGoods.toString());
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

            // 不修改创建时间
            goodsMessage.setCreateTime(oldGoods.getCreateTime());
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
}
