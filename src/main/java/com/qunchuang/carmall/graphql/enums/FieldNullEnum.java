package com.qunchuang.carmall.graphql.enums;

import cn.wzvtcsoft.bosdomain.annotations.EntityEnum;
import com.qunchuang.carmall.graphql.annotation.SchemaDocumentation;
import cn.wzvtcsoft.bosdomain.enums.BosEnum;

@EntityEnum
@SchemaDocumentation("测试是否为空")
public enum FieldNullEnum implements BosEnum {
    @EntityEnum(value = "ISNULL", alias = "为空")
    ISNULL,
    @EntityEnum(value = "NOTNULL", alias = "不为空")
    NOTNULL;


}


