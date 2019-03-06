package com.qunchuang.carmall.domain;

import cn.wzvtcsoft.bosdomain.BosEntity;
import cn.wzvtcsoft.bosdomain.annotations.Bostype;
import com.qunchuang.carmall.graphql.annotation.SchemaDocumentation;
import com.qunchuang.carmall.graphql.query.QueryFilter;
import com.qunchuang.carmall.graphql.query.dataprivilege.PrivilegeConstraint;
import com.qunchuang.carmall.utils.PrivilegeUtil;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;

/**
 * @author Curtain
 * @date 2019/3/6 9:29
 */
@Entity
@Bostype("I01")
@SchemaDocumentation("积分记录")
@Getter
@Setter
public class IntegralRecord extends BosEntity {


    public final static String REGISTER = "注册";
    public final static String INVITE_REGISTER = "邀请用户完成注册";
    public final static String FINISH_CONSULT = "邀请用户完成咨询单";

    @SchemaDocumentation("类型：1增加 2扣除")
    private Integer category;

    @SchemaDocumentation("本次积分增加或扣除")
    private Integer  integralCurrent;

    @SchemaDocumentation("积分余额")
    private Integer  integralBalance;

    @SchemaDocumentation("咨询单Id")
    private String consultId;

    @SchemaDocumentation("内容")
    private String content;

    @SchemaDocumentation("用户id")
    private String customerId;

    public IntegralRecord(){}

    public IntegralRecord(Integer category, Integer integralCurrent, String content,Integer integralBalance, String consultId, String customerId) {
        this.category = category;
        this.integralCurrent = integralCurrent;
        this.content = content;
        this.integralBalance = integralBalance;
        this.consultId = consultId;
        this.customerId = customerId;
    }

    @PrivilegeConstraint
    public QueryFilter privilegeConstraint() {
        //定制查看客户  各角色所属权限
        return PrivilegeUtil.integralRecordPrivilege();
    }
}
