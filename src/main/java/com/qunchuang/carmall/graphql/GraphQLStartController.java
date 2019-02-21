package com.qunchuang.carmall.graphql;

import cn.wzvtcsoft.validator.errors.ValidSelectError;
import cn.wzvtcsoft.validator.exceptions.MutationValidateException;
import com.qunchuang.carmall.graphql.errors.*;
import com.qunchuang.carmall.graphql.util.GraphqlExceptionHandlerUtil;
import graphql.ExceptionWhileDataFetching;
import graphql.ExecutionResult;
import graphql.ExecutionResultImpl;
import graphql.GraphQLError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
public class GraphQLStartController {

    @Autowired
    private GraphQLExecutor graphQLExecutor;

    @CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.OPTIONS}, maxAge = 1800L, allowedHeaders = "*")
    @RequestMapping(path = "/graphql")
    public ExecutionResult graphQl(@RequestBody GraphQLInputQuery graphQLInput, HttpServletResponse response) {
        ExecutionResult result = graphQLExecutor.execute(graphQLInput.getQuery(), graphQLInput.getArguments());
        Object data = result.getData();
        List<GraphQLError> errors = result.getErrors();
        Map<Object, Object> extensions = result.getExtensions();

        return new ExecutionResultBos(data, parseExceptions(errors, response), extensions);
    }

    /**
     * 针对各种异常进行解析并简化处理，并返回符合 GraphQL 规范的异常信息。
     * <p>
     * 主要处理的异常信息有3类:
     * 1. graphql 内置的异常信息 -- 原封不动的返回
     * 2. 业务异常 {@link BusinessException} -- 主要返回 code 和 message {@link BusinessExceptionError}
     * 3. 参数校验异常{@link MutationValidateException} -- 返回有且只有一个方法校验失败的信息 {@link MutationValidError}
     *
     * @param errors 要处理的异常信息
     * @return 返回符合 Graphql 规范的异常信息
     */
    private List<GraphQLError> parseExceptions(List<GraphQLError> errors, HttpServletResponse response) {
        List<GraphQLError> list = null;
        //针对不同类型的异常进行处理,因为 result.getErrors()是不可变集合
        if (errors != null && errors.size() != 0) {
            list = new ArrayList<>(errors.size());
            for (GraphQLError error : errors) {
                if (error instanceof ExceptionWhileDataFetching) {
                    ExceptionWhileDataFetching dataFetching = (ExceptionWhileDataFetching) error;
                    Throwable e = dataFetching.getException();
                    parseExceptionDeteail(list, error, e, response);
                } else {
                    list.add(error);
                }
            }
        }
        return list;
    }

    /**
     * 针对不同类型的异常进行不同的处理
     * <p>
     * 主要处理的异常有:
     * 1. 业务异常
     * 2. 校验异常
     * 3. 权限认证异常
     * 4. 实现 GExceptionHandler 的异常处理
     * <p>
     * 若不是在这些异常的范围之内，则打印异常的堆栈信息
     */
    private void parseExceptionDeteail(List<GraphQLError> list, GraphQLError error, Throwable e, HttpServletResponse response) {
        if (BusinessException.class.isAssignableFrom(e.getClass())) {
            list.add(new BusinessExceptionError(error, (BusinessException) e));
        } else if (MutationValidateException.class.isAssignableFrom(e.getClass())) {
            response.setStatus(400);
            ValidSelectError validSelectError = ((MutationValidateException) e).getError();
            list.add(new MutationValidError(error, validSelectError));
        } else if (AccessDeniedException.class.isAssignableFrom(e.getClass())) {
            //如果用户未登录  那么应该返回401  在用户登录的情况下才是403
            if (SecurityContextHolder.getContext().getAuthentication().getClass().isAssignableFrom(AnonymousAuthenticationToken.class)) {
                response.setStatus(401);
                list.add(new BusinessExceptionError(error, 401, "用户未登录"));
            } else {
                response.setStatus(403);
                list.add(new BusinessExceptionError(error, 403, "权限不足"));
            }
        } else if (AuthenticationException.class.isAssignableFrom(e.getClass())) {
            response.setStatus(401);
            list.add(new BusinessExceptionError(error, 401, "用户认证失败"));
        } else {

            GraphqlExceptionHandler handler = GraphqlExceptionHandlerUtil.getExceptionHandler(e);
            if (handler != null) {
                response.setStatus(handler.status());

                list.add(new ExceptionHandlerError(error, handler.body(e)));
            } else {
                response.setStatus(500);

                list.add(new ThrowableGraphqlError(error, e));
            }
        }
    }

    /**
     * 为了解决一个数据返回规范的问题，前端用graphql-cli/playground的时候，收到后端数据返回时如果发现有errors字段（不管是不是null，是不是为空数组）
     * 都会认为有错误；而ExecutionResultImpl里的toSpecification里是说如果不为null则肯定要返回，为null才不设置该字段），这两者之间有冲突
     * 到底谁对谁错后面需要研究清楚，然后给相应的项目提issue～ TODO
     * 这里先覆盖一个方法把问题先解决。
     */
    static final class ExecutionResultBos extends ExecutionResultImpl {

        ExecutionResultBos(Object data, List<? extends GraphQLError> errors, Map<Object, Object> extensions) {
            super(data, errors, extensions);
        }

        @Override
        public List<GraphQLError> getErrors() {
            List<GraphQLError> errors = super.getErrors();
            if (errors != null && errors.size() == 0) {
                return null;
            }
            return errors;
        }

    }


}