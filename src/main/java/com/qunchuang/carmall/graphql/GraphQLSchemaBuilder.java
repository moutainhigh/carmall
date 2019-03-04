package com.qunchuang.carmall.graphql;

import cn.wzvtcsoft.bosdomain.BosEntity;
import cn.wzvtcsoft.bosdomain.ICoreObject;
import cn.wzvtcsoft.bosdomain.enums.BosEnum;
import cn.wzvtcsoft.bosdomain.util.BostypeUtils;
import cn.wzvtcsoft.bosdomain.util.EntityEnumUtil;
import cn.wzvtcsoft.validator.anntations.*;
import cn.wzvtcsoft.validator.anntations.ValidSelect;
import com.qunchuang.carmall.graphql.annotation.GraphQLIgnore;
import com.qunchuang.carmall.graphql.enums.FieldNullEnum;
import com.qunchuang.carmall.graphql.mutation.DefaultMutationMetaInfo;
import com.qunchuang.carmall.graphql.mutation.MutationDataFetcher;
import com.qunchuang.carmall.graphql.mutation.MutationMetaInfo;
import com.qunchuang.carmall.graphql.query.CollectionJpaDataFetcher;
import com.qunchuang.carmall.graphql.query.JpaDataFetcher;
import com.qunchuang.carmall.graphql.query.Paginator;
import com.qunchuang.carmall.graphql.query.QueryFilter;
import com.qunchuang.carmall.graphql.query.enums.OrderByDirection;
import com.qunchuang.carmall.graphql.query.enums.QueryFilterCombinator;
import com.qunchuang.carmall.graphql.query.enums.QueryFilterOperator;
import com.qunchuang.carmall.graphql.util.SchemaParseUtil;
import graphql.Scalars;
import com.qunchuang.carmall.graphql.annotation.SchemaDocumentation;
import graphql.schema.*;
import org.springframework.beans.BeanUtils;
import org.springframework.core.annotation.AnnotationUtils;

import javax.persistence.EntityManager;
import javax.persistence.metamodel.*;
import java.lang.reflect.Type;
import java.lang.reflect.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static graphql.schema.GraphQLInputObjectField.newInputObjectField;
import static graphql.schema.GraphQLInputObjectType.newInputObject;
import static graphql.schema.GraphQLObjectType.newObject;

/**
 * A wrapper for the {@link GraphQLSchema.Builder}. In addition to exposing the traditional builder functionality,
 * this class constructs an initial {@link GraphQLSchema} by scanning the given {@link EntityManager} for relevant
 * JPA entities. This happens at construction time.
 * <p>
 * Note: This class should not be accessed outside this library.
 */

public class GraphQLSchemaBuilder extends GraphQLSchema.Builder implements IGraphQlTypeMapper {

    public static final String PAGINATION_REQUEST_PARAM_NAME = "paginator";
    public static final String QFILTER_REQUEST_PARAM_NAME = "qfilter";

    private static final String MUTATION_INPUTTYPE_POSTFIX = "_";
    private static final String ENTRY_PARENT_PROPNAME = "parent";
    private static final String ENTITY_LIST_NAME = "List";
    private static final String[] QUERY_OUTPUT_FILTER_PROPS = {"id"};
    /**
     * mutation下的输入entity类型中，需要忽略以下字段
     */
    private static final Set<String> ENTITYPROP_SET_SHOULDBEIGNORED_IN_MUTATION_ARGUMENT = new HashSet(Arrays.asList(
            "parent",
            "createtime",
            "updatetime",
            "createactorid",
            "updateactorid"));

    private final EntityManager entityManager;

    /**
     * Mutation method 与 Controller 的映射
     */
    private final Map<Method, Object> methodTargetMap = new HashMap<>();

