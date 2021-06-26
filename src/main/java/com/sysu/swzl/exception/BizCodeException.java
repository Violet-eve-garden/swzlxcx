package com.sysu.swzl.exception;

/**
 * @author 49367
 * @date 2021/5/24 22:39
 */
public enum BizCodeException {
    UNKNOWN_EXCEPTION(10000, "系统未知异常"),
    VALID_EXCEPTION(10001, "参数校验异常"),
    TYPE_EXCEPTION(10002, "不可用类型异常"),
    FILE_UPLOAD_EXCEPTION(10003, "文件上传异常"),
    FILENAME_EXCEPTION(10004, "文件名错误"),
    UPDATE_GOODS_EXCEPTION(10005, "非法修改失物信息"),
    USER_INFO_EXCEPTION(10006, "用户信息未提交"),
    GOODS_INFO_NOTFOUND_EXCEPTION(10007, "失物消息不存在")
    ;
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
