package com.qunchuang.carmall.graphql.mutation;

import cn.wzvtcsoft.validator.anntations.MutationValidated;
import cn.wzvtcsoft.validator.core.MutationValidator;
import cn.wzvtcsoft.validator.core.impl.MutationValidationMetaInfoImpl;
import cn.wzvtcsoft.validator.core.impl.MutationValidatorImpl;
import com.qunchuang.carmall.graphql.IGraphQlTypeMapper;
import com.qunchuang.carmall.graphql.annotation.GraphqlController;
import com.qunchuang.carmall.graphql.annotation.GraphqlMutation;
import graphql.schema.*;
import com.qunchuang.carmall.graphql.util.MethodParameterUtil;
import com.qunchuang.carmall.graphql.util.SchemaParseUtil;
import org.springframework.core.ResolvableType;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.persistence.metamodel.EntityType;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;


/**
 * mutation方法调用到controller下的那些方法的各种元数据的封装
 */
public class DefaultMutationMetaInfo implements MutationMetaInfo {
    private static MutationValidator validator = new MutationValidatorImpl();

    private IGraphQlTypeMapper graphQlTypeMapper;

    private Object target;
    private Method method;
    /**
     * Schema 的方法签名
     */
    private String mutationFieldName;
    private GraphQLOutputType graphQLOutputType;
    private EntityType entityType;

    private String[] argumentNames;
    private Map<String, Annotation[]> arguAnnotationsMap = new HashMap<>();
    private Map<String, GraphQLArgument> arguGraphQLArguments = new HashMap<>();