    private final Map<Class, GraphQLScalarType> classGraphQlScalarTypeMap = new HashMap<>();
    private final Map<Class<? extends BosEnum>, GraphQLEnumType> enumClassGraphQlEnumTypeMap = new HashMap<>();
    /**
     * 所有的JPA Entity，Embeddable 对应的GraphQLType，包含GraphQLOutputObjectType和GraphQLInputObjectType两个类型，而且也也只有这两个类型
     */
    private final Map<GraphQLType, ManagedType> graphQlTypeManagedTypeClassMap = new HashMap<>();
    /**
     * 要用到的常见用来输入的非实体类。
     */
    private final Map<Class, GraphQLInputType> dtoClassGraphQlInputTypeMap = new HashMap<>();

    /**
     * Initialises the builder with the given {@link EntityManager} from which we immediately start to scan for
     * entities to include in the GraphQL schema.
     *
     * @param entityManager The manager containing the data models to include in the final GraphQL schema.
     */
    public GraphQLSchemaBuilder(EntityManager entityManager, Map<Method, Object> methodTargetMap) {
        this(entityManager, methodTargetMap, null);
    }

    public GraphQLSchemaBuilder(EntityManager entityManager, Map<Method, Object> methodTargetMap, Map<Class, GraphQLScalarType> customGraphQLScalarTypeMap) {
        this.entityManager = entityManager;
        this.classGraphQlScalarTypeMap.put(String.class, Scalars.GraphQLString);
        this.classGraphQlScalarTypeMap.put(Integer.class, Scalars.GraphQLInt);
        this.classGraphQlScalarTypeMap.put(int.class, Scalars.GraphQLInt);
        this.classGraphQlScalarTypeMap.put(float.class, Scalars.GraphQLFloat);
        this.classGraphQlScalarTypeMap.put(Float.class, Scalars.GraphQLFloat);
        this.classGraphQlScalarTypeMap.put(double.class, Scalars.GraphQLFloat);
        this.classGraphQlScalarTypeMap.put(Double.class, Scalars.GraphQLFloat);
        this.classGraphQlScalarTypeMap.put(long.class, Scalars.GraphQLLong);
        this.classGraphQlScalarTypeMap.put(Long.class, Scalars.GraphQLLong);
        this.classGraphQlScalarTypeMap.put(boolean.class, Scalars.GraphQLBoolean);
        this.classGraphQlScalarTypeMap.put(Boolean.class, Scalars.GraphQLBoolean);
        this.classGraphQlScalarTypeMap.put(BigDecimal.class, Scalars.GraphQLBigDecimal);
        this.classGraphQlScalarTypeMap.put(short.class, Scalars.GraphQLShort);
        this.classGraphQlScalarTypeMap.put(Short.class, Scalars.GraphQLShort);
        this.classGraphQlScalarTypeMap.put(UUID.class, JavaScalars.GraphQLUUID);
        this.classGraphQlScalarTypeMap.put(Date.class, JavaScalars.GraphQLDate);
        this.classGraphQlScalarTypeMap.put(LocalDateTime.class, JavaScalars.GraphQLLocalDateTime);
        this.classGraphQlScalarTypeMap.put(Instant.class, JavaScalars.GraphQLInstant);
        this.classGraphQlScalarTypeMap.put(LocalDate.class, JavaScalars.GraphQLLocalDate);
        Optional.ofNullable(customGraphQLScalarTypeMap).ifPresent(map -> this.classGraphQlScalarTypeMap.putAll(map));

        this.methodTargetMap.putAll(methodTargetMap);
        this.enumClassGraphQlEnumTypeMap.put(QueryFilterCombinator.class, QUERY_FILTER_COMBINATOR_ENUM);
        this.enumClassGraphQlEnumTypeMap.put(QueryFilterOperator.class, QUERY_FILTER_OPERATOR_ENUM);
        this.enumClassGraphQlEnumTypeMap.put(OrderByDirection.class, ORDER_BY_DIRECTION_ENUM);
        this.enumClassGraphQlEnumTypeMap.put(FieldNullEnum.class, FIELD_NULL_ENUM);


        this.dtoClassGraphQlInputTypeMap.put(QueryFilter.class, getDtoInputType(QFILTER_REQUEST_PARAM_NAME, QueryFilter.class));
        this.dtoClassGraphQlInputTypeMap.put(Paginator.class, getDtoInputType(PAGINATION_REQUEST_PARAM_NAME, Paginator.class));

        this.prepareArgumentInputTypeForMutations();
        super.query(getQueryType()).mutation(getMutationType());
    }

