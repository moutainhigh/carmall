package com.qunchuang.carmall.graphql;

import java.util.Map;

public final class GraphQLInputQuery {

    public GraphQLInputQuery(String query, Map<String, Object> arguments) {
        this.query = query;
        this.arguments = arguments;
    }

    public GraphQLInputQuery() {
    }

    private String query;

    private Map<String, Object> arguments;

    Map<String, Object> getArguments() {
        return arguments;
    }

    String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public void setArguments(Map<String, Object> arguments) {
        this.arguments = arguments;
    }
}