package com.qunchuang.carmall.enums;

import lombok.Getter;

/**
 * @author Curtain
 * @date 2019/1/16 14:18
 */
@Getter
public enum PrivilegeAuthorityEnum {
    /*权限枚举 identifier*/

    CUSTOMER_MANAGEMENT("A1"),
    VEHICLE_MANAGEMENT("B1"),
    STORE_MANAGEMENT("C1"),
    SALES_CONSULTANT_MANAGEMENT("D1"),
    ;

    private String identifier;

    PrivilegeAuthorityEnum(String identifier) {
        this.identifier = identifier;
    }
}
