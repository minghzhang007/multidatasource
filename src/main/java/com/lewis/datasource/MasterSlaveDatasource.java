package com.lewis.datasource;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author zhangminghua
 */
@Data
@NoArgsConstructor
public class MasterSlaveDatasource {
    private String key;

    private Object masterDatasource;

    private List<Object> slaveDatasourceList;

    private boolean isDefault;
}
