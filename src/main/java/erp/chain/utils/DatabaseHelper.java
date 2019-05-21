package erp.chain.utils;

import erp.chain.common.Constants;
import erp.chain.mapper.UniversalMapper;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.converters.BigDecimalConverter;
import org.apache.commons.beanutils.converters.BigIntegerConverter;
import org.apache.commons.beanutils.converters.DateConverter;
import org.apache.commons.beanutils.converters.IntegerConverter;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class DatabaseHelper {
    private static UniversalMapper universalMapper;

    static {
        ConvertUtils.register(new IntegerConverter(null), Integer.class);
        ConvertUtils.register(new BigIntegerConverter(null), BigInteger.class);
        ConvertUtils.register(new BigDecimalConverter(null), BigDecimal.class);
        ConvertUtils.register(new DateConverter(null), Date.class);
    }

    public static UniversalMapper obtainUniversalMapper() {
        if (universalMapper == null) {
            universalMapper = ApplicationHandler.getBean(UniversalMapper.class);
        }
        return universalMapper;
    }

    public static long insert(Object domain) {
        return obtainUniversalMapper().insert(domain);
    }

    public static long insertAll(List<?> domains) {
        return obtainUniversalMapper().insertAll(domains);
    }

    public static long update(Object domain) {
        return obtainUniversalMapper().update(domain);
    }

    public static <T> T find(Class<T> domainClass, BigInteger id) {
        SearchModel searchModel = new SearchModel(true);
        searchModel.addSearchCondition("id", Constants.SQL_OPERATION_SYMBOL_EQUALS, id);
        return find(domainClass, searchModel);
    }

    public static <T> T find(Class<T> domainClass, SearchModel searchModel) {
        return find(domainClass, DatabaseUtils.obtainTableName(null, domainClass), searchModel);
    }

    public static <T> T find(Class<T> domainClass, String tableName, SearchModel searchModel) {
        try {
            searchModel.setTableName(tableName);
            List<String> columns = searchModel.getColumns();
            if (CollectionUtils.isEmpty(columns)) {
                searchModel.setColumns(DatabaseUtils.obtainAllAlias(domainClass));
            }
            Map<String, Object> map = obtainUniversalMapper().find(searchModel);
            T t = null;
            if (MapUtils.isNotEmpty(map)) {
                t = domainClass.newInstance();
                BeanUtils.populate(t, map);
            }
            return t;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> List<T> findAll(Class<T> domainClass, SearchModel searchModel) {
        return findAll(domainClass, DatabaseUtils.obtainTableName(null, domainClass), searchModel);
    }

    public static <T> List<T> findAll(Class<T> domainClass, String tableName, SearchModel searchModel) {
        try {
            searchModel.setTableName(tableName);
            List<String> columns = searchModel.getColumns();
            if (CollectionUtils.isEmpty(columns)) {
                searchModel.setColumns(DatabaseUtils.obtainAllAlias(domainClass));
            }
            List<Map<String, Object>> result = obtainUniversalMapper().findAll(searchModel);
            List<T> list = new ArrayList<T>();
            for (Map<String, Object> map : result) {
                T t = domainClass.newInstance();
                BeanUtils.populate(t, map);
                list.add(t);
            }
            return list;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> long count(Class<T> domainClass, SearchModel searchModel) {
        return count(DatabaseUtils.obtainTableName(null, domainClass), searchModel);
    }

    public static long count(String tableName, SearchModel searchModel) {
        searchModel.setTableName(tableName);
        return obtainUniversalMapper().count(searchModel);
    }

    public static <T> List<T> findAllPaged(Class<T> domainClass, SearchModel searchModel) {
        return findAllPaged(domainClass, DatabaseUtils.obtainTableName(null, domainClass), searchModel);
    }

    public static <T> List<T> findAllPaged(Class<T> domainClass, String tableName, SearchModel searchModel) {
        try {
            searchModel.setTableName(tableName);
            List<String> columns = searchModel.getColumns();
            if (CollectionUtils.isEmpty(columns)) {
                searchModel.setColumns(DatabaseUtils.obtainAllAlias(domainClass));
            }
            List<Map<String, Object>> result = obtainUniversalMapper().findAllPaged(searchModel);
            List<T> list = new ArrayList<T>();
            for (Map<String, Object> map : result) {
                T t = domainClass.newInstance();
                BeanUtils.populate(t, map);
                list.add(t);
            }
            return list;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static long universalUpdate(UpdateModel updateModel) {
        return obtainUniversalMapper().universalUpdate(updateModel);
    }
}
