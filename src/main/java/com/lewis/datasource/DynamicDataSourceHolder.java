package com.lewis.datasource;

public class DynamicDataSourceHolder {
    private static final ThreadLocal<DatasourceKey> holder = new ThreadLocal<DatasourceKey>();

    private DynamicDataSourceHolder() {
        //
    }

    public static void putDataSource(DatasourceKey dataSource) {
        holder.set(dataSource);
    }

    public static DatasourceKey getDataSource() {
        return holder.get();
    }

    public static void clearDataSource() {
        holder.remove();
    }
}
