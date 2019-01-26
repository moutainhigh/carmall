package com.qunchuang.carmall.auth;


import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Curtain
 * @date 2018/1/14 8:10
 */

public class WeChatMiniAuthenticationFilter extends AbstractAuthenticationProcessingFilter {


    public static final String SPRING_SECURITY_FORM_CODE_KEY = "code";

    private boolean postOnly = true;

    public WeChatMiniAuthenticationFilter() {
        super(new AntPathRequestMatcher("/login/weChatMini", "POST"));
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response) throws AuthenticationException {
        if (postOnly && !request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException(
                    "Authentication method not supported: " + request.getMethod());
        }

        String code = obtainCode(request);

        if (code == null) {
            code = "";
        }

        code = code.trim();

        WeChatMiniAuthenticationToken authRequest = new WeChatMiniAuthenticationToken(code, false);

        setDetails(request, authRequest);

        return this.getAuthenticationManager().authenticate(authRequest);
    }


    protected String obtainCode(HttpServletRequest request) {
        String code = request.getParameter(SPRING_SECURITY_FORM_CODE_KEY);
        return code;
    }

    protected void setDetails(HttpServletRequest request,
                              WeChatMiniAuthenticationToken authRequest) {
        authRequest.setDetails(authenticationDetailsSource.buildDetails(request));
    }

}
