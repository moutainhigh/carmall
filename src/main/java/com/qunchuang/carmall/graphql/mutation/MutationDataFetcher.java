package com.qunchuang.carmall.graphql.mutation;

import cn.wzvtcsoft.bosdomain.persist.CoreObject;
import com.qunchuang.carmall.graphql.IGraphQlTypeMapper;
import graphql.language.Field;
import com.qunchuang.carmall.graphql.query.CollectionJpaDataFetcher;
import com.qunchuang.carmall.graphql.query.QueryFilter;
import com.qunchuang.carmall.graphql.query.enums.QueryFilterCombinator;
import com.qunchuang.carmall.graphql.query.enums.QueryFilterOperator;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLInputType;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MutationDataFetcher extends CollectionJpaDataFetcher {

    protected MutationMetaInfo mutationMetaInfo;

    public MutationDataFetcher(EntityManager entityManager, IGraphQlTypeMapper graphQlTypeMapper, MutationMetaInfo mutationMetaInfo) {
        super(entityManager, mutationMetaInfo.getEntityType(), graphQlTypeMapper);
        this.mutationMetaInfo = mutationMetaInfo;
    }

    @Override
    public Object getResult(DataFetchingEnvironment environment, QueryFilter queryFilter) {
        Field field = environment.getFields().iterator().next();
        Map<String, Object> nameArgMaps = new HashMap<>();
        field.getArguments().stream()
                //过滤掉不需要的输入参数。
                .filter(realArg -> this.mutationMetaInfo.getGraphQLArgument(realArg.getName()) != null)
                .forEach(realArg ->
                        nameArgMaps.put(realArg.getName(), convertValue(environment,
                                (GraphQLInputType) this.mutationMetaInfo.getGraphQLArgument(realArg.getName()).getType(),
                                realArg.getValue(), 0)));
        Object returnValue = this.mutationMetaInfo.invoke(nameArgMaps);
        //说明返回的是简单类型,或是包装简单类型后的集合类型。
        if (this.entityType == null) {
            return returnValue;
        } else if (returnValue == null) {
            return null;
            //说明返回类型要么是实体类型，要么是实体类型的集合类型
        } else {
            //说明返回的是拿到主键
            if (CoreObject.class.isAssignableFrom(returnValue.getClass())) {
                String id = ((CoreObject) returnValue).getId();
                queryFilter = new QueryFilter("id", QueryFilterOperator.EQUEAL, id, QueryFilterCombinator.AND, queryFilter);
                return super.getForEntity(environment, queryFilter);
                //实体类型的集合类型
            } else if (Collection.class.isAssignableFrom(returnValue.getClass())) {
                Collection entityCollection = ((Collection) returnValue);
                List<String> idList = (List<String>) entityCollection.stream().map(entity -> ((CoreObject) entity).getId()).collect(Collectors.toList());
                String idString = StringUtils.collectionToDelimitedString(idList, ",", "'", "'");
                //设置分页和过滤条件
                queryFilter = new QueryFilter("id", QueryFilterOperator.IN, idString, QueryFilterCombinator.AND, queryFilter);
                return super.getResult(environment, queryFilter);
            } else {
                throw new RuntimeException("返回不是 mutation 中不允许出现该类型");
            }
        }
    }


}
