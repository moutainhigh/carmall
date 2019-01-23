package com.qunchuang.carmall.graphql;

import cn.wzvtcsoft.bosdomain.enums.BosEnum;
import graphql.schema.GraphQLEnumType;
import graphql.schema.GraphQLInputType;
import graphql.schema.GraphQLOutputType;
import graphql.schema.GraphQLType;
import org.springframework.lang.Nullable;

import javax.persistence.metamodel.EntityType;
import javax.validation.constraints.NotNull;
import java.lang.reflect.Type;

public interface IGraphQlTypeMapper {

    @Nullable
    Class getClazzByInputType(GraphQLType graphQLType);

    @Nullable
    EntityType getEntityType(@NotNull Class type);

    GraphQLOutputType getGraphQLOutputType(Type type);

    GraphQLInputType getGraphQLInputType(Type type);

    /**
     * 获取某个实体类对应list的输出类型
     */
    String getGraphQLTypeNameOfEntityList(EntityType entityType);

    BosEnum getBosEnumByValue(GraphQLEnumType bosEnumType, String enumValue);
}
