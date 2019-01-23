package com.qunchuang.carmall.service;

/**
 * @author Curtain
 * @date 2019/1/23 15:54
 */
public interface VerificationService {

    /**
     * 获取验证码
     *
     * @param phone
     * @return
     */
    String getCode(String phone);

    /**
     * 验证验证码是否正确
     * @param phone
     * @param code
     * @return
     */
    void verify(String phone, String code);
}
