package com.qunchuang.carmall.graphql;


import com.qunchuang.carmall.graphql.security.*;
import org.springframework.context.annotation.Bean;

/**
 * Graphql-JPA 启动时的自动配置
 *
 * @author zzk
 * @date 2018/09/27
 */

//因为框架已经在模块中  所以不需要再自动配置
//@Configuration
public class GraphqlAutoConfig {



    @Bean
    public GraphQLStartController graphQlController() {
        return new GraphQLStartController();
    }

    @Bean
    public GraphQLExecutor graphQLExecutor() {
        return new GraphQLExecutor();
    }

    @Bean
    public GraphQLInputQueryConverter graphQLInputQueryConverter() {
        return new GraphQLInputQueryConverter();
    }

    @Bean
    public RestAccessDeniedHandler restAccessDeniedHandler() {
        return new RestAccessDeniedHandler();
    }

    @Bean
    public RestAuthenticationEntryPoint restAuthenticationEntryPoint() {
        return new RestAuthenticationEntryPoint();
    }

    @Bean
    public RestAuthenticationFailureHandler restAuthenticationFailureHandler() {
        return new RestAuthenticationFailureHandler();
    }

    @Bean
    public RestAuthenticationSuccessHandler restAuthenticationSuccessHandler() {
        return new RestAuthenticationSuccessHandler();
    }

    @Bean
    public RestLogoutHandler restLogoutHandler() {
        return new RestLogoutHandler();
    }
}
