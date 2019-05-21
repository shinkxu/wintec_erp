package erp.chain.utils;

import erp.chain.annotations.Column;
import erp.chain.annotations.Table;
import erp.chain.annotations.Transient;
import org.apache.commons.lang.StringUtils;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by liuyandong on 2018-01-23.
 */
public class DatabaseUtils {
    public static String generateInsertSql(String domainClassName) {
        return generateInsertSql(domainClassName, null);
    }

    public static String generateInsertSql(String domainClassName, String tableName) {
        Class<?> domainClass = null;
        try {
            domainClass = Class.forName(domainClassName);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return generateInsertSql(domainClass, tableName);
    }

    public static String generateInsertSql(Class<?> domainClass) {
        return generateInsertSql(domainClass, null);
    }

    public static String generateInsertSql(Class<?> domainClass, String tableName) {
        StringBuilder insertSql = new StringBuilder("INSERT INTO ");
        insertSql.append(obtainTableName(tableName, domainClass));
        insertSql.append("(");
        StringBuilder valuesSql = new StringBuilder(" VALUES (");
        while (domainClass != Object.class) {
            Field[] fields = domainClass.getDeclaredFields();
            for (Field field : fields) {
                int modifiers = field.getModifiers();
                if (Modifier.isStatic(modifiers) || Modifier.isFinal(modifiers) || Modifier.isNative(modifiers) || field.getAnnotation(Transient.class) != null) {
                    continue;
                }
                String fieldName = field.getName();
                if ("id".equals(fieldName)) {
                    continue;
                }

                String columnName = null;
                Column column = field.getAnnotation(Column.class);
                if (column != null) {
                    columnName = column.name();
                } else {
                    columnName = NamingStrategyUtils.camelCaseToUnderscore(fieldName);
                }
                insertSql.append(columnName);
                insertSql.append(", ");
                valuesSql.append("#{").append(fieldName);
                valuesSql.append("}, ");
            }
            domainClass = domainClass.getSuperclass();
        }
        insertSql.deleteCharAt(insertSql.length() - 1);
        insertSql.deleteCharAt(insertSql.length() - 1);
        insertSql.append(")");
        valuesSql.deleteCharAt(valuesSql.length() - 1);
        valuesSql.deleteCharAt(valuesSql.length() - 1);
        valuesSql.append(")");
        insertSql.append(valuesSql);
        return insertSql.toString();
    }

    public static String[] generateInsertAllSql(String className) {
        return generateInsertAllSql(className, null);
    }

    public static String[] generateInsertAllSql(String className, String tableName) {
        Class<?> domainClass = null;
        try {
            domainClass = Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return generateInsertAllSql(domainClass, tableName);
    }

    public static String[] generateInsertAllSql(List<?> domains) {
        return generateInsertAllSql(domains.get(0).getClass(), null);
    }

    public static String[] generateInsertAllSql(List<?> domains, String tableName) {
        return generateInsertAllSql(domains.get(0).getClass(), tableName);
    }

    public static String[] generateInsertAllSql(Class<?> domainClass) {
        return generateInsertAllSql(domainClass, null);
    }

    public static String[] generateInsertAllSql(Class<?> domainClass, String tableName) {
        StringBuilder insertSql = new StringBuilder("INSERT INTO ");
        insertSql.append(obtainTableName(tableName, domainClass));
        insertSql.append("(");
        StringBuilder valuesSql = new StringBuilder("(");
        while (domainClass != Object.class) {
            Field[] fields = domainClass.getDeclaredFields();
            for (Field field : fields) {
                int modifiers = field.getModifiers();
                if (Modifier.isStatic(modifiers) || Modifier.isFinal(modifiers) || Modifier.isNative(modifiers) || field.getAnnotation(Transient.class) != null) {
                    continue;
                }
                String fieldName = field.getName();
                if ("id".equals(fieldName)) {
                    continue;
                }
                String columnName = null;
                Column column = field.getAnnotation(Column.class);
                if (column != null) {
                    columnName = column.name();
                } else {
                    columnName = NamingStrategyUtils.camelCaseToUnderscore(fieldName);
                }
                insertSql.append(columnName);
                insertSql.append(", ");
                valuesSql.append("#{item.").append(fieldName);
                valuesSql.append("}, ");
            }
            domainClass = domainClass.getSuperclass();
        }
        insertSql.deleteCharAt(insertSql.length() - 1);
        insertSql.deleteCharAt(insertSql.length() - 1);
        insertSql.append(") VALUES ");
        valuesSql.deleteCharAt(valuesSql.length() - 1);
        valuesSql.deleteCharAt(valuesSql.length() - 1);
        valuesSql.append(")");
        return new String[]{insertSql.toString(), valuesSql.toString()};
    }

    public static String generateUpdateSql(String domainClassName) {
        return generateUpdateSql(domainClassName, null);
    }

    public static String generateUpdateSql(String domainClassName, String tableName) {
        Class<?> domainClass = null;
        try {
            domainClass = Class.forName(domainClassName);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return generateUpdateSql(domainClass, tableName);
    }

    public static String generateUpdateSql(Class<?> domainClass) {
        return generateUpdateSql(domainClass, null);
    }

    public static String generateUpdateSql(Class<?> domainClass, String tableName) {
        StringBuilder updateSql = new StringBuilder("UPDATE ");
        updateSql.append(obtainTableName(tableName, domainClass));
        updateSql.append(" SET ");
        while (domainClass != Object.class) {
            Field[] fields = domainClass.getDeclaredFields();
            for (Field field : fields) {
                int modifiers = field.getModifiers();
                if (Modifier.isStatic(modifiers) || Modifier.isFinal(modifiers) || Modifier.isNative(modifiers) || field.getAnnotation(Transient.class) != null) {
                    continue;
                }
                String fieldName = field.getName();
                if ("id".equals(fieldName)) {
                    continue;
                }
                String columnName = null;
                Column column = field.getAnnotation(Column.class);
                if (column != null) {
                    columnName = column.name();
                } else {
                    columnName = NamingStrategyUtils.camelCaseToUnderscore(fieldName);
                }
                updateSql.append(columnName);
                updateSql.append(" = ");
                updateSql.append("#{");
                updateSql.append(fieldName);
                updateSql.append("}, ");
            }
            domainClass = domainClass.getSuperclass();
        }
        updateSql.deleteCharAt(updateSql.length() - 1);
        updateSql.deleteCharAt(updateSql.length() - 1);
        updateSql.append(" WHERE id = #{id}");
        return updateSql.toString();
    }

    public static List<String> obtainAllAlias(Class<?> domainClass) {
        List<String> alias = new ArrayList<String>();
        while (domainClass != Object.class) {
            Field[] fields = domainClass.getDeclaredFields();
            for (Field field : fields) {
                int modifiers = field.getModifiers();
                if (Modifier.isStatic(modifiers) || Modifier.isFinal(modifiers) || Modifier.isNative(modifiers) || field.getAnnotation(Transient.class) != null) {
                    continue;
                }
                String fieldName = field.getName();

                String underscoreName = NamingStrategyUtils.camelCaseToUnderscore(fieldName);
                String columnName = null;
                Column column = field.getAnnotation(Column.class);
                if (column != null) {
                    columnName = column.name();
                    alias.add("`" + underscoreName + "` AS `" + columnName + "`");
                } else {
                    alias.add("`" + underscoreName + "`");
                }
            }
            domainClass = domainClass.getSuperclass();
        }
        return alias;
    }

    public static String obtainTableName(String tableName, Class<?> clazz) {
        if (StringUtils.isNotBlank(tableName)) {
            return tableName;
        }
        Table table = AnnotationUtils.findAnnotation(clazz, Table.class);
        if (table != null) {
            return table.name();
        } else {
            String simpleName = clazz.getSimpleName();
            return NamingStrategyUtils.camelCaseToUnderscore(simpleName.substring(0, 1).toLowerCase() + simpleName.substring(1));
        }
    }

    public static String obtainWhereClause(String sqlFragment) {
        return sqlFragment.replaceAll("#\\{", "#{namedParameters.");
    }
}
