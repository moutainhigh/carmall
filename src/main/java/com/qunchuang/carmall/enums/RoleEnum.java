package com.qunchuang.carmall.enums;

import lombok.Getter;

/**
 * @author Curtain
 * @date 2019/1/24 14:47
 */
@Getter
public enum  RoleEnum {

        /*权限枚举 identifier*/

    PLATFORM_ADMINISTRATOR("平台管理员"),
    STORE_ADMINISTRATOR("门店管理员"),
    SALES_CONSULTANT_ADMINISTRATOR("销售顾问"),
            ;

    private String roleName;

    RoleEnum(String roleName) {
        this.roleName = roleName;
    }
}
