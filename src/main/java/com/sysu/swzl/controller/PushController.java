package com.sysu.swzl.controller;

import com.sysu.swzl.common.R;
import com.sysu.swzl.constant.FileConstant;
import com.sysu.swzl.exception.BizCodeException;
import com.sysu.swzl.pojo.GoodsMessage;
import com.sysu.swzl.service.CardsMessageService;
import com.sysu.swzl.service.FileService;
import com.sysu.swzl.service.GoodsMessageService;
import com.sysu.swzl.utils.JwtUtil;
import com.sysu.swzl.validate.AddGroup;
import com.sysu.swzl.validate.UpdateGroup;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 上传信息的controller
 * @author 49367
 * @date 2021/5/30 11:03
 */
@RestController
@Slf4j
@RequestMapping(path = {"/push"}, produces = {"application/json;charset=UTF-8"})
public class PushController {


    @Autowired
    private GoodsMessageService goodsMessageService;

    @Autowired
    private FileService fileService;

    @Autowired
    private JwtUtil jwtUtil;

//    /**
//     * 上传卡片失物信息
//     * @param cardsMessage
//     * @return
//     */
//    @PostMapping("/submitCard")
//    public R submitCard(@Validated CardsMessage cardsMessage){
//        return cardsMessageService.addCardMessage(cardsMessage);
//    }

    /**
     * 上传照片
     * @param request
     * @param response
     * @throws IOException
     */
    @PostMapping("/uploadImg")
    public void uploadImg(MultipartHttpServletRequest request, HttpServletResponse response) throws IOException {
        MultipartFile file = request.getFile(FileConstant.REQUEST_FILENAME);
        String token = request.getParameter(FileConstant.TOKEN_PREFIX);
        log.info("uploadImg token: " + token);
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/json;charset=UTF-8");
        token = jwtUtil.weChatVerifyToken(token);
        response.setHeader("token", token);
        if (token == null){
            response.setStatus(HttpStatus.FORBIDDEN.value());
            response.getWriter().write("用户不合法");
            return;
        }

        R res = fileService.saveImg(file);
        if (!res.isOk()) {
            response.setStatus(HttpStatus.FORBIDDEN.value());
            response.getWriter().write((String) res.get("msg"));
            return;
        }

        response.getWriter().write((String) res.get("filename"));
    }


    @PostMapping("/submitInfo")
    public R submitGoods (@Validated(AddGroup.class) GoodsMessage goodsMessage) {
        try {
            return goodsMessageService.addGoodsMessage(goodsMessage);
        } catch (Exception e) {
            e.printStackTrace();
            return R.error(BizCodeException.UNKNOWN_EXCEPTION.getCode(), BizCodeException.UNKNOWN_EXCEPTION.getMessage());
        }
    }

    @PostMapping("/updateInfo")
    public R updateGoods (@Validated(UpdateGroup.class) GoodsMessage goodsMessage) {
        return goodsMessageService.updateGoodsMessage(goodsMessage);
    }

    @GetMapping("/updateGoodsState")
    public R updateState (@RequestParam(name = "id") Long id, @RequestParam(name = "openId") String openId) {
        return goodsMessageService.updateGoodsStateById(id, openId);
    }
}