    /**
     * 简单的DTO输入类型的类型
     */
    private GraphQLInputObjectType getDtoInputType(String graphQLInputObjectTypeName, Class clazz) {
        return new GraphQLInputObjectType(graphQLInputObjectTypeName, AnnotationUtils.findAnnotation(clazz, SchemaDocumentation.class).value()
                , Arrays.stream(clazz.getMethods()).filter(method -> AnnotationUtils.findAnnotation(method, SchemaDocumentation.class) != null)
                .map(method -> {
                    Class propType = BeanUtils.findPropertyForMethod(method).getPropertyType();
                    String propName = BeanUtils.findPropertyForMethod(method).getName();
                    String propDoc = AnnotationUtils.findAnnotation(method, SchemaDocumentation.class).value();
                    return newInputObjectField().name(propName).description(propDoc).type(
                            propType == clazz ? GraphQLTypeReference.typeRef(graphQLInputObjectTypeName) : this.getGraphQLInputType(propType)
                    ).build();
                }).collect(Collectors.toList()));
    }

    /**
     * 把在mutation中可能会用到的输入类型放进去，这与在查询中用到的参数输入类型有所不同，在名称上他们相差一个后缀，在结构上query参数类型只包含标量，而mutation参数类型则包含嵌套对象（除了parent属性之外）
     * 根据实体模型获取所有可能的mutationInputType。
     */
    private void prepareArgumentInputTypeForMutations() {
//根据java类型去重
        Stream.concat(this.entityManager.getMetamodel().getEmbeddables().stream(), this.entityManager.getMetamodel().getEntities().stream())
                .filter(this::isNotIgnored).filter(BostypeUtils.distinctByKey(o -> o.getJavaType()))
                .forEach(type -> {
                    GraphQLInputType inputObjectType = newInputObject()
                            .name(type.getJavaType().getSimpleName() + MUTATION_INPUTTYPE_POSTFIX)
                            .description(getSchemaDocumentation(type.getJavaType()))
                            .fields(type.getAttributes().stream()
                                    //去掉忽略属性
                                    .filter(this::isNotIgnoredForEntityInput)
                                    .map(attribute -> newInputObjectField()
                                            .name(attribute.getName())
                                            .description(getFieldDocumentation(attribute.getJavaMember()))
                                            .type((GraphQLInputType) getAttributeGrahQLType(attribute, true))
                                            .build())
                                    //根据字段属性获取对应字段。GraphQLInputTypeObjectFiled
                                    .collect(Collectors.toList()))
                            .build();
                    this.graphQlTypeManagedTypeClassMap.put(inputObjectType, type);
                    //添加到特别类型中，以便最终能做typereference的解析。
                    this.additionalType(inputObjectType);
                });
    }

    /**
     * @return A freshly built {@link GraphQLSchema}
     * @deprecated Use {@link #build()} instead.
     */
    @Deprecated()
    public GraphQLSchema getGraphQLSchema() {
        return super.build();
    }

    GraphQLObjectType getQueryType() {
        GraphQLObjectType.Builder queryType = newObject().name("QueryType_JPA")
                .description("DDD领域模型下的JPA查询,所有类型均有createtime（创建时间属性),updatedtime(修改时间属性)");
        queryType.fields(
                Stream.concat(entityManager.getMetamodel().getEntities().stream(), entityManager.getMetamodel().getEmbeddables().stream())
                        .filter(this::isNotIgnored).map(this::getQueryFieldDefinition)
                        //TODO 排除分录和值对象.他们不应该是顶级的，无法从此处开始查询数据，必须从顶级实体开始查询。
                        .collect(Collectors.toList()));

        queryType.fields(entityManager.getMetamodel().getEntities().stream().filter(this::isNotIgnored).map(this::getQueryFieldPageableDefinition).collect(Collectors.toList()));
        return queryType.build();
    }

