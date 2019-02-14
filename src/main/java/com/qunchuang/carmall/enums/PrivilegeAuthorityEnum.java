package com.qunchuang.carmall.enums;

import lombok.Getter;

/**
 * @author Curtain
 * @date 2019/1/16 14:18
 */
@Getter
public enum PrivilegeAuthorityEnum {
    /*权限枚举 identifier*/

    /**用户管理**/
    CUSTOMER_MANAGEMENT("CUSTOMER_MANAGEMENT"),
    /**c车辆管理**/
    VEHICLE_MANAGEMENT("VEHICLE_MANAGEMENT"),
    STORE_MANAGEMENT("STORE_MANAGEMENT"),
    SALES_CONSULTANT_MANAGEMENT("SALES_CONSULTANT_MANAGEMENT"),
    PLATFORM_MANAGEMENT("PLATFORM_MANAGEMENT"),
    ;

    private String identifier;

    PrivilegeAuthorityEnum(String identifier) {
        this.identifier = identifier;
    }
}
