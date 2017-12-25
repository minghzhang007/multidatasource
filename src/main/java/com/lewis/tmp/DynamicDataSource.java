package com.lewis.tmp;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import java.util.HashMap;
import java.util.Map;

public class DynamicDataSource extends AbstractRoutingDataSource {
    private Object writeDataSource; //写数据源

    private Object readDataSource; //读数据源

    private Object otherDataSource;//其他数据源

    private Object localDataSource;


    public DynamicDataSource(Object writeDataSource, Object readDataSource, Object otherDataSource, Object localDataSource) {
        this.writeDataSource = writeDataSource;
        this.readDataSource = readDataSource;
        this.otherDataSource = otherDataSource;
        this.localDataSource = localDataSource;
    }


    @Override
    public void afterPropertiesSet() {
        if (writeDataSource == null) {
            throw new IllegalArgumentException("Property 'writeDataSource' is required");
        }

        setDefaultTargetDataSource(writeDataSource);
        Map<Object, Object> targetDataSources = new HashMap<>();


        targetDataSources.put(DynamicDataSourceGlobal.SNAIL_WRITE.name(), writeDataSource);
        if (readDataSource != null) {
            targetDataSources.put(DynamicDataSourceGlobal.SNAIL_READ.name(), readDataSource);
        }
        if (otherDataSource != null) {
            targetDataSources.put(DynamicDataSourceGlobal.YUEDU_READ.name(), otherDataSource);
        }
        if(localDataSource != null){
            targetDataSources.put(DynamicDataSourceGlobal.LOCAL.name(),localDataSource);
        }
        setTargetDataSources(targetDataSources);

        super.afterPropertiesSet();
    }


    @Override
    protected Object determineCurrentLookupKey() {

        DynamicDataSourceGlobal dynamicDataSourceGlobal = DynamicDataSourceHolder.getDataSource();

        if (dynamicDataSourceGlobal == null
                || dynamicDataSourceGlobal == DynamicDataSourceGlobal.SNAIL_WRITE) {
            return DynamicDataSourceGlobal.SNAIL_WRITE.name();
        } else if (dynamicDataSourceGlobal == DynamicDataSourceGlobal.YUEDU_READ) {
            return DynamicDataSourceGlobal.YUEDU_READ.name();
        }else if(dynamicDataSourceGlobal == DynamicDataSourceGlobal.LOCAL){
            return DynamicDataSourceGlobal.LOCAL.name();
        }

        return DynamicDataSourceGlobal.SNAIL_READ.name();
    }

    public Object getWriteDataSource() {
        return writeDataSource;
    }

    public void setWriteDataSource(Object writeDataSource) {
        this.writeDataSource = writeDataSource;
    }

    public Object getReadDataSource() {
        return readDataSource;
    }

    public void setReadDataSource(Object readDataSource) {
        this.readDataSource = readDataSource;
    }

    public Object getOtherDataSource() {
        return otherDataSource;
    }

    public void setOtherDataSource(Object otherDataSource) {
        this.otherDataSource = otherDataSource;
    }
}