    private GraphQLObjectType getMutationType() {
        GraphQLObjectType.Builder queryType = newObject().name("Mutation_SpringMVC").description("将所有的SpringMVC.Controller中的Requestmapping方法暴露出来了");

        queryType.fields(this.methodTargetMap.entrySet()
                .stream()
                .map(entry -> {
                    MutationMetaInfo mutationMetaInfo = new DefaultMutationMetaInfo(entry.getValue(), entry.getKey(), this);
                    return Optional.of(
                            newFieldDefinition()
                                    .name(mutationMetaInfo.getMutationFieldName())
                                    //TODO :这里 构建方法的 SchemaDocumentation,应该要移动到mutationMetaInfo方法中，封装起来。
                                    .description(getMethodSchemaDocumentation(entry.getKey()))
                    ).map(fieldDefinition -> fieldDefinition.type(mutationMetaInfo.getGraphQLOutputType()))
                            .get()
                            .dataFetcher(new MutationDataFetcher(entityManager, this, mutationMetaInfo))
                            .argument(mutationMetaInfo.getGraphQLArgumentList())
                            .build();
                }).filter(gfd -> gfd != null).collect(Collectors.toList()));
        return queryType.build();
    }


    GraphQLFieldDefinition getQueryFieldDefinition(ManagedType<?> entityType) {
        return Optional.of(newFieldDefinition()
                .name(entityType.getJavaType().getSimpleName())
                .description(getSchemaDocumentation(entityType.getJavaType())))
                .map(fieldDefinition -> {
                    fieldDefinition.type(
                            getGraphQLOutputTypeAndCreateIfNecessary(entityType))
                            .dataFetcher(new JpaDataFetcher(entityManager, (entityType instanceof EntityType) ? (EntityType) entityType : null, this));
                    return fieldDefinition;
                }).get()
                .argument(entityType.getAttributes().stream()
                        //只有id和number才能被当作单个实体对象的查询输入参数
                        .filter(attr -> new HashSet<String>(Arrays.asList(QUERY_OUTPUT_FILTER_PROPS)).contains(attr.getName()))
                        .map(attr -> GraphQLArgument.newArgument()
                                .name(attr.getName())
                                .type(GraphQLNonNull.nonNull(Scalars.GraphQLString))
                                .build()).collect(Collectors.toList()))
                .build();
    }

    //查询实体信息时可分页 TODO 应该添加过滤条件信息
    private GraphQLFieldDefinition getQueryFieldPageableDefinition(EntityType<?> entityType) {
        GraphQLObjectType pageType = newObject()
                .name(getGraphQLTypeNameOfEntityList(entityType))
                .description(getGraphQLTypeNameOfEntityList(entityType) + " 负责包装一组" + entityType.getName() + "数据")
                .field(newFieldDefinition().name("totalPages").description("根据paginator.size和数据库记录数得出的总页数").type(Scalars.GraphQLLong).build())
                .field(newFieldDefinition().name("totalElements").description("总的记录数").type(Scalars.GraphQLLong).build())
                .field(newFieldDefinition().name("content").description("实际返回的内容列表").type(new GraphQLList(this.getGraphQLOutputTypeAndCreateIfNecessary(entityType))).build())
                .build();

        return newFieldDefinition()
                .name(getGraphQLTypeNameOfEntityList(entityType))
                .description(getGraphQLTypeNameOfEntityList(entityType) + " 负责包装一组" + entityType.getName() + "数据")
                .type(pageType)
                //采用的是ExtendedJpaDataFetcher来处理
                .dataFetcher(new CollectionJpaDataFetcher(entityManager, entityType, this))
                .argument(GraphQLArgument.newArgument()
                        .name(PAGINATION_REQUEST_PARAM_NAME)
                        .type(this.dtoClassGraphQlInputTypeMap.get(Paginator.class))
                )
                .argument(GraphQLArgument.newArgument()
                        .name(QFILTER_REQUEST_PARAM_NAME)
                        .type(this.dtoClassGraphQlInputTypeMap.get(QueryFilter.class)))
                .build();
    }


