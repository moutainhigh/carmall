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
import java.util.HashMap;
import java.util.Map;

/**
 * @author Curtain
 * @date 2018/1/14 8:10
 */

public class PhoneAuthenticationFilter extends AbstractAuthenticationProcessingFilter {


    public static final String SPRING_SECURITY_FORM_CODE_KEY = "code";
    public static final String SPRING_SECURITY_FORM_PHONE_KEY = "phone";


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

        Map<String,String> data = obtainData(request);

        PhoneAuthenticationToken authRequest = new PhoneAuthenticationToken(data, false);

        setDetails(request, authRequest);

        return this.getAuthenticationManager().authenticate(authRequest);
    }


    protected Map<String,String> obtainData(HttpServletRequest request) {
        Map<String,String>  data = new HashMap<>();
        try {
            InputStream inputStream = request.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            String line = br.readLine();
            Map<String,String> map = (Map) JSON.parse(line);

            data.put("phone",map.get("phone"));
            data.put("code",map.get("code"));

        } catch (IOException e) {
            throw new BadCredentialsException("bad authentication");
        }
        return data;
    }

    protected void setDetails(HttpServletRequest request,
                              PhoneAuthenticationToken authRequest) {
        authRequest.setDetails(authenticationDetailsSource.buildDetails(request));
    }

}
