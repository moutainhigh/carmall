package com.qunchuang.carmall.graphql.query;

import cn.wzvtcsoft.bosdomain.enums.BosEnum;
import cn.wzvtcsoft.bosdomain.util.EntityEnumUtil;
import com.qunchuang.carmall.graphql.IGraphQlTypeMapper;
import com.qunchuang.carmall.graphql.query.dataprivilege.PrivilegeConstraint;
import com.qunchuang.carmall.graphql.query.dataprivilege.PrivilegeConstraintUtil;
import com.qunchuang.carmall.graphql.query.enums.OrderByDirection;
import com.qunchuang.carmall.graphql.query.enums.QueryFilterCombinator;
import com.qunchuang.carmall.graphql.query.enums.QueryFilterOperator;
import com.qunchuang.carmall.graphql.query.enums.QueryForWhatEnum;
import graphql.language.*;
import graphql.schema.*;
import org.hibernate.query.criteria.internal.path.SingularAttributePath;
import org.springframework.beans.ConfigurablePropertyAccessor;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
import javax.persistence.Subgraph;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.PluralAttribute;
import javax.persistence.metamodel.SingularAttribute;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class JpaDataFetcher implements DataFetcher {


    protected EntityManager entityManager;
    protected EntityType<?> entityType;
    protected IGraphQlTypeMapper graphQlTypeMapper;


    public JpaDataFetcher(EntityManager entityManager, EntityType<?> entityType, IGraphQlTypeMapper graphQlTypeMapper) {
        this.entityManager = entityManager;
        this.entityType = entityType;
        this.graphQlTypeMapper = graphQlTypeMapper;
    }


    @Override
    public final Object get(DataFetchingEnvironment environment) {
        QueryFilter queryFilter = extractQueryFilter(environment, environment.getFields().iterator().next());

        //检查权限  构造数据过滤   查询时才需要
        if (this.getClass().isAssignableFrom(JpaDataFetcher.class) || this.getClass().isAssignableFrom(CollectionJpaDataFetcher.class)) {
            queryFilter = checkPermission(queryFilter);
        }

        Object result = this.getResult(environment, queryFilter);
        return result;
    }

    /**
     * 获取 entity 中的 QueryFilter 的约束
     */
    private QueryFilter checkPermission(QueryFilter queryFilter) {

        Class<?> clz = this.entityType.getJavaType();

        for (Method method : clz.getDeclaredMethods()) {
            if (method.isAnnotationPresent(PrivilegeConstraint.class)) {


                QueryFilter result = null;
                try {
                    result = (QueryFilter) method.invoke(clz.newInstance());
                    queryFilter = PrivilegeConstraintUtil.merge(queryFilter, result);
                } catch (Exception e) {
                    //构建约束失败 抛出异常  
                    // TODO: 2019/2/19 后续需要寻找 抛出合适异常 直接修改http response stauts
                    if (SecurityContextHolder.getContext().getAuthentication() == null || (SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof String)){
                        //未登录 （匿名用户）
                        throw new BadCredentialsException("");
                    }else {
                        throw new AccessDeniedException("");
                    }
                }

            }
        }

        return queryFilter;
    }


    protected QueryFilter extractQueryFilter(DataFetchingEnvironment environment, Field field) {
        return null;
    }

    public Object getResult(DataFetchingEnvironment environment, QueryFilter filter) {
        TypedQuery typedQuery = getQuery(environment, environment.getFields().iterator().next(), filter, QueryForWhatEnum.NORMAL, null);
        return typedQuery.getResultList().stream().findAny().orElse(null);
    }

    /**
     * 遍历选中的字段集合，把entityGraph准备好
     */
    private void travelFieldSelection(CriteriaBuilder cb, Path root, SelectionSet selectionSet, List<Argument> arguments, List<Order> orders, EntityGraph entityGraph, Subgraph subgraph, QueryForWhatEnum queryForWhat) {
        if (selectionSet != null) {
            selectionSet.getSelections().forEach(selection -> {
                if (selection instanceof Field) {
                    Field selectedField = (Field) selection;
                    String selectedFieldName = selectedField.getName();
                    // "__typename" is part of the graphql introspection spec and has to be ignored by jpa
                    if (!"__typename".equals(selectedFieldName) && !"parent".equals(selectedFieldName)) {
                        Path fieldPath = root.get(selectedField.getName());

                        // Process the orderBy clause
                        // TODO 排序如果出现在第二层会有一些问题，似乎没法影响到，似乎在指明one2many下分录的排序规则时，会碰到问题，可能跟entry以set形式出现有关系。many2one应该不会。
                        if (QueryForWhatEnum.JUSTFORCOUNTBYDISTINCTID != queryForWhat) {//如果是只用来查询符合条件的总数，则不用添加roderby
                            Optional<Argument> orderByArgument = selectedField.getArguments().stream().filter(it -> OrderByDirection.ORDER_BY.equals(it.getName())).findFirst();
                            if (orderByArgument.isPresent()) {
                                if (OrderByDirection.DESC.name().equals(((EnumValue) orderByArgument.get().getValue()).getName())) {
                                    orders.add(cb.desc(fieldPath));
                                } else {
                                    orders.add(cb.asc(fieldPath));
                                }
                            }
                        }

                        //如果这个root是embeddable的话，则直接返回了。
                        if (root instanceof SingularAttributePath && ((SingularAttributePath) root).getAttribute().getPersistentAttributeType() == Attribute.PersistentAttributeType.EMBEDDED) {
                            return;
                        }


                        //TODO 可以考虑去掉的过滤功能，只保留(id:'xxxx')和（number:'yyyy')这两种，因为我们现在是用QFilter了，除非我们重新构建一个比QFilter更好的查询方法。
                        arguments.addAll(selectedField.getArguments().stream()
                                .filter(it -> !OrderByDirection.ORDER_BY.equals(it.getName()))
                                .filter(it -> "id".equals(it.getName()) || "number".equals(it.getName()))
                                .map(it -> new Argument(selectedFieldName + "." + it.getName(), it.getValue()))
                                .collect(Collectors.toList()));

                        Path root2 = joinIfNecessary((From) root, selectedFieldName);
                        Subgraph subgraph2 = null;
                        if (root2 == null) {//单纯的属性
                            return;
                        } else if (root2 == root) {//embeddable属性
                            root2 = root2.get(selectedFieldName);
                        } else if (root2 != root && (QueryForWhatEnum.NORMAL == queryForWhat)) {//join时的属性
                            if (entityGraph != null) {
                                subgraph2 = entityGraph.addSubgraph(selectedField.getName());
                            } else {
                                subgraph2 = subgraph.addSubgraph(selectedField.getName());
                            }
                        }

                        //Attribute selectedFieldAttribute = JpaDataFetcher.this.entityManager.getMetamodel().entity(root.getJavaType()).getAttribute(selectedFieldName);
                        //如果还有下一层,且不为embedable属性，则需要递归。
                        if (
                            //(selectedFieldAttribute.getPersistentAttributeType() != Attribute.PersistentAttributeType.EMBEDDED) &&
                                ((Field) selection).getSelectionSet() != null) {
                            travelFieldSelection(cb, root2, ((Field) selection).getSelectionSet(), arguments, orders, null, subgraph2, queryForWhat);
                        }
                    }
                }
            });
        }
    }

    /**
     * @param field        -选择内容
     * @param queryFilter  - 过滤条件
     * @param queryforWhat - 是否仅仅为了查询符合条件的对象个数。如果有collection的join，则抛弃掉不管。
     * @param paginator    -当   justforselectcount为false时，这个paginator可以起分页的作用，同样，分页对collection的join无效
     */
    protected TypedQuery getQuery(DataFetchingEnvironment environment, Field field, QueryFilter queryFilter, QueryForWhatEnum queryforWhat, Paginator paginator) {


        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery query = (QueryForWhatEnum.JUSTFORCOUNTBYDISTINCTID == queryforWhat) ? cb.createQuery(Long.class) : (QueryForWhatEnum.JUSTFORIDSINTHEPAGE == queryforWhat) ? cb.createQuery() : cb.createQuery((Class) entityType.getJavaType());
        Root root = query.from(entityType);

        SelectionSet selectionSet = field.getSelectionSet();
        List<Argument> arguments = new ArrayList<>();
        List<Order> orders = new ArrayList<>();
        EntityGraph graph = entityManager.createEntityGraph(entityType.getJavaType());
        List<Predicate> predicates = new ArrayList<>();

        // Loop through all of the fields being requested
        //迭代的形式以便组成一条语句
        travelFieldSelection(cb, root, selectionSet, arguments, orders, graph, null, queryforWhat);

        Predicate predicatebyfilter = getPredicate(cb, root, environment, queryFilter);
        if (predicatebyfilter != null) {
            predicates.add(predicatebyfilter);
        }

        //最终将所有的非orderby形式的argument转化成predicate，并转成where子句.
        arguments.addAll(field.getArguments());
        final Root roottemp = root;
        predicates.addAll(arguments.stream()
                .filter(it -> "id".equals(it.getName()) || "number".equals(it.getName()))
                .map(it -> getPredicate(cb, roottemp, environment, it)).collect(Collectors.toList()));

        if (QueryForWhatEnum.JUSTFORCOUNTBYDISTINCTID == queryforWhat) {
            SingularAttribute idAttribute = entityType.getId(Object.class);
            query.select(cb.countDistinct(root.get(idAttribute.getName())));
        } else if (QueryForWhatEnum.JUSTFORIDSINTHEPAGE == queryforWhat) {
            query.select(root.get("id").alias("id"));
        }
        query.where(predicates.toArray(new Predicate[predicates.size()]));
        if (QueryForWhatEnum.JUSTFORCOUNTBYDISTINCTID != queryforWhat) {
            query.orderBy(orders);
        }
        TypedQuery result = entityManager.createQuery(query.distinct(true));
        if (QueryForWhatEnum.NORMAL == queryforWhat) {
            if (paginator != null) {
                result.setMaxResults(paginator.getSize());
                result.setFirstResult((paginator.getPage() - 1) * paginator.getSize());
            }
        }
        //将EntityGraph加入
        return result.setHint("javax.persistence.fetchgraph", graph);
    }


    private Predicate getPredicate(CriteriaBuilder cb, Root root, DataFetchingEnvironment environment, QueryFilter queryFilter) {
        Predicate result = null;
        if (queryFilter == null) {
            return result;
        }

        String k = queryFilter.getKey();
        List<String> parts = Arrays.asList(k.split("\\."));
        Path path = root;
        boolean isEmbeddable = false;
        for (String part : parts) {
            if (isEmbeddable) {
                path = path.get(part);
            } else {
                From temp = joinIfNecessary((From) path, part);
                //如果没变动，说明到顶了，该属性为简单类型，拿到路径。
                if (temp == null) {
                    path = path.get(part);
                } else if (temp == path) {//表明是embeddable属性
                    isEmbeddable = true;
                    path = path.get(part);
                } else {
                    path = temp;
                }
            }
            //如果(From)path不能转换，则说明queryfilter的k写错了。因为如果含有.那必须是形如roleItems.role.id这样的除最后一段外均为关系(可以作为From)的path

        }

        String v = queryFilter.getValue();
        QueryFilterOperator qfo = queryFilter.getOperator();
        QueryFilterCombinator qfc = queryFilter.getCombinator();
        Object value;

        //TODO 需要进一步扩展，考虑添加更多QFilter操作符
        switch (qfo) {
            case LIKE:
                value = convertFilterValue(path.getJavaType(), v);
                result = cb.like(path, (String) value);
                break;
            case IN:
                List<Object> inList = Stream.of(v.split(",")).map(s -> (s.startsWith("'") && s.endsWith("'") ? s.substring(1, s.length() - 1) : s)).collect(Collectors.toList());
                for (int i = 0; i < inList.size(); i++) {
                    inList.set(i, convertFilterValue(path.getJavaType(), (String) inList.get(i)));
                }
                result = path.in(inList);
                break;
            case ISNULL:
                result = cb.isNull(path);
                break;
            case EQUEAL:
                value = convertFilterValue(path.getJavaType(), v);
                result = cb.equal(path, value);
                ;
                break;
            case LESSTHAN:
                value = convertFilterValue(path.getJavaType(), v);
                result = cb.lessThan(path, String.valueOf(value));
                break;
            case GREATTHAN:
                value = convertFilterValue(path.getJavaType(), v);
                result = cb.greaterThan(path, String.valueOf(value));
                break;
            case NOTLESSTHAN:
                value = convertFilterValue(path.getJavaType(), v);
                result = cb.greaterThanOrEqualTo(path, String.valueOf(value));
                break;
            case NOTGREATTHAN:
                value = convertFilterValue(path.getJavaType(), v);
                result = cb.lessThanOrEqualTo(path, String.valueOf(value));
                break;
            default:
                break;
        }
        //操作符没有，则直接返回
        if (qfc == null) {
            return result;
        }
        Predicate next = getPredicate(cb, root, environment, queryFilter.getNext());
        //如果下一个predicate本身为空，则也直接返回
        if (next == null) {
            return result;
        }

        switch (queryFilter.getCombinator()) {
            case AND:
                return cb.and(result, next);
            case OR:
                return cb.or(result, next);
            default:
                break;
            // case NOT:
        }
        return result;

    }

    private Object convertFilterValue(Class javaType, String v) {

        return (javaType == boolean.class || javaType == Boolean.class) ? Boolean.valueOf(v) :
                (javaType == int.class || javaType == Integer.class) ? Integer.valueOf(v) :
                        (javaType == long.class || javaType == Long.class) ? Long.valueOf(v) :
                                (BosEnum.class.isAssignableFrom(javaType)) ?
                                        Arrays.stream((BosEnum[]) javaType.getEnumConstants()).filter(bosEnum -> EntityEnumUtil.getValueByEnum(bosEnum).equals(v)).findFirst()
                                                .orElse(null) : v;
    }

    /**
     * 判断是否需要进行join连接，如有必要则连接并返回。根据BOS模型，有三种可能：
     * 1。如若必要，则执行join。如进行one2many或者many2one,则需要进行join，并返回新的连接。
     * 2。返回自身（即currentjoin)则表示碰到的是一个embeddable属性，无需join。
     * 3。返回null则表示碰到的是一个简单属性，无需jion，为了与第2点区分开，则需要返回null.
     *
     * @param currentjoin   -当前join
     * @param attributeName - 当前join对应的实体类中的某属性名称
     */
    private From joinIfNecessary(From currentjoin, String attributeName) {
        //根据属性名拿到所在实体里对应的属性值，此处可能因为该属性不存在而报错，这说明queryfilter表达式写错了。
        Attribute selectedFieldAttribute = JpaDataFetcher.this.entityManager.getMetamodel().entity(currentjoin.getJavaType()).getAttribute(attributeName);
        //如果该属性是one2many或者many2one，则检查是否需要在当前from中添加join（如果之前存在就不添加，否则要添加）
        boolean jpaMap = (selectedFieldAttribute instanceof PluralAttribute &&
                selectedFieldAttribute.getPersistentAttributeType() == Attribute.PersistentAttributeType.ONE_TO_MANY)
                || (selectedFieldAttribute instanceof SingularAttribute
                && selectedFieldAttribute.getPersistentAttributeType() == Attribute.PersistentAttributeType.MANY_TO_ONE);
        if (jpaMap) {
            //如果不存在则添加,并将返回的from设置为新的join
            Optional<Join> optjoin = currentjoin.getJoins()
                    .stream()
                    .filter(join -> attributeName.equals(((Join) join).getAttribute().getName()))
                    .findAny();
            return optjoin.isPresent() ? optjoin.get() : currentjoin.join(attributeName, JoinType.LEFT);
        } else if (selectedFieldAttribute.getPersistentAttributeType() == Attribute.PersistentAttributeType.EMBEDDED) {//如果是embeddable类型，返回原有的。
            return currentjoin;
        } else {//如果为简单类型，则说明用不到新的join，返回null。
            return null;
        }
    }

    /**
     * @param argument -为某个字段的过滤参数生成断言
     */
    private Predicate getPredicate(CriteriaBuilder cb, Root root, DataFetchingEnvironment environment, Argument argument) {
        Path path = null;
        if (!argument.getName().contains(".")) {
            Attribute argumentEntityAttribute = getAttribute(environment, argument);
            //似乎只能采用但字段不带点号的，且不带关系的标量==式断言，因此这里的join意义不大，TODO 总觉得这个函数有点问题。
            // If the argument is a list, let's assume we need to join and do an 'in' clause
            if (argumentEntityAttribute instanceof PluralAttribute) {
                Join join = root.join(argument.getName());
                return join.in(convertValue(environment, argument, argument.getValue()));
            }

            path = root.get(argument.getName());
            return cb.equal(path, convertValue(environment, argument, argument.getValue()));
        } else {
            String[] parts = argument.getName().split("\\.");
            for (String part : parts) {
                if (path == null) {
                    path = root.get(part);
                } else {
                    path = path.get(part);
                }
            }
            return cb.equal(path, convertValue(environment, argument, argument.getValue()));
        }
    }


    protected Object convertValue(DataFetchingEnvironment environment, Argument argument, Value value) {
        if (value instanceof StringValue) {
            Object convertedValue = environment.getArgument(argument.getName());
            if (convertedValue != null) {
                // Return real parameter for instance UUID even if the Value is a StringValue
                return convertedValue;
            } else {
                // Return provided StringValue
                return ((StringValue) value).getValue();
            }
        } else if (value instanceof VariableReference) {
            return environment.getArguments().get(((VariableReference) value).getName());
        } else if (value instanceof ArrayValue) {
            return ((ArrayValue) value).getValues().stream().map((it) -> convertValue(environment, argument, it)).collect(Collectors.toList());
        } else if (value instanceof EnumValue) {
            Class enumType = getJavaType(environment, argument);
            return Enum.valueOf(enumType, ((EnumValue) value).getName());
        } else if (value instanceof IntValue) {
            return ((IntValue) value).getValue();
        } else if (value instanceof BooleanValue) {
            return ((BooleanValue) value).isValue();
        } else if (value instanceof FloatValue) {
            return ((FloatValue) value).getValue();
        }

        return value.toString();
    }

    private Class getJavaType(DataFetchingEnvironment environment, Argument argument) {
        Attribute argumentEntityAttribute = getAttribute(environment, argument);

        if (argumentEntityAttribute instanceof PluralAttribute) {
            return ((PluralAttribute) argumentEntityAttribute).getElementType().getJavaType();
        }

        return argumentEntityAttribute.getJavaType();
    }

    private Attribute getAttribute(DataFetchingEnvironment environment, Argument argument) {
        GraphQLObjectType objectType = getObjectType(environment, argument);
        EntityType entityType = getEntityType(objectType);
        return entityType.getAttribute(argument.getName());
    }


    private EntityType getEntityType(GraphQLObjectType objectType) {
        return entityManager.getMetamodel().getEntities().stream().filter(it -> it.getName().equals(objectType.getName())).findFirst().orElse(null);
    }

    private GraphQLObjectType getObjectType(DataFetchingEnvironment environment, Argument argument) {
        GraphQLType outputType = environment.getFieldType();
        if (outputType instanceof GraphQLList) {
            outputType = ((GraphQLList) outputType).getWrappedType();
        }

        if (outputType instanceof GraphQLObjectType) {
            return (GraphQLObjectType) outputType;
        }
        return null;
    }

    /**
     * @param dept 外部调用仅给 0
     */
    protected Object convertValue(DataFetchingEnvironment environment, final GraphQLInputType graphQLInputType, Object value, int dept) {
        if (graphQLInputType instanceof GraphQLNonNull) {
            return convertValue(environment, (GraphQLInputType) (((GraphQLNonNull) graphQLInputType).getWrappedType()), value, dept);
            //否则如果为空
        } else if (value == null) {
            return null;
        } else if (value instanceof VariableReference) {
            value = environment.getExecutionContext().getVariables().get(((VariableReference) value).getName());
            return convertValue(environment, graphQLInputType, value, dept);
            //many2one
        } else if (graphQLInputType instanceof GraphQLInputObjectType) {
            Map<String, Object> map = (value instanceof Map) ? (Map) value : new HashMap();
            //还有可能是ObjectValue，内嵌Argument类型（相对于外部variable）的就是这种类型的。
            if (value instanceof ObjectValue) {
                ((ObjectValue) value).getObjectFields().forEach(field -> map.put(field.getName(), field.getValue()));
            }
            map.keySet().forEach(key -> {
                GraphQLInputType giotype = ((GraphQLInputObjectType) graphQLInputType).getField(key.toString()).getType();
                map.put(key, convertValue(environment, giotype, map.get(key), dept + 1));
            });
            Object result;
            try {
                result = this.graphQlTypeMapper.getClazzByInputType(graphQLInputType).newInstance();
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("convertValue.error!");
            }
            ConfigurablePropertyAccessor wrapper = PropertyAccessorFactory.forDirectFieldAccess(result);
            wrapper.setPropertyValues(map);
            return result;
        } else if (graphQLInputType instanceof GraphQLList) {
            List list = new ArrayList();
            if (value instanceof ArrayValue) {
                GraphQLInputType gitype = ((GraphQLInputType) ((GraphQLList) graphQLInputType).getWrappedType());
                list = ((ArrayValue) value).getValues().stream().map((it) -> convertValue(environment, gitype, it, dept + 1)).collect(Collectors.toList());
            } else if (value instanceof Collection) {
                for (Object item : ((Collection) value)) {
                    GraphQLInputType gitype = ((GraphQLInputType) ((GraphQLList) graphQLInputType).getWrappedType());
                    list.add(convertValue(environment, gitype, item, dept + 1));
                }
            }
            //mutation 的顶级参数是 List,返回List 否则全部返回 set,因为 entry 是以set 形式存在
            if (dept != 0) {
                Set set = new HashSet(list.size());
                set.addAll(list);
                return set;
            } else {
                return list;
            }
        } else if (graphQLInputType instanceof GraphQLEnumType) {
            return (value instanceof BosEnum) ? value : this.graphQlTypeMapper.getBosEnumByValue((GraphQLEnumType) graphQLInputType, (value instanceof EnumValue) ? ((EnumValue) value).getName() : value.toString());
            //scalar
        } else if (graphQLInputType instanceof GraphQLScalarType) {
            if (!(value instanceof Value)) {
                value = (value instanceof Integer) ? new IntValue(BigInteger.valueOf(((Integer) value).longValue())) :
                        (value instanceof BigInteger) ? new IntValue((BigInteger) value) :
                                (value instanceof Boolean) ? (new BooleanValue((Boolean) value)) :
                                        value instanceof String ? new StringValue((String) value) :
                                                value instanceof Float ? new FloatValue(BigDecimal.valueOf((Float) value)) :
                                                        value instanceof Double ? new FloatValue(BigDecimal.valueOf((Double) value)) :
                                                                value instanceof BigDecimal ? new FloatValue((BigDecimal) value) :
                                                                        value instanceof Long ? new IntValue(BigInteger.valueOf((Long) value))
                                                                                : null;

            }
            if (value != null) {
                return ((GraphQLScalarType) graphQLInputType).getCoercing().parseLiteral(value);
            }
        }
        throw new RuntimeException("convertValue scalar mismatch?" + graphQLInputType.getName() + ":" + value.getClass().getCanonicalName());
    }

}