    /**
     * 依次从map队列中寻找符合条件的记录对应的GraphQLInputType，如果找到则返回，如果没有找到，最终抛出异常。类似于四个if语句
     */
    @Override
    public GraphQLInputType getGraphQLInputType(Type typeClazz) {
        return Optional.ofNullable((GraphQLInputType) this.classGraphQlScalarTypeMap.get(typeClazz)).orElseGet(() ->
                Optional.ofNullable((GraphQLInputType) this.enumClassGraphQlEnumTypeMap.get(typeClazz)).orElseGet(() ->
                        Optional.ofNullable(this.dtoClassGraphQlInputTypeMap.get(typeClazz)).orElseGet(() ->
                                this.graphQlTypeManagedTypeClassMap.entrySet().stream()
                                        .filter(entry -> entry.getValue().getJavaType().equals(typeClazz) && entry.getKey() instanceof GraphQLInputType)
                                        .map(entry -> (GraphQLInputType) entry.getKey()).findFirst()
                                        .orElseThrow(null))));
    }

    @Override
    public Class getClazzByInputType(GraphQLType graphQLType) {
        return Optional.ofNullable(this.graphQlTypeManagedTypeClassMap.get(graphQLType)).map(entityType -> entityType.getJavaType())
                .orElseGet(() -> this.enumClassGraphQlEnumTypeMap.entrySet().stream().filter(entry -> graphQLType.equals(entry.getValue()))
                        .map(Map.Entry::getKey).findAny()
                        .orElseGet(() -> this.dtoClassGraphQlInputTypeMap.entrySet()
                                .stream()
                                .filter(entry -> graphQLType.equals(entry.getValue()))
                                .map(entry -> entry.getKey())
                                .findAny()
                                .orElseGet(() -> this.classGraphQlScalarTypeMap.entrySet()
                                        .stream()
                                        .filter(entry -> graphQLType.equals(entry.getValue()))
                                        .map(entry -> entry.getKey()).findAny()
                                        .orElse(null))));
    }

    @Override
    public String getGraphQLTypeNameOfEntityList(EntityType entityType) {
        return entityType.getName() + ENTITY_LIST_NAME;
    }

    @Override
    public BosEnum getBosEnumByValue(GraphQLEnumType bosEnumType, String enumValue) {
        Class<? extends BosEnum> enumType = this.getClazzByInputType(bosEnumType);

        //todo : 暂时这么写，到时候异常处理
        return Arrays.stream(enumType.getEnumConstants())
                .filter(bosEnum -> EntityEnumUtil.getValueByEnum(bosEnum).equals(enumValue))
                .findAny()
                .orElse(null);
    }


    @Override
    public EntityType getEntityType(Class type) {
        return entityManager.getMetamodel().getEntities().stream()
                .filter(et -> et.getJavaType().equals(type)).findFirst().orElse(null);
    }

    private GraphQLOutputType getGraphQLOutputTypeAndCreateIfNecessary(ManagedType entityType) {
        return Optional.ofNullable(getGraphQLOutputType(entityType.getJavaType())).orElseGet(() -> {
            GraphQLObjectType graphQLObjectType = newObject().name(entityType.getJavaType().getSimpleName())
                    .fields((List<GraphQLFieldDefinition>) entityType.getAttributes().stream()
                            .filter(attr -> this.isNotIgnored((Attribute) attr))
                            .map(attribute -> {
                                GraphQLOutputType outputType = (GraphQLOutputType) this.getAttributeGrahQLType((Attribute) attribute, false);
                                GraphQLFieldDefinition.Builder builder = newFieldDefinition()
                                        .description(getEntitySchemaDocumentation(((Attribute) attribute).getJavaMember()))
                                        .name(((Attribute) attribute).getName())
                                        .type(outputType);
                                if (outputType instanceof GraphQLScalarType) {
                                    builder.argument(GraphQLArgument.newArgument()
                                            .name(OrderByDirection.ORDER_BY)
                                            .type(ORDER_BY_DIRECTION_ENUM)
                                    );
                                }
                                return builder.build();
                            })
                            .collect(Collectors.toList()))
                    .build();
            this.graphQlTypeManagedTypeClassMap.put(graphQLObjectType, entityType);
            return graphQLObjectType;
        });
    }

