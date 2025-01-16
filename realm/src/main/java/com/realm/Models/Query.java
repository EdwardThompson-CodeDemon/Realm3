package com.realm.Models;

import java.util.LinkedHashMap;

public class Query {
    public String[] columns, tableFilters, order_filters, queryParameters;
    public String customQuery;
    public boolean order_asc;
    public int limit, offset;
    public LinkedHashMap<String, Boolean> orderFilters = new LinkedHashMap<>();

    public Query setCustomQuery(String customQuery) {
        this.customQuery = customQuery;
        return this;
    }

    public Query setColumns(String... columns) {
        this.columns = columns;
        return this;
    }

    public Query setTableFilters(String... tableFilters) {
        this.tableFilters = tableFilters;
        return this;
    }

    public Query setQueryParams(String... queryParameters) {
        this.queryParameters = queryParameters;
        return this;
    }

    /**
     * Setting order of the query result
     *
     * @return The new Query Instance
     * @deprecated This method is no longer used,it had an error where only the first column is considered and setting more than one column would result in an invalid query.
     * <p> Use {@link #addOrderFilters(String, boolean)} instead.
     */
    @Deprecated
    public Query setOrderFilters(boolean order_asc, String... order_columns) {
        this.orderFilters.put(order_columns[0], order_asc);
        this.order_filters = order_columns;
        this.order_asc = order_asc;
        return this;
    }

    /**
     * Adding order of the query result.
     * <p>
     * This method should be called in the order of the order statement relevance
     * </p>
     *
     * @param order_column the column to order by
     * @param order_asc    whether to order in ascending order
     * @return Query with the order added
     * This method is the best method for the query generator to generate the required query.
     */
    public Query addOrderFilters(String order_column, boolean order_asc) {
        this.orderFilters.put(order_column, order_asc);
        return this;
    }

    public Query setOrderFilters(LinkedHashMap<String, Boolean> orderFilters) {
        this.orderFilters = orderFilters;
        return this;
    }

    public Query setOrderAscending(boolean order_asc) {
        this.order_asc = order_asc;
        return this;
    }

    public Query setLimit(int limit) {
        this.limit = limit;
        return this;
    }

    public Query setOffset(int offset) {
        this.offset = offset;
        return this;
    }
}
