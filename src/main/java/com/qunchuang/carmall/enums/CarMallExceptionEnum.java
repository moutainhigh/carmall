package com.qunchuang.carmall.enums;

import lombok.Getter;

/**
 * @author Curtain
 * @date 2019/1/15 14:13
 */
@Getter
public enum CarMallExceptionEnum {
    /*异常枚举 code  message*/

    REQUEST_FAIL(10001, "请求失败，参数错误"),

    USER_NOT_EXISTS(20001,"用户不存在"),
    ROLE_IS_EXISTS(20002,"角色已存在"),
    USER_ARGS_NOT_TRUE(20003,"用户参数不正确"),
    USERNAME_IS_EXISTS(20004,"用户名已被注册"),
    USER_PHONE_IS_REGISTER(20005,"手机号已经被注册")

    ;

    private Integer code;

    private String message;

    CarMallExceptionEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

}
