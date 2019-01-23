package com.qunchuang.carmall.validator.errors;

import cn.wzvtcsoft.validator.errors.ParamInfo;

import java.util.List;

/**
 * 未通过 @ValidSelect 校验的方法会生成此异常，并封装未通过校验的参数的信息。
 *
 * 该类的实例将被序列化返回
 *
 * @author zzk
 * @date 2018/10/2
 */
public class ValidSelectError {

    private String message;

    private List<cn.wzvtcsoft.validator.errors.ParamInfo> paramInfoList;

    public String getMessage() {
        return message;
    }

    public List<cn.wzvtcsoft.validator.errors.ParamInfo> getParamInfoList() {
        return paramInfoList;
    }

    public ValidSelectError(String message, List<ParamInfo> paramInfoList) {
        this.message = message;
        this.paramInfoList = paramInfoList;
    }

}
