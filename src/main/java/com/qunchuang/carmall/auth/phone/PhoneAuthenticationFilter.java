package com.qunchuang.carmall.auth.phone;


import com.alibaba.fastjson.JSON;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

/**
 * 手机号登录
 * 参数   phone   手机号
 *       code     验证码
 *       type     类型  注册 or 登录
 *       invitedId 邀请人Id
 * @author Curtain
 * @date 2018/1/14 8:10
 */

public class PhoneAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    private boolean postOnly = true;

    public PhoneAuthenticationFilter() {
        super(new AntPathRequestMatcher("/login/phone", "POST"));
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response) throws AuthenticationException {
        if (postOnly && !request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException(
                    "Authentication method not supported: " + request.getMethod());
        }

        Map<String, String> data = obtainData(request);

        if (data==null){
            throw new BadCredentialsException("bad Credentials");
        }

        PhoneAuthenticationToken authRequest = new PhoneAuthenticationToken(data, false);

        setDetails(request, authRequest);

        return this.getAuthenticationManager().authenticate(authRequest);
    }


    protected Map<String, String> obtainData(HttpServletRequest request) {
        Map<String, String> data;
        try {
            InputStream inputStream = request.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder sb = new StringBuilder();
            String line;

            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            data = (Map<String, String>) JSON.parse(sb.toString());

        } catch (IOException e) {
            throw new BadCredentialsException("bad Credentials");
        }
        return data;
    }

    protected void setDetails(HttpServletRequest request,
                              PhoneAuthenticationToken authRequest) {
        authRequest.setDetails(authenticationDetailsSource.buildDetails(request));
    }

}
