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
    USER_PHONE_IS_REGISTER(20005,"手机号已经被注册"),
    ADMIN_NOT_EXISTS(20006,"管理员账号不存在"),


    GET_VERIFICATION_CODE_FAIL(22001,"获取验证码失败"),
    VERIFY_CODE_FAIL(22002,"验证失败，验证码不正确"),

    CAR_INFO_NOT_EXISTS(30001,"车辆信息不存在"),
    CONSULT_NOT_EXISTS(30002,"咨询单不存在"),

    STORE_NOT_EXISTS(40001,"门店不存在"),


    ;

    private Integer code;

    private String message;

    CarMallExceptionEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

}
