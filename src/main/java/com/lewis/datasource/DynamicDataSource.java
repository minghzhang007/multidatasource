package com.lewis.datasource;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zhangminghua
 */
public class DynamicDataSource extends AbstractRoutingDataSource {

    @Resource
    private DatasourceProvider datasourceProvider;

    private List<MasterSlaveDatasource> masterSlaveDatasources;

    public DynamicDataSource(List<MasterSlaveDatasource> masterSlaveDatasources) {
        this.masterSlaveDatasources = masterSlaveDatasources;
    }

    @Override
    protected Object determineCurrentLookupKey() {
        return null;
    }

    @Override
    public void afterPropertiesSet() {
        if (CollectionUtils.isEmpty(masterSlaveDatasources)) {
            throw new IllegalArgumentException("Property 'writeDataSource' is required");
        }

        MasterSlaveDatasource masterSlaveDatasource = masterSlaveDatasources.stream().filter(item -> item.isDefault()).findFirst().orElse(null);
        if (masterSlaveDatasource != null) {
            setDefaultTargetDataSource(masterSlaveDatasource.getMasterDatasource());
        }

        Map<Object, Object> targetDataSources = new HashMap<>(64);

        for (MasterSlaveDatasource slaveDatasource : masterSlaveDatasources) {
            Object o = datasourceProvider.get(slaveDatasource.getKey());
            targetDataSources.put(slaveDatasource.getKey(), o);
        }

        setTargetDataSources(targetDataSources);

    }
}
