package com.lewis.datasource;

/**
 * @author zhangminghua
 */
public interface DatasourceProvider {

    Object get(String key);
}
