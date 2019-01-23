package com.qunchuang.carmall.graphql;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.qunchuang.carmall.graphql.annotation.GraphqlController;
import com.qunchuang.carmall.graphql.annotation.GraphqlMutation;
import com.qunchuang.carmall.graphql.mutation.MutationReturnInstrumentation;
import graphql.ExecutionInput;
import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.execution.TypeFromAST;
import graphql.execution.preparsed.PreparsedDocumentEntry;
import graphql.language.Document;
import graphql.language.NodeUtil;
import graphql.language.OperationDefinition;
import graphql.language.Type;
import graphql.parser.Parser;
import graphql.schema.GraphQLScalarType;
import graphql.schema.GraphQLSchema;
import graphql.schema.GraphQLType;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.aop.support.AopUtils;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * A GraphQL executor capable of constructing a {@link GraphQLSchema} from a JPA {@link EntityManager}. The executor
 * uses the constructed schema to execute queries directly from the JPA data source.
 * <p>
 * If the executor is given a mutator function, it is feasible to manipulate the {@link GraphQLSchema}, introducing
 * the option to add mutations, subscriptions etc.
 */

//todo : 最后需要允许 值对象 与 DTO 的存在
@Component
public class GraphQLExecutor implements ApplicationListener, IGraphQLExecutor {

    @Resource
    private EntityManager entityManager;


    private GraphQL graphQL;
    private GraphQLSchema graphQLSchema;
    private GraphQLSchema.Builder builder;
    private Cache<String, PreparsedDocumentEntry> cache;


    Map<Class, GraphQLScalarType> customGraphQLScalarTypeMap;

    public GraphQLExecutor() {
    }

    /**
     * Creates a read-only GraphQLExecutor using the entities discovered from the given {@link EntityManager}.
     *
     * @param entityManager The entity manager from which the JPA classes annotated with
     *                      {@link javax.persistence.Entity} is extracted as {@link GraphQLSchema} objects.
     */
    public GraphQLExecutor(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public GraphQLExecutor(EntityManager entityManager, Map<Class, GraphQLScalarType> customGraphQLScalarTypeMap) {
        this.entityManager = entityManager;
        this.customGraphQLScalarTypeMap = customGraphQLScalarTypeMap;

    }

    private synchronized void createGraphQL(ApplicationContext listableBeanFactory) {

        Map<Method, Object> methodTargetMap = new HashMap<>();

        Collection<Object> controllers = listableBeanFactory.getBeansWithAnnotation(GraphqlController.class).values();
        for (Object controller : controllers) {
            if (AopUtils.isAopProxy(controller)) {
                controller = AopProxyUtils.getSingletonTarget(controller);
            }
            Object finalController = controller;
            Arrays.stream(controller.getClass().getMethods())
                    .filter(method -> method.isAnnotationPresent(PostMapping.class)
                            || method.isAnnotationPresent(PutMapping.class)
                            || method.isAnnotationPresent(GetMapping.class)
                            || method.isAnnotationPresent(DeleteMapping.class)
                            || method.isAnnotationPresent(PatchMapping.class)
                            || method.isAnnotationPresent(GraphqlMutation.class))
                    .forEach(method -> methodTargetMap.put(method, finalController));
        }

        if (entityManager != null) {
            if (builder == null && this.customGraphQLScalarTypeMap == null) {
                this.builder = new GraphQLSchemaBuilder(entityManager, methodTargetMap);
            } else if (builder == null) {
                this.builder = new GraphQLSchemaBuilder(entityManager, methodTargetMap, customGraphQLScalarTypeMap);
            }
            this.graphQLSchema = builder.build();
            this.cache = Caffeine.newBuilder().maximumSize(10_000).build();
            this.graphQL = GraphQL.newGraphQL(graphQLSchema).preparsedDocumentProvider(cache::get).instrumentation(new MutationReturnInstrumentation()).build();
        }
    }


    /**
     * @return The {@link GraphQLSchema} used by this executor.
     */
    public GraphQLSchema getGraphQLSchema() {
        return graphQLSchema;
    }

    @Override
    public ExecutionResult execute(String query, Map<String, Object> arguments) {
        return innerExecute(query, arguments);
    }

    @Override
    public IGraphQlTypeMapper getGraphQlTypeMapper() {
        return (GraphQLSchemaBuilder) this.builder;
    }

    public ExecutionResult innerExecute(String query, Map<String, Object> arguments) {
        if (arguments == null) {
            return graphQL.execute(query);
        }
        return graphQL.execute(ExecutionInput.newExecutionInput().query(query).variables(arguments).build());
    }

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        if (event instanceof ApplicationStartedEvent) {
            try {
                this.createGraphQL(((ApplicationStartedEvent) event).getApplicationContext());
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("初始化graphql字段和类型不成功！");
            }

        }
    }

    @Override
    public GraphQLType getGraphQLType(Type type) {
        return TypeFromAST.getTypeFromAST(this.graphQLSchema, type);
    }

    @Override
    public OperationDefinition getOperationDefinition(String query) {
        PreparsedDocumentEntry entry = this.cache.getIfPresent(query);
        Document doc;
        if (entry != null) {
            doc = entry.getDocument();
        } else {
            try {
                doc = new Parser().parseDocument(query);
            } catch (ParseCancellationException e) {
                e.printStackTrace();
                throw new RuntimeException("GraphQLExecutor.getOperationDefinition...");
            }
        }
        return NodeUtil.getOperation(doc, null).operationDefinition;
    }

}
