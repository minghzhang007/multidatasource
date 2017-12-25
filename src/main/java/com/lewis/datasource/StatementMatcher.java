package com.lewis.datasource;

import org.apache.ibatis.executor.keygen.SelectKeyGenerator;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;

import java.util.Locale;

/**
 * Created by zhangminghua
 */
public abstract class StatementMatcher {

    private static final String REGEX = ".*insert\\u0020.*|.*delete\\u0020.*|.*update\\u0020.*";

    public String getDatasourceKey(MappedStatement mappedStatement, Object[] args) {
        String id = mappedStatement.getId();
        //逻辑key 比如阅读库 蜗牛库
        String logicKey = doGetDatasourceKey(id);
        SqlCommandType sqlCommandType = mappedStatement.getSqlCommandType();
        String masterSlaveKey = "";
        //查询
        if (sqlCommandType == SqlCommandType.SELECT) {
            //!selectKey 为自增id查询主键(SELECT LAST_INSERT_ID() )方法，使用主库
            if (id.contains(SelectKeyGenerator.SELECT_KEY_SUFFIX)) {
                //todo masterSlaveKey = 主库

            } else {
                BoundSql boundSql = mappedStatement.getSqlSource().getBoundSql(args[1]);
                String sql = boundSql.getSql().toLowerCase(Locale.CHINA).replaceAll("[\\t\\n\\r]", " ");
                if (sql.matches(REGEX)) {
                    //主库
                    //todo masterSlaveKey = 主库
                } else {
                    //从库
                    //todo masterSlaveKey = 从库
                }
            }
        }
        String datasourceKey = createDataSource(logicKey, masterSlaveKey);
        return datasourceKey;
    }

    private String createDataSource(String logicKey, String masterSlaveKey) {

        return logicKey + "_" + masterSlaveKey;
    }

    protected abstract String doGetDatasourceKey(String id);
}
