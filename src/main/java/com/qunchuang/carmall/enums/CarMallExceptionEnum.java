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

    USER_NOT_EXISTS(20001, "用户不存在"),
    ROLE_IS_EXISTS(20002, "角色已存在"),
    USER_OPENID_IS_NULL(20003, "用户openid为空"),
    USERNAME_IS_EXISTS(20004, "用户名已被注册"),
    USER_PHONE_IS_REGISTER(20005, "手机号已经被注册"),
    ADMIN_NOT_EXISTS(20006, "管理员账号不存在"),
    SALES_CONSULTANT_NOT_EXISTS(20007, "销售人员不存在"),
    PASSWORD_WRONG(20008,"密码错误，修改失败"),
    CUSTOMER_INTAGRAL_INSUFFICIENT(20010,"积分不足"),
    CAR_INFO_NOT_IMPERFECT(20011,"车辆上架需要完善方案和图片信息"),


    GET_VERIFICATION_CODE_FAIL(22001, "获取验证码失败"),
    VERIFY_CODE_FAIL(22002, "验证失败，验证码不正确"),

    CAR_INFO_NOT_EXISTS(30001, "车辆信息不存在"),
    CONSULT_NOT_EXISTS(30002, "咨询单不存在"),
    CONSULT_ALLOCATE_FAIL(30003, "派咨询单失败，该订单已不再所属此门店"),
    CONSULT_ALREADY_ALLOCATE(30004, "派咨询单失败，该订单已经派送"),
    CONSULT_MODIFY_FAIL(30005, "修改咨询单失败，该订单已不再所属此销售人员"),
    CAR_BRAND_ICON_NOT_EXISTS(30006,"车辆品牌信息不存在"),
    CONSULT_NOT_FINISH(30007,"新订单不允许完结"),
    ADVERTISEMENT_NOT_EXISTS(30201,"广告信息不存在"),


    STORE_NOT_EXISTS(40001, "门店不存在"),
    STORE_DISTANCE_CALC_FAIL(40002,"门店距离计算失败"),
    STORE_ACCOUNT_EXISTS(40003,"门店账号已经存在"),


    GET_USER_LOGIN_INFO_FAIL(50001, "获取当前登录的微信用户失败"),
    GET_ADMIN_LOGIN_INFO_FAIL(50002, "获取当前登录的管理员用户失败"),

    PRIVILEGE_INSUFFICIENT(50003, "权限不足，操作失败"),;

    private Integer code;

    private String message;

    CarMallExceptionEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

}
