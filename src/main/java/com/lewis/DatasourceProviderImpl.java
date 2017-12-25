package com.lewis;

import com.lewis.datasource.DatasourceProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/12/25.
 */
public class DatasourceProviderImpl implements DatasourceProvider {


    private Map<String, Object> datasourceMapping;

    public DatasourceProviderImpl(Map<String, Object> datasourceMapping) {
        this.datasourceMapping = datasourceMapping;
    }

    @Override
    public Object get(String key) {
        Object datasource = datasourceMapping.get(key);
        return datasource;
    }

    @Override
    public List<String> keys() {
        ArrayList<String> keys = new ArrayList<>(datasourceMapping.keySet());
        return keys;
    }
}
