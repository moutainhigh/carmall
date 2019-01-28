package com.qunchuang.carmall.auth.phone;


import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.SpringSecurityCoreVersion;

/**
 * 小程序认证信息Principal
 *
 * @author Curtain
 * @date 2018/11/8:42
 */

public class PhoneAuthenticationToken extends AbstractAuthenticationToken {


    private static final long serialVersionUID = SpringSecurityCoreVersion.SERIAL_VERSION_UID;
    private final Object principal;


    public PhoneAuthenticationToken(Object principal, boolean b) {
        super(null);
        this.principal = principal;
        setAuthenticated(b);
    }


    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return this.principal;
    }
}
