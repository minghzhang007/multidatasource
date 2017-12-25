package com.lewis.datasource;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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
        String dataSourceKey = DynamicDataSourceHolder.getDataSourceKey();
        for (MasterSlaveDatasource masterSlaveDatasource : masterSlaveDatasources) {
            if (isMasterKey(dataSourceKey, masterSlaveDatasource)) {
                return masterSlaveDatasource.getKey();
            } else if (isSlaveKey(dataSourceKey, masterSlaveDatasource)) {
                Object slaveDatasource = getSlaveDataSource(masterSlaveDatasource.getSlaveDatasourceList());
                return slaveDatasource;
            }
        }
        return getDefaultMaster();
    }

    //todo 负载均衡
    private Object getSlaveDataSource(List<Object> slaveDatasourceList) {

        return slaveDatasourceList.get(0);
    }

    private boolean isSlaveKey(String dataSourceKey, MasterSlaveDatasource masterSlaveDatasource) {
        return Objects.equals(dataSourceKey, masterSlaveDatasource.getKey());
    }

    private boolean isMasterKey(String dataSourceKey, MasterSlaveDatasource masterSlaveDatasource) {
        return isSlaveKey(dataSourceKey, masterSlaveDatasource);
    }

    @Override
    public void afterPropertiesSet() {
        if (CollectionUtils.isEmpty(masterSlaveDatasources)) {
            throw new IllegalArgumentException("Property 'writeDataSource' is required");
        }

        MasterSlaveDatasource masterSlaveDatasource = getDefaultMaster();
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

    private MasterSlaveDatasource getDefaultMaster() {
        return masterSlaveDatasources.stream().filter(item -> item.isDefault()).findFirst().orElse(null);
    }
}