    @Override
    public GraphQLOutputType getGraphQLOutputType(Type type) {
        return Optional.ofNullable((GraphQLOutputType) this.classGraphQlScalarTypeMap.get(type)).orElseGet(() ->
                Optional.ofNullable((GraphQLOutputType) this.enumClassGraphQlEnumTypeMap.get(type)).orElseGet(() ->
                        this.graphQlTypeManagedTypeClassMap.entrySet().stream()
                                .filter(entry -> entry.getValue().getJavaType().equals(type) && entry.getKey() instanceof GraphQLOutputType)
                                .map(entry -> (GraphQLOutputType) entry.getKey()).findAny().orElse(null)));
    }

    /**
     * 根据atrribute来查找对应的GraphQLType，如果是InputType属性，则结果可以被强制转换为GraphQLInputType
     */
    private GraphQLType getAttributeGrahQLType(Attribute attribute, boolean needInputType) {
        if (attribute.getPersistentAttributeType() == Attribute.PersistentAttributeType.BASIC) {
            GraphQLInputType graphQLInputType = getBasicAttributeTypeAndAddBosEnumIfNecessary(attribute.getJavaType());
            if (graphQLInputType != null) {
                return graphQLInputType;
            }
        } else if (attribute.getPersistentAttributeType() == Attribute.PersistentAttributeType.ONE_TO_MANY) {
            EntityType foreignType = (EntityType) ((PluralAttribute) attribute).getElementType();
            return new GraphQLList(new GraphQLTypeReference(foreignType.getName() + (needInputType ? MUTATION_INPUTTYPE_POSTFIX : "")));
        } else if (attribute.getPersistentAttributeType() == Attribute.PersistentAttributeType.MANY_TO_ONE) {
            EntityType foreignType = (EntityType) ((SingularAttribute) attribute).getType();
            return new GraphQLTypeReference(foreignType.getName() + (needInputType ? MUTATION_INPUTTYPE_POSTFIX : ""));
        } else if (attribute.getPersistentAttributeType() == Attribute.PersistentAttributeType.EMBEDDED) {
            EmbeddableType embeddableType = (EmbeddableType) ((SingularAttribute) attribute).getType();
            return new GraphQLTypeReference(embeddableType.getJavaType().getSimpleName() + (needInputType ? MUTATION_INPUTTYPE_POSTFIX : ""));
        }
        throw new UnsupportedOperationException(
                "Attribute could not be mapped to GraphQL: field '" + attribute.getJavaMember().getName() + "' of entity class '" + attribute.getDeclaringType().getJavaType().getName() + "'");
    }

    /**
     * 找到基础类型的类型，包括枚举enum ,如果没找到，则回返回null
     */
    private GraphQLInputType getBasicAttributeTypeAndAddBosEnumIfNecessary(Class javaType) {
        return Optional.ofNullable((GraphQLInputType) this.classGraphQlScalarTypeMap.get(javaType))
                .orElseGet(() -> Optional.ofNullable(this.enumClassGraphQlEnumTypeMap.get(javaType))
                        .orElseGet(() -> {
                            if (javaType.isEnum() && (BosEnum.class.isAssignableFrom(javaType))) {
                                GraphQLEnumType gt = getGraphQLEnumType(javaType);
                                this.enumClassGraphQlEnumTypeMap.put((Class<BosEnum>) javaType, gt);
                                return gt;
                            } else {
                                return null;
                            }
                        }));
    }


    /**
     * 获得 Entity 中的 SchemaDocumentation
     */
    private String getEntitySchemaDocumentation(Member member) {
        return (member instanceof AnnotatedElement) ? getSchemaDocumentation((AnnotatedElement) member) : null;
    }

