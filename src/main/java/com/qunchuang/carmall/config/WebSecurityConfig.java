package com.qunchuang.carmall.config;


import com.qunchuang.carmall.auth.app.AppAdminInfo;
import com.qunchuang.carmall.auth.app.AppAuthenticationFilter;
import com.qunchuang.carmall.auth.app.AppAuthenticationProvider;
import com.qunchuang.carmall.auth.phone.PhoneAuthenticationFilter;
import com.qunchuang.carmall.auth.phone.PhoneAuthenticationProvider;
import com.qunchuang.carmall.auth.phone.PhoneUserInfo;
import com.qunchuang.carmall.auth.web.WebAdminInfo;
import com.qunchuang.carmall.auth.web.WebAuthenticationFilter;
import com.qunchuang.carmall.auth.web.WebAuthenticationProvider;
import com.qunchuang.carmall.auth.wechat.WeChatMiniAuthenticationFilter;
import com.qunchuang.carmall.auth.wechat.WeChatMiniAuthenticationProvider;
import com.qunchuang.carmall.auth.wechat.WeChatMiniUserInfo;
import com.qunchuang.carmall.graphql.security.RestAccessDeniedHandler;
import com.qunchuang.carmall.graphql.security.RestAuthenticationEntryPoint;
import com.qunchuang.carmall.graphql.security.RestAuthenticationFailureHandler;
import com.qunchuang.carmall.graphql.security.RestLogoutHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CompositeFilter;

import javax.servlet.Filter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * Security配置
 *
 * @author Curtain
 * @date 2018/1/14 9:10
 */

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private WeChatMiniResources weChatMiniResources;

    @Autowired
    private WeChatMiniUserInfo weChatMiniUserInfo;

    @Autowired
    private PhoneUserInfo phoneUserInfo;

    @Autowired
    private RestAuthenticationEntryPoint restAuthenticationEntryPoint;

    @Autowired
    private MyAuthenticationSuccessHandler restAuthenticationSuccessHandler;

    @Autowired
    private RestAuthenticationFailureHandler restAuthenticationFailureHandler;

    @Autowired
    private RestAccessDeniedHandler restAccessDeniedHandler;

    @Autowired
    private RestLogoutHandler restLogoutHandler;

    @Autowired
    private WebAdminInfo webAdminInfo;

    @Autowired
    private AppAdminInfo appAdminInfo;

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        AuthenticationManager am = this.authenticationManager();
        http.cors().and()
                .csrf().disable()
                .addFilterBefore(ssoFilter(am), BasicAuthenticationFilter.class)
                .exceptionHandling()
                .authenticationEntryPoint(restAuthenticationEntryPoint)
                .accessDeniedHandler(restAccessDeniedHandler)
                .and()
                .authorizeRequests()
                .antMatchers("/**").authenticated()
                .antMatchers("/login/weChatMini", "/graphql", "/login/phone", "login/web","/login/app","/sts","/getCode","/jsapisignature","/initAccount").permitAll()

//                .and()
//                .formLogin()
//                .loginPage("/login")
//                .successHandler(restAuthenticationSuccessHandler)
//                .failureHandler(restAuthenticationFailureHandler)
                .and()
                .logout().logoutSuccessHandler(restLogoutHandler);

        /*避免用户多地登录*/
        http.sessionManagement().maximumSessions(1);

    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
//        auth.userDetailsService(this.adminService)
//                .passwordEncoder(myPasswordEncoder).
//                and()
        auth.authenticationProvider(new WeChatMiniAuthenticationProvider(weChatMiniResources, weChatMiniUserInfo))
                .authenticationProvider(new PhoneAuthenticationProvider(phoneUserInfo))
                .authenticationProvider(new WebAuthenticationProvider(webAdminInfo))
                .authenticationProvider(new AppAuthenticationProvider(appAdminInfo));
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST"));
        configuration.addAllowedHeader("x-auth-token");
        configuration.addExposedHeader("x-auth-token");
        configuration.addAllowedHeader("content-type");
        configuration.setAllowCredentials(false);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    private Filter ssoFilter(AuthenticationManager am) {
        CompositeFilter compositeFilter = new CompositeFilter();
        List<Filter> filters = new ArrayList<>();

        //todo  如果不使用微信认证 那么之后取消
        //微信验证过滤
        addFilter(am, filters, new WeChatMiniAuthenticationFilter());

        //手机号验证过滤
        addFilter(am, filters, new PhoneAuthenticationFilter());

        //Web验证过滤
        addFilter(am, filters, new WebAuthenticationFilter());

        //app登录过滤
        addFilter(am,filters,new AppAuthenticationFilter());

        compositeFilter.setFilters(filters);
        return compositeFilter;
    }

    private void addFilter(AuthenticationManager am, List<Filter> filters, AbstractAuthenticationProcessingFilter filter) {
        filter.setAuthenticationManager(am);
        filter.setAuthenticationSuccessHandler(new MyAuthenticationSuccessHandler());
        filter.setAuthenticationFailureHandler(new SimpleUrlAuthenticationFailureHandler());
        filters.add(filter);
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        //解决静态资源被拦截的问题
        web.ignoring().mvcMatchers("/graphqlapi/**");
    }
}
