package com.qunchuang.carmall.domain;

import cn.wzvtcsoft.bosdomain.BosEntity;
import cn.wzvtcsoft.bosdomain.annotations.Bostype;
import com.qunchuang.carmall.enums.CarMallExceptionEnum;
import com.qunchuang.carmall.enums.IntegralEnum;
import com.qunchuang.carmall.exception.CarMallException;
import com.qunchuang.carmall.graphql.annotation.SchemaDocumentation;
import com.qunchuang.carmall.graphql.query.QueryFilter;
import com.qunchuang.carmall.graphql.query.dataprivilege.PrivilegeConstraint;
import com.qunchuang.carmall.utils.PrivilegeUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 * @author Curtain
 * @date 2019/1/14 8:48
 */
@Entity
@Bostype("C01")
@SchemaDocumentation("顾客")
@Getter
@Setter
@Slf4j
public class Customer extends BosEntity {

    @SchemaDocumentation("昵称")
    private String nickname;

    @SchemaDocumentation("微信openid")
    private String openid;

    @SchemaDocumentation("性别")
    @Column(length = 10)
    private String gender;

    @SchemaDocumentation("姓名")
    private String name;

    @SchemaDocumentation("手机号")
    private String phone;

    @SchemaDocumentation("门店")
    @ManyToOne
    private Store store;

    @SchemaDocumentation("销售顾问")
    @ManyToOne
    private Admin salesConsultantAdmin;

    @SchemaDocumentation("积分")
    private int integral;

    @SchemaDocumentation("邀请人")
    private String invitedId;


//    @SchemaDocumentation("是否是销售顾问")
//    private boolean salesConsultant;

    public void setOpenid(String openid) {
        if (StringUtils.isEmpty(openid)) {
            log.error("用户openid为空 openid = {}", openid);
            throw new CarMallException(CarMallExceptionEnum.USER_OPENID_IS_NULL);
        }
        this.openid = openid;
    }

    public static Customer getCustomer() {
        Customer customer;
        try {
            customer = (Customer) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        } catch (Exception e) {
            log.error("获取当前微信用户登录信息失败 Authentication = {}", SecurityContextHolder.getContext().getAuthentication());
//            throw new CarMallException(CarMallExceptionEnum.GET_USER_LOGIN_INFO_FAIL);
            throw new BadCredentialsException("失效用户");
        }
        return customer;
    }

    public void modifyIntegral(int number){
        this.integral = this.integral + number;
        if (this.integral > IntegralEnum.UPPER_LIMIT.getCode()){
            this.integral = IntegralEnum.UPPER_LIMIT.getCode();
        }
    }


    @PrivilegeConstraint
    public QueryFilter privilegeConstraint() {
        //定制查看客户  各角色所属权限
        return PrivilegeUtil.customerInfoPrivilege();
    }
}
