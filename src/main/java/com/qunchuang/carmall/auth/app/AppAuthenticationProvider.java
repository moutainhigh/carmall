package com.qunchuang.carmall.auth.app;


import com.qunchuang.carmall.domain.Admin;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

/**
 * @author Curtain
 * @date 2018/11/29 8:50
 */

public class AppAuthenticationProvider implements AuthenticationProvider {

    private AppAdminInfo webAdminInfo;

    public AppAuthenticationProvider(AppAdminInfo webAdminInfo){
        this.webAdminInfo = webAdminInfo;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        AppAuthenticationToken authenticationToken = (AppAuthenticationToken) authentication;

        String username = (String) authentication.getPrincipal();
        String password = (String) authentication.getCredentials();

        Admin admin = webAdminInfo.getAdmin(username);

        if (!(admin.salesAdmin()||admin.storeAdmin())){
            //平台管理员  超级管理员账号不允许登录。
            throw new BadCredentialsException("错误账号");
        }

        if (!password.equals(admin.getPassword())){
            throw new BadCredentialsException("密码错误");
        }

        return new AppAuthenticationToken(admin,password,admin.getAuthorities());

    }

    @Override
    public boolean supports(Class<?> authentication) {
        return AppAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
