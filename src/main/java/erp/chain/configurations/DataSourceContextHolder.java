package erp.chain.configurations;

public class DataSourceContextHolder {
    private static final ThreadLocal<String> DATA_SOURCE_CONTEXT_HOLDER = new ThreadLocal<String>();

    public static void setDataSourceType(String dataSourceType) {
        DATA_SOURCE_CONTEXT_HOLDER.set(dataSourceType);
    }

    public static String getDataSourceType() {
        return DATA_SOURCE_CONTEXT_HOLDER.get();
    }

    public static void clearDataSourceType() {
        DATA_SOURCE_CONTEXT_HOLDER.remove();
    }
}
