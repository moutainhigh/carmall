package com.qunchuang.carmall.validator.errors;

import cn.wzvtcsoft.validator.errors.ParamInfo;
import cn.wzvtcsoft.validator.errors.ValidSelectError;
import cn.wzvtcsoft.validator.exceptions.MutationValidateException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;

/**
 * 校验未通过的处理，具体是针对 MutationValidateException 的处理
 *
 * @author zzk
 * @date 2018/10/2
 */
@ControllerAdvice
public class MutationValidationHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(MutationValidateException.class)
    @ResponseBody
    ResponseEntity<?> handleControllerException(MutationValidateException ex) {
        ValidResponse response = new ValidResponse(ex.getError(), HttpStatus.BAD_REQUEST.value());
        return new ResponseEntity(response, HttpStatus.BAD_REQUEST);
    }


    /**
     * 错误输出的匿名内部类
     */
    private static class ValidResponse {
        private String message;

        private int code;

        private List<ParamInfo> paramErrors;

        ValidResponse(ValidSelectError validSelectError, int code) {
            this.paramErrors = validSelectError.getParamInfoList();
            this.code = code;
            this.message = validSelectError.getMessage();
        }

        public String getMessage() {
            return message;
        }

        public int getCode() {
            return code;
        }

        public Object getParamErrors() {
            return paramErrors;
        }
    }
}
