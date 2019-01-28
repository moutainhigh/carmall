package com.qunchuang.carmall.auth.phone;


import com.qunchuang.carmall.domain.Customer;
import com.qunchuang.carmall.exception.CarMallException;
import com.qunchuang.carmall.service.VerificationService;
import com.qunchuang.carmall.utils.SpringUtil;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import java.util.Map;

/**
 * @author Curtain
 * @date 2018/11/8:50
 */

public class PhoneAuthenticationProvider implements AuthenticationProvider {

    private PhoneUserInfo weChatMiniUserInfo;

    public PhoneAuthenticationProvider(PhoneUserInfo weChatMiniUserInfo) {
        this.weChatMiniUserInfo = weChatMiniUserInfo;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        PhoneAuthenticationToken authenticationToken = (PhoneAuthenticationToken) authentication;
        Map<String,String> verifyData = (Map<String, String>) authenticationToken.getPrincipal();
        VerificationService verificationService = (VerificationService) SpringUtil.getBean("verificationServiceImpl");

        try {
            verificationService.verify(verifyData.get("phone"),verifyData.get("code"));
        }catch (CarMallException e){
//            throw new BadCredentialsException("认证失败，验证码校验错误");
        }

        Customer customer = weChatMiniUserInfo.getCustomer(verifyData.get("phone"));

        return new PhoneAuthenticationToken(customer, true);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return PhoneAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
