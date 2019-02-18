package com.qunchuang.carmall.config;

import com.alibaba.fastjson.JSON;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Curtain
 * @date 2018/1/14 9:00
 */

@Component("myAuthenticationSuccessHandler")
public class MyAuthenticationSuccessHandler
        extends SimpleUrlAuthenticationSuccessHandler {

    private RequestCache requestCache = new HttpSessionRequestCache();

    MyAuthenticationSuccessHandler() {
        super();
    }

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication)
            throws ServletException, IOException {

        SavedRequest savedRequest
                = requestCache.getRequest(request, response);

        if (savedRequest == null) {
            clearAuthenticationAttributes(request);
            //返回文本数据
            response.setContentType("application/json;charset=utf-8");
            ServletOutputStream outputStream = response.getOutputStream();
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            outputStream.write(JSON.toJSONString(principal).getBytes());
            outputStream.flush();
            outputStream.close();

        }

    }

    public void setRequestCache(RequestCache requestCache) {
        this.requestCache = requestCache;
    }
}