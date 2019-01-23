package cn.wzvtcsoft.validator.errors;

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

    private List<ParamInfo> paramInfoList;

    public String getMessage() {
        return message;
    }

    public List<ParamInfo> getParamInfoList() {
        return paramInfoList;
    }

    public ValidSelectError(String message, List<ParamInfo> paramInfoList) {
        this.message = message;
        this.paramInfoList = paramInfoList;
    }

}
