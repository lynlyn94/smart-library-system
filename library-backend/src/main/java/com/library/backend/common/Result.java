package com.library.backend.common;

import lombok.Data;

@Data
public class Result<T> {
    private Integer code;
    private String message;
    private T data;

    // 私有化构造方法，强制通过静态方法创建对象
    private Result() {}

    // 成功时的返回方法 (带数据)
    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMessage("操作成功");
        result.setData(data);
        return result;
    }

    // 成功时的返回方法 (不带数据，只返回成功提示)
    public static <T> Result<T> success() {
        return success(null);
    }

    // 失败时的返回方法
    public static <T> Result<T> error(Integer code, String message) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMessage(message);
        result.setData(null);
        return result;
    }
}