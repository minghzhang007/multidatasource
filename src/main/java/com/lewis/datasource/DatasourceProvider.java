package com.lewis.datasource;

import java.util.List;

/**
 * @author zhangminghua
 */
public interface DatasourceProvider {

    Object get(String key);

    List<String> keys();
}
