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
    /**车辆管理**/
    VEHICLE_MANAGEMENT("VEHICLE_MANAGEMENT"),
    /**门店管理**/
    STORE_MANAGEMENT("STORE_MANAGEMENT"),
    /**销售员管理**/
    SALES_CONSULTANT_MANAGEMENT("SALES_CONSULTANT_MANAGEMENT"),
    /**平台管理**/
    PLATFORM_MANAGEMENT("PLATFORM_MANAGEMENT"),
    ;

    private String identifier;

    PrivilegeAuthorityEnum(String identifier) {
        this.identifier = identifier;
    }
}
