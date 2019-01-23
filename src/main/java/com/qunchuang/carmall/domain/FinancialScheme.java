package com.qunchuang.carmall.domain;


import cn.wzvtcsoft.bosdomain.Entry;
import cn.wzvtcsoft.bosdomain.annotations.Bostype;
import com.qunchuang.carmall.graphql.annotation.SchemaDocumentation;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;

/**
 * @author Curtain
 * @date 2018/7/27 20:01
 */

@Entity
@Bostype("F01")
@SchemaDocumentation("金融方案")
@Getter
@Setter
public class FinancialScheme extends Entry {

    @SchemaDocumentation("首付")
    private String downPayment;

    @SchemaDocumentation("月供")
    private String monthly;

    @SchemaDocumentation("期数/月")
    private String periods;


}
