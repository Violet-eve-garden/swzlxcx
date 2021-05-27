package com.sysu.swzl.common;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 49367
 * @date 2021/5/24 14:58
 */
public class R extends HashMap<String, Object> {
    private static final long serialVersionUID = 1L;

    public R setData(Object data){
        put("data", data);
        return this;
    }

    public <T> T getData(TypeReference<T> typeReference){
        Object data = get("data");
        // data转json
        String s = JSON.toJSONString(data);
        // json转T类
        return JSON.parseObject(s, typeReference);
    }

    public R setData(String key, Object data){
        put(key, data);
        return this;
    }

    public <T> T getData(String key, TypeReference<T> typeReference){
        Object data = get(key);
        // data转json
        String s = JSON.toJSONString(data);
        // json转T类
        return JSON.parseObject(s, typeReference);
    }


    public R() {
        put("code", 0);
        put("msg", "success");
    }

    public static R error() {
        return error(HttpStatus.SERVICE_UNAVAILABLE.value(), "未知异常，请联系管理员");
    }

    public static R error(String msg) {
        return error(HttpStatus.SERVICE_UNAVAILABLE.value(), msg);
    }

    public static R error(int code, String msg) {
        R r = new R();
        r.put("code", code);
        r.put("msg", msg);
        return r;
    }

    public static R ok(String msg) {
        R r = new R();
        r.put("msg", msg);
        return r;
    }

    public static R ok(Map<String, Object> map) {
        R r = new R();
        r.putAll(map);
        return r;
    }

    public static R ok() {
        return new R();
    }

    public R put(String key, Object value) {
        super.put(key, value);
        return this;
    }

    public Integer getCode(){
        return (Integer) this.get("code");
    }
}