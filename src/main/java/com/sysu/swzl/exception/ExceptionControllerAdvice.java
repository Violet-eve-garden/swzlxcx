package com.sysu.swzl.exception;

import com.sysu.swzl.common.R;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.support.DefaultHandlerExceptionResolver;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 49367
 * @date 2021/5/24 22:38
 */
@Slf4j
// 扫描包，无法使用通配符，只能扫描priv.violet.mall下的所有包
@ControllerAdvice(basePackages = "com.sysu.swzl")
public class ExceptionControllerAdvice {

    @ResponseBody
    @ExceptionHandler(value = BindException.class) // 数据校验异常
    public R handleValidException(BindException exception){

        Map<String,String> map = new HashMap<>();

        //获取数据校验的错误结果
        BindingResult bindingResult = exception.getBindingResult();
        bindingResult.getFieldErrors().forEach(fieldError -> {
            String message = fieldError.getDefaultMessage();
            String field = fieldError.getField();
            map.put(field,message);
        });

        log.error("数据校验出现问题{},异常类型{}",exception.getMessage(),exception.getClass());

        return R.error(BizCodeException.VALID_EXCEPTION.getCode(), BizCodeException.VALID_EXCEPTION.getMessage()).put("data",map);
    }
}
