package com.qunchuang.carmall.bean;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Curtain
 * @date 2019/3/18 11:29
 */

@Getter
@Setter
public class WeChatMiniShareInfo {

    /**销售人员id*/
    private String salesManId;

    /**车型*/
    private String carModel;

    /**路径*/
    private String from;
}
