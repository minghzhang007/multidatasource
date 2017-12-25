package com.lewis.tmp;

import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.keygen.SelectKeyGenerator;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
/*import org.slf4j.Logger;
import org.slf4j.LoggerFactory;*/
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

@Intercepts({
        @Signature(type = Executor.class, method = "update", args = {
                MappedStatement.class, Object.class}),
        @Signature(type = Executor.class, method = "query", args = {
                MappedStatement.class, Object.class, RowBounds.class,
                ResultHandler.class})})
public class DynamicPlugin implements Interceptor {

    //protected static final Logger logger = LoggerFactory.getLogger(DynamicPlugin.class);

    private static final String REGEX = ".*insert\\u0020.*|.*delete\\u0020.*|.*update\\u0020.*";

    private static final Map<String, DynamicDataSourceGlobal> cacheMap = new ConcurrentHashMap<>();

    @Override
    public Object intercept(Invocation invocation) throws Throwable {

        boolean synchronizationActive = TransactionSynchronizationManager.isSynchronizationActive();
        if (!synchronizationActive) {
            Object[] objects = invocation.getArgs();
            MappedStatement ms = (MappedStatement) objects[0];
            DynamicDataSourceGlobal dynamicDataSourceGlobal = null;
            String id = ms.getId();
            if (id.startsWith("com.netease.snailreader.financebackend.biz.dao.yuedu")) {
                dynamicDataSourceGlobal = DynamicDataSourceGlobal.YUEDU_READ;
            } else if (id.startsWith("com.netease.snailreader.financebackend.biz.dao.local")) {
                dynamicDataSourceGlobal = DynamicDataSourceGlobal.LOCAL;
            } else {
                if ((dynamicDataSourceGlobal = cacheMap.get(ms.getId())) == null) {
                    //读方法
                    if (ms.getSqlCommandType().equals(SqlCommandType.SELECT)) {
                        //!selectKey 为自增id查询主键(SELECT LAST_INSERT_ID() )方法，使用主库
                        if (ms.getId().contains(SelectKeyGenerator.SELECT_KEY_SUFFIX)) {
                            dynamicDataSourceGlobal = DynamicDataSourceGlobal.SNAIL_WRITE;
                        } else {
                            BoundSql boundSql = ms.getSqlSource().getBoundSql(objects[1]);
                            String sql = boundSql.getSql().toLowerCase(Locale.CHINA).replaceAll("[\\t\\n\\r]", " ");
                            if (sql.matches(REGEX)) {
                                dynamicDataSourceGlobal = DynamicDataSourceGlobal.SNAIL_WRITE;
                            } else {
                                dynamicDataSourceGlobal = DynamicDataSourceGlobal.SNAIL_READ;
                            }
                        }
                    } else {
                        dynamicDataSourceGlobal = DynamicDataSourceGlobal.SNAIL_WRITE;
                    }
                }
            }
            //logger.info("数据库路由[{}] use [{}] Strategy, SqlCommandType [{}]..", ms.getId(), dynamicDataSourceGlobal.name(), ms.getSqlCommandType().name());
            cacheMap.put(ms.getId(), dynamicDataSourceGlobal);
            DynamicDataSourceHolder.putDataSource(dynamicDataSourceGlobal);
        }

        return invocation.proceed();
    }

    @Override
    public Object plugin(Object target) {
        if (target instanceof Executor) {
            return Plugin.wrap(target, this);
        } else {
            return target;
        }
    }

    @Override
    public void setProperties(Properties properties) {
        //
    }
}