    public DefaultMutationMetaInfo(Object target, Method proxyMethod, IGraphQlTypeMapper graphQlTypeMapper) {
        this.graphQlTypeMapper = graphQlTypeMapper;
        this.target = target;
        this.method = proxyMethod;


        mutationMethodMap(proxyMethod);

        boolean isCollectionReturnValue = false;


        Type type = proxyMethod.getReturnType();
        if (Collection.class.isAssignableFrom(proxyMethod.getReturnType()) && proxyMethod.getGenericReturnType() instanceof ParameterizedType) {
            type = ((ParameterizedType) proxyMethod.getGenericReturnType()).getActualTypeArguments()[0];
            isCollectionReturnValue = true;
        }
        this.entityType = this.graphQlTypeMapper.getEntityType((Class) type);
        this.graphQLOutputType = !isCollectionReturnValue ? graphQlTypeMapper.getGraphQLOutputType(type) :
                entityType != null ? new GraphQLTypeReference(graphQlTypeMapper.getGraphQLTypeNameOfEntityList(this.entityType)) : new GraphQLList(graphQlTypeMapper.getGraphQLOutputType(type));

        this.argumentNames = MethodParameterUtil.getParamNames(proxyMethod).get();
        Parameter[] parameters = proxyMethod.getParameters();

        boolean[] paramRequire = new boolean[parameters.length];
        for (int i = 0; i < paramRequire.length; i++) {
            paramRequire[i] = true;
        }
        for (int i = 0; i < parameters.length; i++) {
            if (parameters[i].isAnnotationPresent(RequestParam.class)) {
                RequestParam rp = parameters[i].getAnnotation(RequestParam.class);
                paramRequire[i] = rp.required();
                if (StringUtils.hasText(rp.value())) {
                    argumentNames[i] = rp.value();
                } else if (StringUtils.hasText(rp.name())) {
                    argumentNames[i] = rp.name();
                }
            }
        }


        Annotation[][] annotations = proxyMethod.getParameterAnnotations();
        for (int i = 0; i < argumentNames.length; i++) {
            this.arguAnnotationsMap.put(argumentNames[i], annotations[i]);
        }


        //todo 将 param -> GraphQLParam 需要封装
        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            String typeName = argumentNames[i];

            boolean isCollection = false;

            //todo : 若 Collection 非泛型或者是 Collection<Object> 则无法处理
            Class typeClazz = parameter.getType();
            if (List.class.isAssignableFrom(typeClazz)) {
                isCollection = true;
                Type genericSuperclass = parameter.getParameterizedType();
                typeClazz = ResolvableType.forType(genericSuperclass).resolveGeneric(0);
            } else if (Collection.class.isAssignableFrom(typeClazz) || parameter.isVarArgs()) {
                throw new RuntimeException("Controller 的参数只接受 List");
            }

            GraphQLInputType graphQLObjectInputType = this.graphQlTypeMapper.getGraphQLInputType(typeClazz);
            graphQLObjectInputType = isCollection ? new GraphQLList(graphQLObjectInputType) : graphQLObjectInputType;
            graphQLObjectInputType = paramRequire[i] ? GraphQLNonNull.nonNull(graphQLObjectInputType) : graphQLObjectInputType;
            GraphQLArgument graphQLArgument = GraphQLArgument.newArgument()
                    .name(typeName)
                    .type(graphQLObjectInputType)
                    .description(SchemaParseUtil.methodSchema(method, parameter))
                    .build();
            this.arguGraphQLArguments.put(typeName, graphQLArgument);
        }


    }

    /**
     * 将目标的 mutation method 的方法名进行映射
     *
     * @param proxyMethod 指定的 Mutation Method
     */
    private void mutationMethodMap(Method proxyMethod) {
        Class currentClass = proxyMethod.getDeclaringClass();
        while (!Object.class.equals(currentClass) && AnnotationUtils.getAnnotation(currentClass, GraphqlController.class) == null) {
            currentClass = currentClass.getSuperclass();
        }

        String grc = AnnotationUtils.findAnnotation(currentClass, GraphqlController.class).value();
        String grm = getMethodPath(proxyMethod);
        this.mutationFieldName = ("/" + grc + grm).replace("//", "/").replace("/", "_").substring(1);

    }


    @Override
    public Object invoke(Map<String, Object> nameArgMaps) {
        final Map<String, Object> argmaps = this.populateDefaultValueIfNecessary(nameArgMaps);
        Object[] args = Arrays.stream(this.argumentNames).map(argname -> argmaps.get(argname)).toArray();


        if (this.target.getClass().isAnnotationPresent(MutationValidated.class)) {
            validator.validate(new MutationValidationMetaInfoImpl(this.method, args, this.target));
        }

        return ReflectionUtils.invokeMethod(this.method, this.target, args);

    }

    /**
     * 有可能为null，如果不存在的话
     */
    @Override
    public GraphQLArgument getGraphQLArgument(String arguName) {
        return this.arguGraphQLArguments.get(arguName);
    }

    @Override
    public GraphQLOutputType getGraphQLOutputType() {
        return this.graphQLOutputType;
    }

    @Override
    public List<GraphQLArgument> getGraphQLArgumentList() {
        return this.arguGraphQLArguments.entrySet().stream().map(entry -> entry.getValue()).collect(Collectors.toList());
    }

    @Override
    public EntityType getEntityType() {
        return this.entityType;
    }

    @Override
    public String getMutationFieldName() {
        return this.mutationFieldName;
    }

    protected Map<String, Object> populateDefaultValueIfNecessary(Map<String, Object> nameArgMaps) {
        return nameArgMaps;
    }


    private static String getMethodPath(Method proxyMethod) {
        if (proxyMethod.isAnnotationPresent(PostMapping.class)) {
            return AnnotationUtils.findAnnotation(proxyMethod, PostMapping.class).path()[0];
        } else if (proxyMethod.isAnnotationPresent(PutMapping.class)) {
            return AnnotationUtils.findAnnotation(proxyMethod, PutMapping.class).path()[0];
        } else if (proxyMethod.isAnnotationPresent(DeleteMapping.class)) {
            return AnnotationUtils.findAnnotation(proxyMethod, DeleteMapping.class).path()[0];
        } else if (proxyMethod.isAnnotationPresent(GetMapping.class)) {
            return AnnotationUtils.findAnnotation(proxyMethod, GetMapping.class).path()[0];
        } else if (proxyMethod.isAnnotationPresent(PatchMapping.class)) {
            return AnnotationUtils.findAnnotation(proxyMethod, PatchMapping.class).path()[0];
        } else if (proxyMethod.isAnnotationPresent(GraphqlMutation.class)) {
            GraphqlMutation annotation = AnnotationUtils.findAnnotation(proxyMethod, GraphqlMutation.class);
            if (annotation.value().length == 0 || annotation.path().length == 0) {
                return "_" + proxyMethod.getName();
            }
            return annotation.path()[0];
        }
        throw new RuntimeException("该方法不是 Controller 的对外接口方法");
    }

}




