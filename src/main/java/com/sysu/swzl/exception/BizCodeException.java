package com.sysu.swzl.exception;

/**
 * @author 49367
 * @date 2021/5/24 22:39
 */
public enum BizCodeException {
    UNKNOWN_EXCEPTION(10000, "系统未知异常"),
    VALID_EXCEPTION(10001, "参数校验异常"),;

    private int code;
    private String message;

    BizCodeException(int code, String message){
        this.code = code;
        this.message = message;
    }

    public int getCode(){
        return code;
    }

    public String getMessage(){
        return message;
    }
}