    /**
     * 获得属性上的 SchemaDocumentation
     */
    private String getFieldDocumentation(Member member) {
        if (member instanceof Field) {
            return SchemaParseUtil.fieldSchema((Field) member);
        }
        // 删除末尾的分割符标致
        return "";
    }

    /**
     * method 的SchemaDocumentation 的生成
     */
    private String getMethodSchemaDocumentation(Method method) {
        String value = "";
        if (method.isAnnotationPresent(SchemaDocumentation.class)) {
            SchemaDocumentation schemaDocumentation = method.getAnnotation(SchemaDocumentation.class);
            value += schemaDocumentation.value();
        }
        if (method.getDeclaringClass().isAnnotationPresent(MutationValidated.class)) {
            if (method.isAnnotationPresent(ValidSelect.class)) {
                ValidSelect validSelect = method.getAnnotation(ValidSelect.class);
                value += SchemaParseUtil.SEPARATOR + SchemaParseUtil.VALID_SELECT_PREFIX + validSelect.value();
            } else {
                value += SchemaParseUtil.SEPARATOR + SchemaParseUtil.VALID_SELECT_ALL;
            }
        }
        return value;
    }


    private static String getSchemaDocumentation(AnnotatedElement annotatedElement) {
        if (annotatedElement != null) {
            SchemaDocumentation schemaDocumentation = annotatedElement.getAnnotation(SchemaDocumentation.class);
            return schemaDocumentation != null ? schemaDocumentation.value() : null;
        }
        return null;
    }

    private boolean isNotIgnoredForEntityInput(Attribute attribute) {
        return !ENTITYPROP_SET_SHOULDBEIGNORED_IN_MUTATION_ARGUMENT.contains(attribute.getName()) && isNotIgnored(attribute);
    }

    private boolean isNotIgnored(Attribute attribute) {
        boolean isEntryParent = ICoreObject.class.equals(attribute.getJavaType()) && ENTRY_PARENT_PROPNAME.equals(attribute.getName());
        return !isEntryParent && (isNotIgnored(attribute.getJavaMember()) && isNotIgnored(attribute.getJavaType()));
    }

    private boolean isNotIgnored(ManagedType entityType) {
        //TODO
        // return ICoreObject.class.isAssignableFrom(entityType.getJavaType()) &&
        return isNotIgnored(entityType.getJavaType());
    }

    private boolean isBosEntity(ManagedType entityType) {
        return BosEntity.class.isAssignableFrom(entityType.getJavaType());
    }

    private boolean isNotIgnored(Member member) {
        return member instanceof AnnotatedElement && isNotIgnored((AnnotatedElement) member);
    }

    private boolean isNotIgnored(AnnotatedElement annotatedElement) {
        if (annotatedElement != null) {
            GraphQLIgnore schemaDocumentation = annotatedElement.getAnnotation(GraphQLIgnore.class);
            return schemaDocumentation == null;
        }
        return false;
    }


    private static final GraphQLEnumType FIELD_NULL_ENUM = getGraphQLEnumType(FieldNullEnum.class);
    private static final GraphQLEnumType ORDER_BY_DIRECTION_ENUM = getGraphQLEnumType(OrderByDirection.class);
    private static final GraphQLEnumType QUERY_FILTER_OPERATOR_ENUM = getGraphQLEnumType(QueryFilterOperator.class);
    private static final GraphQLEnumType QUERY_FILTER_COMBINATOR_ENUM = getGraphQLEnumType(QueryFilterCombinator.class);


    private static GraphQLEnumType getGraphQLEnumType(Class<? extends BosEnum> bosEnumClass) {
        return new GraphQLEnumType(bosEnumClass.getSimpleName()
                , getSchemaDocumentation(bosEnumClass),
                EntityEnumUtil.entityEnumMap(bosEnumClass).entrySet().stream().map(entry -> {
                    String value = entry.getKey();
                    String alias = entry.getValue();
                    return new GraphQLEnumValueDefinition(value, alias, value);
                }).collect(Collectors.toList()));
    }


}


