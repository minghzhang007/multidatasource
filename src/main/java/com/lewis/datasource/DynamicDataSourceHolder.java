package com.lewis.datasource;

public class DynamicDataSourceHolder {
    private static final ThreadLocal<String> holder = new ThreadLocal<String>();

    private DynamicDataSourceHolder() {
        //
    }

    public static void putDataSourceKey(String dataSourceKey) {
        holder.set(dataSourceKey);
    }

    public static String getDataSourceKey() {
        return holder.get();
    }

    public static void clearDataSource() {
        holder.remove();
    }
}
